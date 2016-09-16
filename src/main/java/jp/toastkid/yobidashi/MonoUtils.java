package jp.toastkid.yobidashi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class MonoUtils {

    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(MonoUtils.class);

    public static void doElastic(Runnable command) {
        Mono.create(emitter -> {
            command.run();
            emitter.success();
            LOGGER.info("ENDED TASK.");
        }).subscribeOn(Schedulers.elastic()).subscribe();
    }
}
