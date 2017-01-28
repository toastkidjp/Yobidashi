package jp.toastkid.libs.classify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link BayesianClassifier}'s test.
 *
 * @author Toast kid
 *
 */
public class BayesianClassifierTest {

    /** Classifier. */
    private BayesianClassifier classifier;

    /**
     * Set up test object.
     * @throws IOException
     * @throws URISyntaxException
     */
    @Before
    public void setUp() throws IOException, URISyntaxException {
        final Path path = Paths.get(getClass().getClassLoader()
                .getResource("libs/classify/learn.txt").toURI());
        final List<String> lines = Files.readAllLines(path);
        classifier = new BayesianClassifier(lines);
    }

    /**
     * Check of {@link BayesianClassifier#trial(String)}.
     */
    @Test
    public void testTrial() {
        assertTrue(classifier.trial("味噌ラーメン"));
        assertTrue(classifier.trial("冷し中華"));
        assertTrue(classifier.trial("台湾ラーメン"));
        assertFalse(classifier.trial("ベジそば"));
        assertTrue(classifier.trial("タンタンタンメン"));
    }

    /**
     * Check of {@link BayesianClassifier#trialRetVal(String)}.
     */
    @Test
    public void testTrialRetVal() {
        assertEquals(2.8720948652545617E-6d, classifier.trialRetVal("味噌ラーメン"),      0.001d);
        assertEquals(3.046966817080427E-4d,  classifier.trialRetVal("冷し中華"),          0.001d);
        assertEquals(2.8720948652545617E-6d, classifier.trialRetVal("台湾ラーメン"),      0.001d);
        assertEquals(1.596030237518319E-4d,   classifier.trialRetVal("ベジそば"),         0.001d);
        assertEquals(2.638216880299886E-6d,   classifier.trialRetVal("タンタンタンメン"), 0.001d);
    }

}
