package jp.toastkid.article;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.collections.impl.block.factory.Functions;
import org.eclipse.collections.impl.collector.Collectors2;
import org.eclipse.collections.impl.list.fixed.ArrayAdapter;

import jp.toastkid.libs.utils.FileUtil;

/**
 * Image file url random chooser.
 *
 * @author Toast kid
 */
public class ImageChooser {

    /** images. */
    private final List<URI> images;

    /**
     * pass dirs.
     * @param dirs
     */
    public ImageChooser(final String... dirs) {
        images = ArrayAdapter.adapt(dirs)
            .select(StringUtils::isNotBlank)
            .collect(Paths::get)
            .select(p -> Files.exists(p) && Files.isDirectory(p))
            .flatCollect(Functions.throwing(f -> Files.list(f).collect(Collectors2.toList())))
            .select(p -> FileUtil.isImageFile(p.getFileName().toString()))
            .collect(Path::toUri);
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