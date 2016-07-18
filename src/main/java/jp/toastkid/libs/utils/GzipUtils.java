package jp.toastkid.libs.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Gzip converting utility.
 * @author Toast kid
 *
 */
public class GzipUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * passed object convert to gziped byte array.
     * @param o
     * @return
     * @throws JSONException
     * @throws IOException
     */
    public static final <T> byte[] gzip(final T o) throws IOException {
        if (o == null) {
            return null;
        }
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final GZIPOutputStream      gzip = new GZIPOutputStream(baos);
                ) {
            gzip.write(MAPPER.writeValueAsBytes(o));;
            gzip.close();
            return baos.toByteArray();
        }
    }

    /**
     * return JSON form.
     * @param b gziped byte array.
     * @return JSON string.
     * @throws JSONException
     * @throws IOException
     */
    public static final <T> T gunzip(final byte[] b, final Class<T> c) throws IOException {
        if (b == null) {
            return null;
        }
        try (final GZIPInputStream in = new GZIPInputStream(new ByteArrayInputStream(b))) {
            return MAPPER.readValue(in, c);
        }
    }

    /**
     * read gzip with String list.
     * @param filename
     * @return
     * @throws IOException
     */
    public static List<String> read(final String filename) throws IOException {
        try (final BufferedReader br = setUpReader(filename)) {
            final List<String> list = new ArrayList<>();
            String line = null;
            while((line = br.readLine()) != null){
                list.add(line);
            }
            return list;
        }
    }

    /**
     * write to gzip file.
     * @param o
     * @param filename
     * @throws IOException
     */
    public static void write(final Object o, final String filename) throws IOException {
        try (final BufferedWriter bw = setUpWriter(filename)) {
            bw.write(MAPPER.writeValueAsString(o));
            bw.close();
        }
    }

    /**
     * set up gzip reader.
     * @param filename
     * @return
     * @throws IOException
     */
    private static BufferedReader setUpReader(final String filename) throws IOException {
        return new BufferedReader(
                new InputStreamReader(new GZIPInputStream(
                                Files.newInputStream(new File(filename).toPath()))));
    }

    /**
     * set up gzip file writer.
     * @param filename
     * @return
     * @throws IOException
     */
    private static BufferedWriter setUpWriter(final String filename) throws IOException {
        return new BufferedWriter(
                new OutputStreamWriter(
                        new GZIPOutputStream(Files.newOutputStream(new File(filename).toPath()))));
    }

}
