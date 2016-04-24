package jp.toastkid.libs.hash;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * ConsistentHashの実装.
 * @author Toast kid
 *
 * @param <T>
 * @see <a href="https://weblogs.java.net/blog/tomwhite/archive/2007/11/consistent_hash.html">Consistent Hash</a>
 */
public class ConsistentHash<T> {
    /** 任意のHash関数. */
    private final HashFunction hashFunction;
    /** レプリケーション数. */
    private final int numOfReplicas;
    /** サークル. */
    private final SortedMap<Long, T> circle = new TreeMap<Long, T>();
    /**
     *
     * @param hashFunction
     * @param numberOfReplicas
     * @param nodes
     */
    public ConsistentHash(HashFunction hashFunction, int numberOfReplicas,
            Collection<T> nodes) {
        this.hashFunction = hashFunction;
        this.numOfReplicas = numberOfReplicas;

        for (final T node : nodes) {
            add(node);
        }
    }

    public void add(T node) {
        for (int i = 0; i < numOfReplicas; i++) {
            circle.put(hashFunction.hash(node.toString() + i), node);
        }
    }

    public void remove(T node) {
        for (int i = 0; i < numOfReplicas; i++) {
            circle.remove(hashFunction.hash(node.toString() + i));
        }
    }

    public T get(Object key) {
        if (circle.isEmpty()) {
            return null;
        }
        long hash = hashFunction.hash(key);
        if (!circle.containsKey(hash)) {
            final SortedMap<Long, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

}
