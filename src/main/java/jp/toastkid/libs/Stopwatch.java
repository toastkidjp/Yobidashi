package jp.toastkid.libs;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Simple Stopwatch.
 *
 * @author Toast kid
 *
 */
public class Stopwatch {

    /** Map. */
    private final Map<String, Long> map;

    /**
     * Initialize map.
     */
    public Stopwatch() {
        map = new LinkedHashMap<>(10);
    }

    /**
     * Initialize map with initial capacity.
     * @param initialCapacity
     */
    public Stopwatch(final int initialCapacity) {
        map = new LinkedHashMap<>(initialCapacity);
    }

    /**
     * Add entry.
     * @param key
     */
    public void add(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("key allow not null.");
        }
        map.put(key, System.currentTimeMillis());
    }

    /**
     * Get elapsed ms.
     * @param key
     * @return
     */
    public long get(final String key) {
        if (key == null) {
            throw new IllegalArgumentException("key allow not null.");
        }
        if (!map.containsKey(key)) {
            return System.currentTimeMillis();
        }
        return System.currentTimeMillis() - map.get(key).longValue();
    }

    /**
     * Get all entries.
     * @return Set&lt;Entry&lt;String, Long&gt;&gt;
     */
    public Set<Entry<String, Long>> getEntries() {
        return map.entrySet();
    }

    /**
     * Print all entries.
     */
    public void print() {
        getEntries().stream().map(this::format).forEach(System.out::println);
    }

    /**
     * Format entry.
     * @param entry
     * @return formatted text.
     */
    private String format(final Entry<String, Long> entry) {
        final String key = entry.getKey();
        return String.format("%s=%d[ms]", key, get(key));
    }

}
