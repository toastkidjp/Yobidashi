package jp.toastkid.libs;

import java.util.ArrayList;
import java.util.Collection;

import net.arnx.jsonic.JSON;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;


/**
 * Two values pair.
 *
 * @author Toast kid
 *
 * @param <T1>
 * @param <T2>
 */
public final class Pair<T1, T2> {
    /** left value. */
    public final T1 left;
    /** right value. */
    public final T2 right;

    /**
     * public constructor.
     * @param t1
     * @param t2
     */
    public Pair(
            final T1 left,
            final T2 right
        ) {
        this.left  = left;
        this.right = right;
    }

    /**
     * initialize of two values.
     * @param t1
     * @param t2
     */
    public static <T1, T2> Pair<T1, T2> of(
            final T1 t1,
            final T2 t2
        ) {
        return new Pair<T1, T2>(t1, t2);
    }

    /**
     * initialize with two values.
     * @param t1
     * @param t2
     */
    public static <T1, T2> Pair<T1, T2> with(
            final T1 t1,
            final T2 t2
        ) {
        return new Pair<T1, T2>(t1, t2);
    }

    @Override
    public boolean equals(final Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return JSON.encode(this);
    }

    /**
     * Pair の左側だけを集めた List を返す。
     * @param pairs
     * @return Pair の左側だけを集めた List
     */
    public static final <T1, T2> Collection<T1> lefts(final Collection<Pair<T1, T2>> pairs) {
        final Collection<T1> lefts = new ArrayList<T1>();
        pairs.stream().forEach((pair) -> {
            lefts.add(pair.left);
        });
        return lefts;
    }
    /**
     * Pair の右側だけを集めた List を返す。
     * @param pairs
     * @return Pair の右側だけを集めた List
     */
    public static final <T1, T2> Collection<T2> rights(final Collection<Pair<T1, T2>> pairs) {
        final Collection<T2> rights = new ArrayList<T2>();
        pairs.stream().forEach((pair) -> {
            rights.add(pair.right);
        });
        return rights;
    }

}
