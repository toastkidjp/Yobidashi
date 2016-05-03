package jp.toastkid.gui.jfx.wiki.chart;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableView;

/**
 * for use {@link TableView}.
 * @author Toast kid
 *
 */
public class KeyValue {

    /** key. */
    private final StringProperty key;

    /** middle value. */
    private final StringProperty middle;

    /** numeric value. */
    private final LongProperty value;

    /**
     * Builder.
     * @author Toast kid
     *
     */
    public static class Builder {

        private String key;
        private Object middle;
        private Number value;

        public Builder setKey(final String key) {
            this.key = key;
            return this;
        }

        public Builder setMiddle(final Object middle) {
            this.middle = middle;
            return this;
        }

        public Builder setValue(final Number value) {
            this.value = value;
            return this;
        }

        public KeyValue build() {
            return new KeyValue(this);
        }
    }

    /**
     * call only internal Builder.
     * @param b
     */
    private KeyValue(final Builder b) {
        this.key    = new SimpleStringProperty(b.key != null ? b.key : "");
        this.middle = new SimpleStringProperty(b.middle != null ? b.middle.toString() : "");
        this.value  = new SimpleLongProperty(b.value != null ? b.value.longValue() : 0);
    }

    /**
     * It should be named xxProperty.
     * @return
     */
    public StringProperty keyProperty() {
        return key;
    }

    /**
     * It should be named xxProperty.
     * @return
     */
    public StringProperty middleProperty() {
        return middle;
    }

    /**
     * It should be named xxProperty.
     * @return
     */
    public Property valueProperty() {
        return value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

}
