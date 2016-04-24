package jp.toastkid.libs.epub;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.toastkid.gui.jfx.wiki.models.Article;
import jp.toastkid.gui.jfx.wiki.models.Defines;
import jp.toastkid.libs.utils.FileUtil;
import jp.toastkid.libs.utils.Strings;
import jp.toastkid.libs.wiki.WikiConverter;

public class ReviewToEpub {
    /** 処理対象フォルダ */
    private static final String targetDirPath = "D:/MyWiki/MyWikiData";
    /** 変換対象文字列(プレフィクス)、出力フォルダ名にもなる */
    private static final String prefix = "日記";
    private static String articleDirPath;
    public static void main(String[] args) {
        final File[] files = new File(targetDirPath).listFiles();
        final int length = files.length;
        final Map<String, List<String>> map = new HashMap<>();
        for (int i = 0; i < length; i++){
            if (!files[i].isDirectory()) {
                final String title
                    = Article.convertTitle(files[i].getName());
                if (title.startsWith(prefix )) {
                    map.putAll(extract(files[i].getAbsolutePath()));;
                    System.out.println(
                            "Now processing (" + (i + 1) + " / " + length + ") ... " + title
                            );
                }
            }
        }
        //System.out.println(extract.get("レバレッジ"));
        //System.out.println(extract.keySet().toString());
        FileUtil.mkdir("reviews");
        final WikiConverter converter = new WikiConverter("", articleDirPath);
        converter.containsMenubar = false;
        //for (final String key : map.keySet()) {

            /*final ContentMetaData cmeta = new ContentMetaData();
            final String content = converter.getConvertedTXT(
                        file.getAbsolutePath(),
                        WikiClientDefine.ARTICLE_ENCODE
                    );
            final String title = WikiClientDefine.convertTitle(file.getName());
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
            final String convertedSource = FileUtil.bindArgsToTemplate(
                    "public/resources/epub/OEBPS/template.xhtml",
                    new HashMap(){{
                        put("title",   title);
                        put("content", content.toString());
                        put("stylesheet", style);
                    }}
            );
            cmeta.entry  = outputName;
            cmeta.source = outputName;
            cmeta.title  = title;
            cmeta.dest   = "";
            FileUtil.outPutStr(
                    convertedSource.replace(
                        FileUtil.FILE_PROTOCOL.concat(conf.imageDir),
                        "OEBPS/"
                    ),
                    outputName,
                    WikiClientDefine.ARTICLE_ENCODE
            );
            targetPaths.add(cmeta);
            cleanTargets.add(outputName);

            FileUtil.outPutStr(
                    content.toString(),
                    prefix + "/" + key.replaceAll(StringUtil.getDirSeparator(), "-") + ".txt",
                    WikiClientDefine.ARTICLE_ENCODE
            );
            content.setLength(0);*/
        //}

        /*
        EpubMetaData meta = new EpubMetaData();
        meta.targets
        DocToEpub.run(json);
        //*/
    }

    /**
     * ファイル単位での抜粋をする。
     * @param absolutePath 抜粋対象のファイルパス
     */
    private static Map<String, List<String>> extract(final String absolutePath) {
        final Map<String, List<String>> result = new HashMap<String, List<String>>();
        final List<String> list = FileUtil.readLines(
                absolutePath,
                Defines.ARTICLE_ENCODE
                );
        final StringBuilder content = new StringBuilder();
        String title = null;
        final String date  = Article.convertTitle(
                new File(absolutePath).getName()
                );
        for (int i = 0; i < list.size(); i++) {
            final String str = list.get(i);
            if (str.startsWith("** ")) {
                List<String> contents = new ArrayList<String>();
                if (title != null && result.containsKey(title)) {
                    contents = result.get(title);
                }
                contents.add(content.toString());
                result.put(title, contents);
                title    = null;
                content.setLength(0);
                if (str.contains("『")) {
                    final String[] split = str
                            .replaceFirst("\\*\\*.*『", "")
                            .trim()
                            .split("』");
                    if (1 < split.length) {
                        title = split[0];
                    }
                }
            }
            if ("----".equals(str)) {
                List<String> contents = new ArrayList<String>();
                if (title != null && result.containsKey(title)) {
                    contents = result.get(title);
                }
                contents.add(content.toString());
                result.put(title, contents);
                title    = null;
                content.setLength(0);
            }
            if (title != null) {
                if (content.length() == 0) {
                    content.append(str);
                    content.append(Strings.LINE_SEPARATOR);
                    content.append(date);
                    content.append(Strings.LINE_SEPARATOR);
                    content.append(Strings.LINE_SEPARATOR);
                } else {
                    content.append(str);
                }
                content.append(Strings.LINE_SEPARATOR);
            }
            if (i == list.size() - 1) {
                List<String> contents = new ArrayList<String>();
                if (title != null && result.containsKey(title)) {
                    contents = result.get(title);
                }
                contents.add(content.toString());
                result.put(title, contents);
                title = null;
                content.setLength(0);
            }
        }
        return result;
    }
}
