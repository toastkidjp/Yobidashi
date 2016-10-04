package jp.toastkid.libs.epub;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.api.list.ImmutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Maps;

import com.fasterxml.jackson.databind.ObjectMapper;

import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.wiki.ArticleGenerator;
import jp.toastkid.wiki.lib.WikiConverter;
import jp.toastkid.wiki.models.Article;
import jp.toastkid.wiki.models.Config;
import jp.toastkid.wiki.models.Defines;

/**
 * 記事を epub に変換して出力する.
 * @author Toast kid
 *
 */
public final class DocToEpub {

    /** path/to/articles. */
    private static final String ARTICLE_PATH = Config.get("articleDir");

    /** ファイル名の制限. */
    public static final int FILE_NAME_LENGTH = 50;

    /** 記事名一覧. */
    private static final ImmutableList<File> ARTICLES
        = Lists.immutable.of(new File(ARTICLE_PATH).listFiles());

    /**
     * ひとりWiki のハイパーリンクを再現するための検出用正規表現.
     * ひとりWiki プラグインとの互換性を持たせるためのもの
     */
    private static final Pattern HYPERLINK_PAT = Pattern.compile(
            "\\[\\[(.+?)\\]\\]",
            Pattern.DOTALL
            );

    /** 削除対象ファイルのパス一覧. */
    private static List<String> cleanTargets = new ArrayList<String>();

    /**
     * run generator.
     * @param fileNames names of json file
     */
    public static void run(final String[] fileNames) {
        run(Lists.immutable.of(fileNames));
    }

    /**
     * run generator.
     * @param fileNames names of json file
     */
    public static void run(final Iterable<String> args) {
        args.forEach(json -> generate(FileUtil.getStrFromFile(json, Defines.ARTICLE_ENCODE)));
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
        try {
            final EpubMetaData meta = new ObjectMapper().readValue(
                    json,
                    EpubMetaData.class
                    );
            /*
             * 処理の流れ
             * プレフィックス回収
             * 指定回収
             * 指定静的コンテンツ回収
             */
            final EpubMaker eMaker = new EpubMaker(meta);
            final List<ContentMetaData> targetContents = new ArrayList<ContentMetaData>();
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
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * epub に入れる記事を前方一致で選択する.
     * @param prefix 記事の接頭辞
     * @return 記事ファイルオブジェクトの一覧
     */
    private static final List<File> selectTargetsByPrefix(
            final String  prefix,
            final boolean recursive
        ) {
        final List<File> targets = ARTICLES.asParallel(Executors.newFixedThreadPool(20), 20)
                .select(file -> file.getName().startsWith(ArticleGenerator.toBytedString_EUC_JP(prefix)))
                .toList();
        if (recursive) {
            final List<File> recursiveFiles = new ArrayList<File>();
            targets.forEach(file ->{
                recursiveFiles.add(file);
                final List<String> contents = FileUtil.readLines(
                        file.getAbsolutePath(),
                        Defines.ARTICLE_ENCODE
                    );
                contents.stream()
                    .filter(content -> content.contains("[[") && content.contains("]]"))
                    .map(   content -> HYPERLINK_PAT.matcher(content))
                    .forEach(matcher -> {
                        while (matcher.find()) {
                            final File f = new File(
                                    ARTICLE_PATH,
                                    ArticleGenerator.toBytedString_EUC_JP(matcher.group(1)).concat(".txt")
                                );
                            if (f.exists() && !recursiveFiles.contains(f)) {
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
    private static final List<File> getTargets(final List<String> targets) {
        final List<File> files = new ArrayList<File>(targets.size());
        targets.parallelStream()
            .map(prefix -> new File(ARTICLE_PATH,
                    ArticleGenerator.toBytedString_EUC_JP(prefix).concat(".txt")))
            .filter(ARTICLES::contains)
            .forEach(f -> files.add(f));
        return files;
    }

    /**
     * 処理対象のコンテンツのパス一覧を返す.
     * @param targets
     * @return 処理対象のコンテンツのパス一覧
     */
    private static final List<ContentMetaData> getTargetContents(
            final List<File> targets,
            final PageLayout layout
            ) {
        final List<ContentMetaData> targetPaths = new ArrayList<ContentMetaData>();
        final WikiConverter converter = new WikiConverter("", ARTICLE_PATH);
        final String imageDir = Config.get("imageDir").replace("\\", "/");
        converter.containsMenubar = false;
        targets.forEach((file) -> {
            final ContentMetaData cmeta = new ContentMetaData();
            final String content = converter.convert(
                        file.getAbsolutePath(),
                        Defines.ARTICLE_ENCODE
                    );
            final String title = Article.convertTitle(file.getName());
            String baseName = file.getName();
            baseName = baseName.substring(0, baseName.length() - 4);
            baseName = (FILE_NAME_LENGTH < baseName.length())
                    ? baseName.substring(0, FILE_NAME_LENGTH)
                    : baseName;
            final String outputName = baseName.concat(EpubDefine.FILE_SUFFIX);
            //System.out.println(title + " - " + outputName);
            final String style = layout.equals(PageLayout.VERTICAL)
                    ? EpubDefine.STYLESHEET_VERTICAL
                    : EpubDefine.STYLESHEET_HORIZONTAL;
            final String convertedSource = ArticleGenerator.bindArgs(
                    Defines.ASSETS_DIR + "/resources/epub/OEBPS/template.xhtml",
                    Maps.mutable.of(
                            "title", title,
                            "content", content.toString(),
                            "stylesheet", style
                            )
            );
            // img
            final Set<String> imgs = converter.latestImagePaths;
            imgs.parallelStream().forEach((path) -> {
                if (new File(imageDir + path).exists()) {
                    final ContentMetaData imgCMeta = new ContentMetaData();
                    final String parentPath
                        = new File(path.replace(
                                FileUtil.FILE_PROTOCOL.concat(imageDir),
                                ""
                            )
                        ).getParent() + "/";
                    imgCMeta.source = (imageDir + path);
                    imgCMeta.entry  = path;
                    imgCMeta.dest   = parentPath.replace("\\", "/");
                    targetPaths.add(imgCMeta);
                }
            });
            cmeta.entry  = outputName;
            cmeta.source = outputName;
            cmeta.title  = title;
            cmeta.dest   = "";
            FileUtil.outPutStr(
                    convertedSource.replace(
                        FileUtil.FILE_PROTOCOL.concat(imageDir),
                        "OEBPS/"
                    ),
                    outputName,
                    Defines.ARTICLE_ENCODE
            );
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
            .map(    path -> new File(path))
            .filter( file -> file.exists())
            .forEach(file -> file.delete());
    }

    /**
     *
     * @param args
     */
    public static final void main(final String[] args) {
        run(args);
    }
}
