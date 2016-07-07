package jp.toastkid.gui.jfx.wiki.name;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jp.toastkid.libs.utils.FileUtil;

public class Convertor {
    private static final String LINE_SEPARATOR = System.lineSeparator();
    private static MutableList<NameInformation> firstNames  = Lists.mutable.empty();
    private static MutableList<NameInformation> familyNames = Lists.mutable.empty();

    public static void main(final String[] args) {
        final int numOfTasks = 2;
        final CountDownLatch latch = new CountDownLatch(numOfTasks);
        final ExecutorService es = Executors.newFixedThreadPool(numOfTasks);
        es.execute(() -> {
            final NameLoader loader = new NameLoader(new File("firstName.xml"));
            firstNames = Lists.mutable.ofAll(loader.call())
                    .sortThis().distinct();
            latch.countDown();
        });
        es.execute(() -> {
            final NameLoader loader = new NameLoader(new File("familyName.xml"));
            familyNames = Lists.mutable.ofAll(loader.call())
                    .sortThis().distinct();
            latch.countDown();
        });

        try {
            latch.await();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }
        es.shutdown();

        final ObjectMapper mapper = new ObjectMapper();

        FileUtil.outPutStr(
                firstNames.collect(name -> {
                    try {
                        return mapper.writeValueAsString(name);
                    } catch (final JsonProcessingException e) {
                        e.printStackTrace();
                        return "";
                    }
                })
                .makeString(LINE_SEPARATOR), "first.txt", "UTF-8");
        FileUtil.outPutStr(
                familyNames.collect(name -> {
                    try {
                        return mapper.writeValueAsString(name);
                    } catch (final JsonProcessingException e) {
                        e.printStackTrace();
                        return "";
                    }
                })
                .makeString(LINE_SEPARATOR), "family.txt", "UTF-8");
    }
}
