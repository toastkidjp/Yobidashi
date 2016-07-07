package jp.toastkid.gui.jfx.wiki.name;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.factory.Sets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * Read nameInformation from passed XML.
 * @author Toast kid
 *
 */
public class NameLoader implements Callable<Collection<NameInformation>>{

    private static final ThreadLocal<ObjectReader> READER
        = ThreadLocal.withInitial(() -> new ObjectMapper().readerFor(NameInformation.class));

    private final Collection<NameInformation> names;

    private final Collection<String> nationalities;

    private final File targetFile;

    /**
     *
     * @param file
     */
    public NameLoader(final File file) {
        this.targetFile = file;
        nationalities = Sets.mutable.empty();
        names         = Lists.mutable.empty();
    }

    @Override
    public Collection<NameInformation> call() {
        try (final BufferedReader fileReader = Files.newBufferedReader(Paths.get(targetFile.toURI()))) {
            final Flux<NameInformation> cache
                = Flux.<NameInformation>create(emitter -> fileReader.lines().forEach(str -> {
                    try {
                        emitter.next(READER.get().readValue(str));
                    } catch (final Exception e) {
                        emitter.fail(e);
                    }
                })).cache();
            cache.subscribeOn(Schedulers.elastic())
                .subscribe(names::add);
            cache.subscribeOn(Schedulers.elastic())
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
