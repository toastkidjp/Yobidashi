package jp.toastkid.article;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

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
        images = Stream.of(dirs)
            .filter(StringUtils::isNotBlank)
            .map(Paths::get)
            .filter(p -> Files.exists(p) && Files.isDirectory(p))
            .flatMap(f -> {
				try {
					return Files.list(f);
				} catch (final IOException e) {
					e.printStackTrace();
				}
				return Stream.empty();
			})
            .filter(p -> FileUtil.isImageFile(p.getFileName().toString()))
            .map(Path::toUri)
            .collect(Collectors.toList());
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