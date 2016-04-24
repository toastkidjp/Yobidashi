package jp.toastkid.gui.jfx.wiki.name;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import jp.toastkid.libs.utils.FileUtil;

/**
 * Name generator.
 * @author Toast kid
 *
 */
public class NameGenerator {

    /** encoding. */
    private static final String UTF_8 = "UTF-8";
    /** ファーストネーム XML のパス. */
    private static final String FIRST_NAME_XML  = "public/resources/NameMaker/FirstName.xml";
    /** ファミリーネーム XML のパス. */
    private static final String FAMILY_NAME_XML = "public/resources/NameMaker/FamilyName.xml";

    /** XML から読み込んだ国籍を格納する. */
    private volatile MutableList<String> nationalities;

    /** XML から読み込んだファーストネームを格納する. */
    private MutableList<NameInformation> firstNames;

    /** XML から読み込んだファミリーネームを格納する. */
    private MutableList<NameInformation> familyNames;

    /**
     * Construct object.
     */
    public NameGenerator() {
        nationalities = Lists.mutable.empty();

        final int numOfTasks = 2;
        final CountDownLatch latch = new CountDownLatch(numOfTasks);
        final ExecutorService es = Executors.newFixedThreadPool(numOfTasks);
        es.execute(() -> {
            final NameXmlLoader loader = new NameXmlLoader(new File(FIRST_NAME_XML));
            firstNames = Lists.mutable.ofAll(loader.call())
                    .sortThis().distinct();
            nationalities.addAll(loader.getNationalities());
            latch.countDown();
        });
        es.execute(() -> {
            final NameXmlLoader loader = new NameXmlLoader(new File(FAMILY_NAME_XML));
            familyNames = Lists.mutable.ofAll(loader.call())
                    .sortThis().distinct();
            nationalities.addAll(loader.getNationalities());
            latch.countDown();
        });

        nationalities.distinct().sortThis();
        try {
            latch.await();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        es.shutdown();
    }

    /**
     * XMLファイルを点検する.
     */
    public void checkXml() {
        try (final PrintWriter outPutter = FileUtil.makeFileWriter(FIRST_NAME_XML, UTF_8);) {
            outPutter.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            outPutter.println("<humannames>");
            outPutter.println("");
            for (int i = 0; i < firstNames.size(); i++) {
                outPutter.println("<firstname>");
                final NameInformation firstName  = firstNames.get(i);
                outPutter.println("<name>"        + firstName.getName()        + "</name>"        );
                outPutter.println("<spelling>"    + firstName.getSpelling()    + "</spelling>"    );
                outPutter.println("<nationality>" + firstName.getNationality() + "</nationality>" );
                outPutter.println("<seibetsu>"    + firstName.getSeibetsu()    + "</seibetsu>"    );
                outPutter.println("</firstname>");
                outPutter.println();
            }
            outPutter.println("</humannames>");
            outPutter.flush();
            outPutter.close();
        }

        // familiName の処理
        try (final PrintWriter outPutter = FileUtil.makeFileWriter(FAMILY_NAME_XML, UTF_8);) {
            outPutter.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            outPutter.println("<humannames>");
            outPutter.println();
            for (int i = 0; i < familyNames.size(); i++) {
                outPutter.println("<familyname>");
                final NameInformation familyName  = familyNames.get(i);
                outPutter.println("<name>"+familyName.getName()+"</name>");
                outPutter.println("<spelling>"+familyName.getSpelling()+"</spelling>");
                outPutter.println("<nationality>"+familyName.getNationality()+"</nationality>");
                outPutter.println("</familyname>");
                outPutter.println();
            }
            outPutter.println("</humannames>");
            outPutter.flush();
            outPutter.close();
        }
    }

    /**
     * 名前を生成する
     * @param nationality 国籍は？
     * @param neededValue いくつ生成するか？
     */
    public String generate(final String nationality, final int neededValue) {
        int x = 0;
        int y = 0;
        NameInformation firstName;
        NameInformation secondName;

        final String lineSeparator = System.lineSeparator();
        final StringBuilder names = new StringBuilder();
        if ("指定しない".equals(nationality) ) {
            for (int i = 0; i < neededValue; i++) {
                x = (int)(Math.random() * firstNames.size());
                y = (int)(Math.random() * familyNames.size());
                firstName  = firstNames.get(x);
                secondName = familyNames.get(y);
                names.append(firstName.getSeibetsu()).append(" , ").append(firstName.getName())
                     .append("・").append(secondName.getName()).append(" , ")
                     .append(firstName.getSpelling()).append(" ").append(secondName.getSpelling())
                     .append(lineSeparator);
            }
        } else {
            for (int i = 0; i < neededValue; i++) {
                x = (int)(Math.random() * firstNames.size());
                y = (int)(Math.random() * familyNames.size());
                firstName  = firstNames.get(x);
                secondName = familyNames.get(y);
                while ( !nationality.equals(firstName.getNationality()) ) {
                    x = (int)(Math.random() * firstNames.size());
                    firstName  = firstNames.get(x);
                }
                while ( !nationality.equals(secondName.getNationality()) ) {
                    x = (int)(Math.random()*familyNames.size());
                    secondName  = familyNames.get(x);
                }
                names.append(firstName.getSeibetsu()).append(" , ").append(firstName.getName())
                     .append("・").append(secondName.getName()).append(" , ")
                     .append(firstName.getSpelling()).append(" ").append(secondName.getSpelling())
                     .append(lineSeparator);
            }
        }
        return names.toString();
    }

    /**
     * 国籍一覧を返す.
     */
    public Collection<String> getNationalities() {
        return nationalities.distinct().reject(str -> {return str == null;}).sortThis();
    }
}
