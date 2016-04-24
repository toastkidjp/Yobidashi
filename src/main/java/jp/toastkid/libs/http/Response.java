package jp.toastkid.libs.http;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * HTTP request's response model.
 * @author Toast kid
 *
 */
public final class Response {
    /** header. */
    private final Map<String, List<String>> header;
    /** body. */
    private final byte[] body;

    /**
     * Builder.
     * @author Toast kid
     *
     */
    protected static class Builder {
        private byte[] body;
        private Map<String, List<String>> header;

        /**
         * set body.
         * @param byteArray
         */
        public Builder setBody(final byte[] byteArray) {
            this.body = byteArray;
            return this;
        }

        public Builder setHeader(final Map<String, List<String>> headerFields) {
            this.header = headerFields;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }

    /**
     * deny make instance from other package.
     * @param b Builder.
     */
    private Response(final Builder b) {
        this.header = b.header;
        this.body   = b.body;
    }

    /**
     * get body(byte[]).
     * @return byte[].
     */
    public byte[] body() {
        return this.body;
    }

    /**
     * return body text form.
     * @return
     */
    public String text() {
        try {
            return new String(this.body, "UTF-8").replace("\n", System.lineSeparator());
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return Optional&lt;String&gt; (Nullable).
     */
    public Optional<String> textOpt() {
        return Optional.ofNullable(this.text());
    }

    /**
     *
     * @return header list map.
     */
    public Map<String, List<String>> header() {
        return this.header;
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
