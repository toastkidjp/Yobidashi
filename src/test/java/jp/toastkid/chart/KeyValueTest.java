package jp.toastkid.chart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link KeyValue}'s test cases.
 *
 * @author Toast kid
 *
 */
public class KeyValueTest {

    /** Test object. */
    private KeyValue kv;

    /**
     * Initialize test object.
     */
    @Before
    public void setUp() {
        kv = makeSample();
    }

    /**
     * Make sample object.
     * @return
     */
    private KeyValue makeSample() {
        return new KeyValue.Builder().setKey("key").setMiddle("middle").setValue(10L).build();
    }

    /**
     * {@link jp.toastkid.chart.KeyValue#hashCode()} 's test method.
     */
    @Test
    public void testHashCode() {
        System.out.println(kv.hashCode());
    }

    /**
     * {@link jp.toastkid.chart.KeyValue#keyProperty()} 's test method.
     */
    @Test
    public void testKeyProperty() {
        assertEquals("key", kv.keyProperty().get());
    }

    /**
     * {@link jp.toastkid.chart.KeyValue#middleProperty()} 's test method.
     */
    @Test
    public void testMiddleProperty() {
        assertEquals("middle", kv.middleProperty().get());
    }

    /**
     * {@link jp.toastkid.chart.KeyValue#valueProperty()} 's test method.
     */
    @Test
    public void testValueProperty() {
        assertEquals(10L, kv.valueProperty().get());
    }

    /**
     * {@link jp.toastkid.chart.KeyValue#toString()} 's test method.
     */
    @Test
    public void testToString() {
        assertNotNull(kv.toString());
    }

    /**
     * {@link jp.toastkid.chart.KeyValue#equals(java.lang.Object)} 's test method.
     */
    @Test
    public void testEqualsObject() {
        assertNotSame(kv, makeSample());
    }

}
