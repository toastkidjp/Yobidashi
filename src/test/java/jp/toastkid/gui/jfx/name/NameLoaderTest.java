package jp.toastkid.gui.jfx.name;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.junit.Test;

import jp.toastkid.gui.jfx.name.NameInformation;
import jp.toastkid.gui.jfx.name.NameLoader;
import jp.toastkid.gui.jfx.wiki.models.Defines;

/**
 * {@link NameLoader}'s test.
 * @author Toast kid
 *
 */
public class NameLoaderTest {

    /** path/to/firstname/file. */
    private static final String FIRST_NAME_FILE
        = Defines.ASSETS_DIR + "/resources/NameMaker/first.txt";

    /** path/to/familyname/file. */
    private static final String FAMILY_NAME_FILE
        = Defines.ASSETS_DIR + "/resources/NameMaker/family.txt";

    /** file から読み込んだ国籍を格納する. */
    private volatile MutableList<String> nationalities;

    /** file から読み込んだファーストネームを格納する. */
    private MutableList<NameInformation> firstNames;

    /** file から読み込んだファミリーネームを格納する. */
    private MutableList<NameInformation> familyNames;

    @Test
    public void test() {
        final long start = System.currentTimeMillis();
        nationalities = Lists.mutable.empty();

        final int numOfTasks = 2;
        final CountDownLatch latch = new CountDownLatch(numOfTasks);
        final ExecutorService es = Executors.newFixedThreadPool(numOfTasks);
        es.execute(() -> {
            final NameLoader loader = new NameLoader(new File(FIRST_NAME_FILE));
            firstNames = Lists.mutable.ofAll(loader.call())
                    .sortThis().distinct();
            nationalities.addAll(loader.getNationalities());
            latch.countDown();
        });
        es.execute(() -> {
            final NameLoader loader = new NameLoader(new File(FAMILY_NAME_FILE));
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
        System.out.println(System.currentTimeMillis() - start + "[ms]");
    }

    /**
     * check {@link NameLoader#getNationalities()}.
     */
    @Test
    public void testGetNationalities() {
        assertTrue(new NameLoader(new File("")).getNationalities().isEmpty());
    }
}
