package jp.toastkid.jfx.common.transition;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import javafx.animation.Transition;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import jp.toastkid.libs.utils.Whitebox;

/**
 * {@link SplitterTransitionFactory}'s test.
 *
 * @author Toast kid
 *
 */
public class SplitterTransitionFactoryTest extends ApplicationTest {

    /**
     * Failure case of {@link SplitterTransitionFactory#makeHorizontalSlide(SplitPane, double, double)}.
     */
    @Test(expected=ArrayIndexOutOfBoundsException.class)
    public void testFailure() {
        SplitterTransitionFactory.makeHorizontalSlide(new SplitPane(), 1.0, 1.0);
    }

    /**
     * Open case of {@link SplitterTransitionFactory#makeHorizontalSlide(SplitPane, double, double)}.
     */
    @Test
    public void testMakeHorizontalSlide_closing() {
        final Transition transition
            = SplitterTransitionFactory.makeHorizontalSlide(new SplitPane(new HBox(), new HBox()), 1.0d, 1.0d);
        transition.play();
        assertEquals(0.5, Whitebox.getInternalState(transition, "start"));
    }

    /**
     * Close case of {@link SplitterTransitionFactory#makeHorizontalSlide(SplitPane, double, double)}.
     */
    @Test
    public void testMakeHorizontalSlide_opening() {
        final Transition transition
            = SplitterTransitionFactory.makeHorizontalSlide(new SplitPane(new HBox(), new HBox()), 0.5d, 1.0d);
        transition.play();
        assertEquals(0.5, Whitebox.getInternalState(transition, "start"));
    }

    @Override
    public void start(Stage stage) throws Exception {
        // NOP.
    }

}
