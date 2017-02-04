package jp.toastkid.article.control.editor;

import javafx.collections.ObservableList;
import javafx.scene.input.InputMethodHighlight;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Shape;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Text;

/**
 * Attr factory.
 *
 * @author Toast kid
 */
public class AttrFactory {

    static class XY {
        double minX = 0d;
        double maxX = 0d;
        double minY = 0d;
        double maxY = 0d;
    }

    private static XY initXy(final PathElement pe) {

        final XY xy = new XY();

        if (pe instanceof MoveTo) {
            xy.minX = xy.maxX = ((MoveTo)pe).getX();
            xy.minY = xy.maxY = ((MoveTo)pe).getY();
            return xy;
        }

        if (pe instanceof LineTo) {
            xy.minX = (xy.minX < ((LineTo)pe).getX() ? xy.minX : ((LineTo)pe).getX());
            xy.maxX = (xy.maxX > ((LineTo)pe).getX() ? xy.maxX : ((LineTo)pe).getX());
            xy.minY = (xy.minY < ((LineTo)pe).getY() ? xy.minY : ((LineTo)pe).getY());
            xy.maxY = (xy.maxY > ((LineTo)pe).getY() ? xy.maxY : ((LineTo)pe).getY());
            return xy;
        }

        if (pe instanceof HLineTo) {
            xy.minX = (xy.minX < ((HLineTo)pe).getX() ? xy.minX : ((HLineTo)pe).getX());
            xy.maxX = (xy.maxX > ((HLineTo)pe).getX() ? xy.maxX : ((HLineTo)pe).getX());
            return xy;
        }

        if (pe instanceof VLineTo) {
            xy.minY = (xy.minY < ((VLineTo)pe).getY() ? xy.minY : ((VLineTo)pe).getY());
            xy.maxY = (xy.maxY > ((VLineTo)pe).getY() ? xy.maxY : ((VLineTo)pe).getY());
            return xy;
        }

        return xy;
    }

    public static Shape make(
            final PathElement pe, final Text text, final InputMethodHighlight highlight) {
        if (highlight == InputMethodHighlight.SELECTED_RAW) {
            // blue background
            final Shape attr = new Path();
            //((javafx.scene.shape.Path)attr).getElements().addAll(getRangeShape(start, end));
            ((javafx.scene.shape.Path)attr).getElements().addAll(pe);
            attr.setFill(Color.BLUE);
            attr.setOpacity(0.3f);
            System.out.println("SELECTED_RAW: " + text.getText());
            return attr;
        }

        if (highlight == InputMethodHighlight.UNSELECTED_RAW) {
            final XY xy = initXy(pe);
            // dash underline.
            final Shape attr = new Line(xy.minX + 2, xy.maxY + 1, xy.maxX - 2, xy.maxY + 1);
            attr.setStroke(text.getFill());
            attr.setStrokeWidth(xy.maxY - xy.minY);
            final ObservableList<Double> dashArray = attr.getStrokeDashArray();
            dashArray.add(Double.valueOf(2f));
            dashArray.add(Double.valueOf(2f));
            System.out.println("UNSELECTED_RAW: " + text.getText());
            return attr;
        }

        if (highlight == InputMethodHighlight.SELECTED_CONVERTED) {
            final XY xy = initXy(pe);
            // thick underline.
            final Shape attr = new Line(xy.minX + 2, xy.maxY + 1, xy.maxX - 2, xy.maxY + 1);
            attr.setStroke(text.getFill());
            attr.setStrokeWidth((xy.maxY - xy.minY) * 3);
            System.out.println("SELECTED_CONVERTED: " + text.getText());
            return attr;
        }

        if (highlight == InputMethodHighlight.UNSELECTED_CONVERTED) {
            final XY xy = initXy(pe);
            // single underline.
            final Shape attr = new Line(xy.minX + 2, xy.maxY + 1, xy.maxX - 2, xy.maxY + 1);
            attr.setStroke(text.getFill());
            attr.setStrokeWidth(xy.maxY - xy.minY);
            System.out.println("UNSELECTED_CONVERTED: " + text.getText());
            return attr;
        }
        return new Line();
    }

}
