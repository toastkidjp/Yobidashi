package jp.toastkid.wiki;

import java.io.File;
import java.io.FileFilter;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;

import jp.toastkid.libs.utils.FileUtil;

/**
 * Image file url random chooser.
 *
 * @author Toast kid
 *
 */
public class ImageChooser {

    /** image file filter. */
    private static final FileFilter IMAGE_FILTER = f -> FileUtil.isImageFile(f.getName());

    /** images. */
    private final List<URI> images;

    /**
     * pass dirs.
     * @param dirs
     */
    public ImageChooser(final String... dirs) {
        images = ArrayAdapter.adapt(dirs)
            .select(StringUtils::isNotBlank)
            .collect(File::new)
            .select(f -> f.exists() && f.isDirectory())
            .collect(f -> f.listFiles(IMAGE_FILTER))
            .flatCollect(Arrays::asList)
            .collect(File::toURI);
    }

    /**
     * Add passed URI.
     * @param uri
     */
    public void add(final URI uri) {
        images.add(uri);
    }

    /**
     * return file url.
     * @return file url string.
     */
    public String choose() {
        if (images.size() == 0) {
            return "";
        }
        return images.get((int) Math.round((images.size() - 1) * Math.random())).toString();
    }
}