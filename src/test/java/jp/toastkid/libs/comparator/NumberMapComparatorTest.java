package jp.toastkid.libs.comparator;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.collections.impl.factory.Maps;
import org.junit.Test;

/**
 * {@link NumberMapComparator}'s test case.
 * @author Toast kid
 *
 */
public class NumberMapComparatorTest {

    /**
     * check {@link NumberMapComparator#compare(String, String)}.
     */
    @Test
    public final void testCompare() {
        final Map<String, Integer> map = Maps.fixedSize.of(
                "tomato", 120,
                "orange", 100,
                "apple",  130
                );
        final NumberMapComparator comparator = new NumberMapComparator(map);
        assertEquals(0,  comparator.compare("tomato", "tomato"));
        assertEquals(-1, comparator.compare("tomato", "orange"));
        assertEquals(1,  comparator.compare("tomato", "apple"));
        assertEquals(1,  comparator.compare("orange", "tomato"));

        final TreeMap<String, Integer> treeMap = new TreeMap<>(comparator);
        treeMap.putAll(map);
        assertEquals("{apple=130, tomato=120, orange=100}", treeMap.toString());
    }

}
