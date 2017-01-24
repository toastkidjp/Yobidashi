package jp.toastkid.chart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.stage.Stage;
import jp.toastkid.chart.ChartPane.Category;

/**
 * {@link ChartPane}'s test cases.
 *
 * @author Toast kid
 *
 */
public class ChartPaneTest extends ApplicationTest {

    /**
     * {@link ChartPane.Category}'s test.
     */
    @Test
    public void testCategory() {
        final Category diary = Category.DIARY;
        assertEquals("DIARY", diary.name());
        assertEquals("日記の文字数", diary.text());
        assertEquals(3_000, diary.threshold());
    }

    /**
     * {@link ChartPane.Category#findByText(String)}'s test.
     */
    @Test
    public void testCategory_findByText() {
        final Category diary = Category.findByText("日記の文字数");
        assertSame(Category.DIARY, diary);
        assertEquals("DIARY", diary.name());
        assertEquals("日記の文字数", diary.text());
        assertEquals(3_000, diary.threshold());
    }

    /**
     * {@link ChartPane.Category}'s test.
     */
    @Test
    public void testCategoryvalueOf() {
        assertSame(Category.DIARY, Category.valueOf("DIARY"));
    }

    /**
     * {@link ChartPane.Category}'s test.
     */
    @Test
    public void testCategoryvalues() {
        assertNotNull(Category.values());
    }

    /**
     * {@link ChartPane#make(java.lang.String, java.lang.String, java.lang.String)}'s test method.
     * @throws URISyntaxException
     */
    @Test
    public void testMake() throws URISyntaxException {
        ChartPane.make(
                Paths.get(getClass().getClassLoader().getResource("chart").toURI()).toString(),
                ChartPane.Category.OUTGO,
                "日記2017-01"
                );
    }

    /**
     * {@link ChartPane#make(java.lang.String, java.lang.String, java.lang.String)}'s test method.
     * @throws URISyntaxException
     */
    @Test
    public void testMake_in_empty_table_case() throws URISyntaxException {
        ChartPane.make(
                Paths.get(getClass().getClassLoader().getResource("chart").toURI()).toString(),
                ChartPane.Category.DIARY,
                "日記2017-01"
                );
    }

    /**
     * {@link ChartPane#make(java.lang.String, java.lang.String, java.lang.String)}'s test method.
     * @throws URISyntaxException
     */
    @Test
    public void testMake_nikkei_225() throws URISyntaxException {
        ChartPane.make(
                Paths.get(getClass().getClassLoader().getResource("chart").toURI()).toString(),
                ChartPane.Category.NIKKEI225,
                "日記2017-01"
                );
    }

    /**
     * {@link ChartPane#make(java.lang.String, java.lang.String, java.lang.String)}'s test method.
     * @throws URISyntaxException
     */
    @Test(expected=StringIndexOutOfBoundsException.class)
    public void testMake_empty() throws URISyntaxException {
        ChartPane.make(
                Paths.get(getClass().getClassLoader().getResource("chart").toURI()).toString(),
                ChartPane.Category.NIKKEI225,
                ""
                );
    }

    /**
     * {@link ChartPane#make(java.lang.String, java.lang.String, java.lang.String)}'s test method.
     * @throws URISyntaxException
     */
    @Test(expected=IllegalArgumentException.class)
    public void testMake_IllegalArgumentException() throws URISyntaxException {
        ChartPane.make(
                Paths.get(getClass().getClassLoader().getResource("chart").toURI()).toString(),
                null,
                ""
                );
    }

    /**
     * {@link ChartPane#readMonths()}'s test.
     */
    @Test
    public void test_readMonths() {
        final List<String> months = ChartPane.readMonths();
        assertEquals("2011-02", months.get(0));
        assertEquals(
                DateTimeFormatter.ofPattern("yyyy-MM").format(LocalDate.now()),
                months.get(months.size() - 1)
                );
    }

    @Override
    public void start(Stage stage) throws Exception {
        // NOP.
    }

}
