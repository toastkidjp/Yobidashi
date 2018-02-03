/*
 * Copyright (c) 2017 toastkidjp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompany this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html.
 */
package jp.toastkid.libs.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * HTTP Request.
 * TODO response binary.
 * TODO gzip
 * @author Toast kid
 *
 */
public final class Request {

    /**
     * HTTP method.
     * @author Toast kid
     *
     */
    public enum HttpMethod {
        GET, POST, PUT, HEAD, DELETE, OPTIONS, TRACE
    };

    /** target url. */
    private HttpURLConnection conn = null;

    private static TrustManager tm = new X509TrustManager() {

        @Override
        public void checkClientTrusted(
                final java.security.cert.X509Certificate[] arg0, final String arg1)
                throws java.security.cert.CertificateException {
            // NOP.
        }

        @Override
        public void checkServerTrusted(
                final java.security.cert.X509Certificate[] arg0, final String arg1)
                throws java.security.cert.CertificateException {
            // NOP.
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            // NOP.
            return null;
        }
    };

    private static SSLSocketFactory socketFactory;
    static {
        try {
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{tm}, null);
            socketFactory = sslContext.getSocketFactory();
        } catch (final KeyManagementException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Factory.
     * @author Toast kid
     *
     */
    public static final class Factory {
        /** http method. */
        private final HttpMethod method;

        /**
         *
         * @param method
         */
        protected Factory(final HttpMethod method) {
            this.method = method;
        }

        /**
         * If url is blank, throw IllegalArgumentException.
         * @param url
         * @return
         */
        public Request url(final String url) {
            if (StringUtils.isBlank(url)) {
                throw new IllegalArgumentException();
            }
            return new Request(url, method);
        }
    }

    /**
     * call only Factory.
     * @param url url.
     * @param method HTTP method.
     * @throws Exception
     */
    private Request(final String url, final HttpMethod method) {
        try {
            conn = url.startsWith("https")
                    ? (HttpsURLConnection) new URL(url).openConnection()
                    : (HttpURLConnection)  new URL(url).openConnection();
            conn.setRequestMethod(method == null ? HttpMethod.GET.name() : method.name());
            if (socketFactory != null && conn instanceof HttpsURLConnection) {
                HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public Request connectTimeout(final int ms) {
        this.conn.setConnectTimeout(ms);
        return this;
    }

    public Request readTimeout(final int ms) {
        this.conn.setReadTimeout(ms);
        return this;
    }

    public Request timeout(final int ms) {
        this.conn.setConnectTimeout(ms);
        this.conn.setReadTimeout(ms);
        return this;
    }

    public Request useCaches(final boolean useCache) {
        this.conn.setUseCaches(useCache);
        return this;
    }

    public Request followRedirects(final boolean isFollow) {
        HttpURLConnection.setFollowRedirects(isFollow);
        return this;
    }

    public Request setHeader(final String key, final String value) {
        this.conn.setRequestProperty(key, value);
        return this;
    }

    /**
     * don't overwrite value.
     */
    public Request addHeader(final String key, final String value) {
        this.conn.addRequestProperty(key, value);
        return this;
    }

    public <K, V> Request setHeaders(final Map<K, V> map) {
        map.entrySet().stream()
            .filter((entry) -> {return entry.getKey() != null && entry.getValue() != null;})
            .forEach((entry) -> {this.conn.setRequestProperty(
                    entry.getKey().toString(), entry.getValue().toString());});
        return this;
    }

    /**
     * attempt HTTP method.
     * @return {@link Response} object.
     */
    public Response fetch() {

        if (conn == null) {
            return null;
        }

        try (final InputStream           in  = conn.getInputStream();
             final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ) {
            final byte[] buf = new byte[4096];
            int len = -1;
            while ((len = in.read(buf, 0, 4096)) != -1) {
                bos.write(buf, 0, len);
            }
            conn.disconnect();
            final Response.Builder response = new Response.Builder();
            response.setBody(bos.toByteArray());
            response.setHeader(conn.getHeaderFields());
            return response.build();
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        return null;
    }

    /**
     * attempt HTTP fetch.
     * @return Optional contains {@link Response} object.
     */
    public Optional<Response> fetchOpt() {
        final Response fetch = fetch();
        return (fetch == null) ? Optional.empty() : Optional.of(fetch);
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
