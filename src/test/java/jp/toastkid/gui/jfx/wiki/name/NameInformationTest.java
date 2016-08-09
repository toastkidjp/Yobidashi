package jp.toastkid.gui.jfx.wiki.name;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import jp.toastkid.gui.jfx.name.NameInformation;

/**
 * {@link NameInformation}'s test case.
 * @author Toast kid
 *
 */
public class NameInformationTest {

    /**
     * check same object.
     */
    @Test
    public void test() {
        assertSame(NameInformation.empty(), NameInformation.empty());
    }

    /**
     * check {@link NameInformation.Builder}.
     */
    @Test
    public void testMakeInstanceWithBuilder() {
        final NameInformation name = new NameInformation.Builder().setName("トマト")
                .setNationality("UK").setSeibetsu("M").setSpelling("Tomato").build();
        assertEquals("トマト", name.getName());
        assertEquals("UK", name.getNationality());
        assertEquals("M", name.getSeibetsu());
        assertEquals("Tomato", name.getSpelling());
    }

    /**
     * check {@link NameInformation#hashCode()}.
     */
    @Test
    public void testHashCode() {
        assertEquals(new NameInformation().hashCode(), NameInformation.empty().hashCode());
    }

    /**
     * check {@link NameInformation#equals()}.
     */
    @Test
    public void testEquals() {
        assertEquals(NameInformation.empty(), NameInformation.empty());
        assertEquals(new NameInformation(), NameInformation.empty());
    }
}
