package jp.toastkid.gui.jfx.name;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Name information.
 * @author Toast kid
 */
public final class NameInformation implements Comparable<NameInformation> {

    /** empty object. */
    private static final NameInformation PRESENT = new NameInformation.Builder().build();

    /** name. */
    private String name;

    /** name's spelling. */
    private String spelling;

    /** name's gender. */
    private String seibetsu;

    /** name's nationality. */
    private String nationality;

    /**
     * Builder.
     * @author Toast kid
     *
     */
    public static class Builder {

        private String name;

        private String spelling;

        private String seibetsu;

        private String nationality;
        /**
         *
         * @param name
         */
        public Builder setName(final String name){
            this.name = name;
            return this;
        }
        /**
         *
         * @param spelling
         */
        public Builder setSpelling(final String spelling){
            this.spelling = spelling;
            return this;
        }
        /**
         *
         * @param seibetsu
         */
        public Builder setSeibetsu(final String seibetsu){
            this.seibetsu = seibetsu;
            return this;
        }
        /**
         *
         * @param nationality
         */
        public Builder setNationality(final String nationality){
            this.nationality = nationality;
            return this;
        }

        public NameInformation build() {
            return new NameInformation(this);
        }
    }

    /**
     * for use Jackson.
     */
    public NameInformation() {
        // NOP.
    }

    /**
     * can call only class internal.
     * @param builder
     */
    private NameInformation(final Builder builder) {
        this.name        = builder.name;
        this.nationality = builder.nationality;
        this.seibetsu    = builder.seibetsu;
        this.spelling    = builder.spelling;
    }

    @Override
    public boolean equals(final Object pNameInformation){
        return EqualsBuilder.reflectionEquals(this, pNameInformation, true);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, true);
    }

    @Override
    public int compareTo(final NameInformation obj1) {
        final String nationality1 = obj1.getNationality();
        int ret = 0;
        if ((ret = this.nationality.compareTo(nationality1)) == 0) {
            final String spelling0 = this.getSpelling();
            final String spelling1 = obj1.getSpelling();
            ret = spelling0.compareTo(spelling1);
            if ((ret = spelling0.compareTo(spelling1)) == 0) {
                final String seibetsu0 = this.getSeibetsu();
                final String seibetsu1 = ( obj1).getSeibetsu();
                ret = seibetsu0.compareTo(seibetsu1);
            }
        }
        return ret;
    }

    /**
     *
     * @return
     */
    public String getName(){
         return this.name;
    }
    /**
     *
     * @return
     */
    public String getSpelling(){
         return this.spelling;
    }

    /**
     *
     * @return
     */
    public String getSeibetsu(){
         return this.seibetsu;
    }

    /**
     *
     * @return
     */
    public String getNationality(){
         return this.nationality;
    }

    public static NameInformation empty() {
        return PRESENT;
    }
}
