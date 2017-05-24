package jp.toastkid.libs.epub;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jp.toastkid.article.converter.MarkdownConverter;
import jp.toastkid.article.models.Article;
import jp.toastkid.article.models.Articles;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.yobidashi.models.Config;
import jp.toastkid.yobidashi.models.Config.Key;
import jp.toastkid.yobidashi.models.Defines;

/**
 * 記事を epub に変換して出力する.
 *
 * @author Toast kid
 */
public final class DocToEpub {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DocToEpub.class);

    /** path/to/articles. */
    private static String articlePath;

    /** ファイル名の制限. */
    public static final int FILE_NAME_LENGTH = 50;

    private static Config conf;

    /** 記事名一覧. */
    private static List<Path> articles;
    static {
        try {
            conf = new Config(Defines.CONFIG);
            articlePath = conf.get(Key.ARTICLE_DIR);
            articles = Files.list(Paths.get(articlePath)).collect(Collectors.toList());
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ひとりWiki のハイパーリンクを再現するための検出用正規表現.
     * ひとりWiki プラグインとの互換性を持たせるためのもの
     */
    private static final Pattern HYPERLINK_PAT = Pattern.compile(
            "\\[\\[(.+?)\\]\\]",
            Pattern.DOTALL
            );

    /** 削除対象ファイルのパス一覧. */
    private static List<String> cleanTargets = new ArrayList<>();

    /**
     * run generator.
     * @param fileNames names of json file
     */
    public static void run(final String[] fileNames) {
        run(Arrays.asList(fileNames));
    }

    /**
     * run generator.
     * @param fileNames names of json file
     */
    public static void run(final Iterable<String> args) {
        args.forEach(json -> {
			try {
				run(Files.readAllLines(Paths.get(json))
				        .stream()
				        .collect(Collectors.joining(Strings.LINE_SEPARATOR)));
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
        clean();
    }

    /**
     * 1ファイルのみのePub生成メソッド.
     * @param json jsonで定義したレシピ
     */
    public static void run(final String json) {
        generate(json);
        clean();
    }

    /**
     * epubを1つ生成する.
     * @param json 設定ファイル
     */
    private static void generate(final String json) {
        final EpubMetaData meta = EpubMetaData.readJson(json);

        /*
         * 処理の流れ
         * プレフィックス回収
         * 指定回収
         * 指定静的コンテンツ回収
         */
        final EpubMaker eMaker = new EpubMaker(meta);
        final List<ContentMetaData> targetContents = new ArrayList<>();
        if (StringUtils.isNotEmpty(meta.targetPrefix)) {
            targetContents.addAll(
                    getTargetContents(selectTargetsByPrefix(meta.targetPrefix, meta.recursive), meta.layout)
                );
        }
        if (meta.targets != null && meta.targets.size() != 0) {
            targetContents.addAll(
                    getTargetContents(getTargets(meta.targets), meta.layout)
                );
        }
        eMaker.setContentPairs(targetContents);
        eMaker.generateEpub();
    }

    /**
     * epub に入れる記事を前方一致で選択する.
     * @param prefix 記事の接頭辞
     * @return 記事ファイルオブジェクトの一覧
     */
    private static final List<Path> selectTargetsByPrefix(
            final String  prefix,
            final boolean recursive
        ) {
        final List<Path> targets = articles.stream()
                .filter(path -> path.getFileName().toString().startsWith(Articles.titleToFileName(prefix)))
                .collect(Collectors.toList());
        if (recursive) {
            final List<Path> recursiveFiles = new ArrayList<>();
            targets.forEach(path ->{
                recursiveFiles.add(path);
                final List<String> contents = FileUtil.readLines(path, Article.ENCODE);
                contents.stream()
                    .filter(content -> content.contains("[[") && content.contains("]]"))
                    .map(   content -> HYPERLINK_PAT.matcher(content))
                    .forEach(matcher -> {
                        while (matcher.find()) {
                            final Path f = Paths.get(
                                    articlePath,
                                    Articles.titleToFileName(matcher.group(1)).concat(".txt")
                                );
                            if (Files.exists(f) && !recursiveFiles.contains(f)) {
                                recursiveFiles.add(f);
                            }
                        }
                    });
            });
            return recursiveFiles;
        }
        return targets;
    }

    /**
     * epub に入れる記事を完全一致で選択する.
     * @param targets 記事名
     * @return 記事ファイルオブジェクトの一覧
     */
    private static final List<Path> getTargets(final List<String> targets) {
        final List<Path> paths = new ArrayList<>(targets.size());
        targets.parallelStream()
            .map(prefix -> Paths.get(articlePath, Articles.titleToFileName(prefix).concat(".txt")))
            .filter(articles::contains)
            .forEach(paths::add);
        return paths;
    }

    /**
     * 処理対象のコンテンツのパス一覧を返す.
     * @param targets
     * @return 処理対象のコンテンツのパス一覧
     */
    private static final List<ContentMetaData> getTargetContents(
            final List<Path> targets,
            final PageLayout layout
            ) {
        final List<ContentMetaData> targetPaths = new ArrayList<>();
        final MarkdownConverter converter = new MarkdownConverter(conf);
        final String imageDir = conf.get(Key.IMAGE_DIR).replace("\\", "/");
        converter.containsMenubar = false;
        targets.forEach(path -> {
            final ContentMetaData cmeta = new ContentMetaData();
            final String content = converter.convert(path, Article.ENCODE);
            final String title = Articles.convertTitle(path.getFileName().toString());
            String baseName = path.getFileName().toString();
            baseName = baseName.substring(0, baseName.length() - 4);
            baseName = (FILE_NAME_LENGTH < baseName.length())
                    ? baseName.substring(0, FILE_NAME_LENGTH)
                    : baseName;
            final String outputName = baseName.concat(EpubDefine.FILE_SUFFIX);
            //System.out.println(title + " - " + outputName);
            final String style = layout.equals(PageLayout.VERTICAL)
                    ? EpubDefine.STYLESHEET_VERTICAL
                    : EpubDefine.STYLESHEET_HORIZONTAL;
            final Map<String, String> map = new HashMap<>();
            map.put("title", title);
            map.put("content", content.toString());
            map.put("stylesheet", style);
            final String convertedSource = Articles.bindArgs(Resource.TEMPLATE, map);
            // img
            final Set<String> imgs = converter.latestImagePaths;
            imgs.parallelStream().filter(p -> Files.exists(Paths.get(imageDir + p))).forEach(p -> {
                final ContentMetaData imgCMeta = new ContentMetaData();
                final String parentPath
                    = Paths.get(p.replace(FileUtil.FILE_PROTOCOL.concat(imageDir), ""))
                           .getParent()
                           .toString() + "/";
                imgCMeta.source = (imageDir + p);
                imgCMeta.entry  = p;
                imgCMeta.dest   = parentPath.replace("\\", "/");
                targetPaths.add(imgCMeta);
            });
            cmeta.entry  = outputName;
            cmeta.source = outputName;
            cmeta.title  = title;
            cmeta.dest   = "";
            try {
                Files.write(
                        Paths.get(outputName),
                        convertedSource.replace(
                                FileUtil.FILE_PROTOCOL.concat(imageDir),
                                "OEBPS/"
                                ).getBytes(StandardCharsets.UTF_8
                        )
                );
            } catch (final IOException e) {
                LOGGER.error("ERROR!", e);
            }
            targetPaths.add(cmeta);
            cleanTargets.add(outputName);
        });
        return targetPaths;
    }

    /**
     * 不要となった生成ファイルを削除する.
     * @param pathList ファイルパスの一覧
     */
    private static final void clean() {
        cleanTargets.parallelStream()
            .map(Paths::get)
            .filter(Files::exists)
            .forEach(path -> {
                try {
                    Files.deleteIfExists(path);
                } catch (final Exception e) {
                    LOGGER.error("Error!", e);
                }
            });
    }

    /**
     *
     * @param args
     */
    public static final void main(final String[] args) {
        run(args);
    }
}
