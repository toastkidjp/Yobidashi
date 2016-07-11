package jp.toastkid.gui.jfx.wiki.name;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Read nameInformation from passed JSON.
 *
 * @author Toast kid
 */
public class NameLoader implements Callable<Collection<NameInformation>>{

    /** ObjectReader's holder. */
    private static final ThreadLocal<ObjectReader> READER
        = ThreadLocal.withInitial(() -> new ObjectMapper().readerFor(NameInformation.class));

    /** Name list. */
    private final Collection<NameInformation> names;

    /** Name nationalities. */
    private final Collection<String> nationalities;

    /** target JSON file. */
    private final File targetFile;

    /**
     *
     * @param file
     */
    public NameLoader(final File file) {
        this.targetFile = file;
        nationalities   = Sets.mutable.empty();
        names           = Lists.mutable.empty();
    }

    @Override
    public Collection<NameInformation> call() {
        try (final BufferedReader fileReader = Files.newBufferedReader(Paths.get(targetFile.toURI()))) {
            final Flux<NameInformation> cache
                = Flux.<NameInformation>create(emitter -> fileReader.lines().forEach(str -> {
                    try {
                        emitter.next(READER.get().readValue(str.getBytes("UTF-8")));
                    } catch (final Exception e) {
                        emitter.fail(e);
                    }
                })).cache();
            cache.subscribeOn(Schedulers.fromExecutor(Executors.newFixedThreadPool(8)))
                .subscribe(names::add);
            cache
                .subscribe(info -> nationalities.add(info.getNationality()));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return names;
    }

    /**
     * get Nationality set.
     * @return
     */
    public Collection<String> getNationalities() {
        return nationalities;
    }

}
