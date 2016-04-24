package jp.toastkid.libs;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 特定パッケージ配下のクラスを検索する.
 * @author Toast kid
 * @see <a href="http://d.hatena.ne.jp/Kazuhira/20120311/1331461906">
 *Javaで特定のパッケージ配下のクラスを検索する</a>
 */
public final class ClassFinder {

    private final ClassLoader classLoader;

    public static void main(final String[] args) throws Exception {
        final ClassFinder classFinder = new ClassFinder();
        for (final Class<?> clazz : classFinder.findClasses("")) {
            System.out.println(clazz);
        }
    }
    /**
     * 現在のスレッドのクラスローダを取得して初期化する.
     */
    public ClassFinder() {
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }
    /**
     * 渡されたClassLoaderで初期化する.
     * @param classLoader  ClassLoader のインスタンス
     */
    public ClassFinder(final ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
    /**
     *
     * @param rootPackageName
     * @throws Exception
     */
    public final void printClasses(final String rootPackageName) throws Exception {
        final String resourceName = rootPackageName.replace('.', '/');
        final URL url = classLoader.getResource(resourceName);
        System.out.println("URL = " + url);
        System.out.println("URLConnection = " + url.openConnection());
    }
    /**
     *
     * @param name
     * @return
     */
    private static final String fileNameToClassName(final String name) {
        return name.substring(0, name.length() - ".class".length());
    }
    /**
     *
     * @param resourceName
     * @return
     */
    private static final String resourceNameToClassName(final String resourceName) {
        return fileNameToClassName(resourceName).replace('/', '.');
    }
    /**
     *
     * @param fileName
     * @return
     */
    private static final boolean isClassFile(final String fileName) {
        return fileName.endsWith(".class");
    }
    /**
     *
     * @param packageName
     * @return
     */
    private static String packageNameToResourceName(final String packageName) {
        return packageName.replace('.', '/');
    }
    /**
     *
     * @param rootPackageName
     * @return
     * @throws Exception
     */
    public List<Class<?>> findClasses(final String rootPackageName) throws Exception {
        final String resourceName = packageNameToResourceName(rootPackageName);
        final URL url = classLoader.getResource(resourceName);

        if (url == null) {
            return new ArrayList<Class<?>>();
        }

        final String protocol = url.getProtocol();
        if ("file".equals(protocol)) {
            return findClassesWithFile(rootPackageName, new File(url.getFile()));
        } else if ("jar".equals(protocol)) {
            return findClassesWithJarFile(rootPackageName, url);
        }

        throw new IllegalArgumentException("Unsupported Class Load Protodol[" + protocol + "]");
    }
    /**
     *
     * @param packageName
     * @param dir
     * @return
     * @throws Exception
     */
    private List<Class<?>> findClassesWithFile(
            final String packageName,
            final File dir
            ) throws Exception {
        final List<Class<?>> classes = new ArrayList<Class<?>>();

        for (final String path : dir.list()) {
            final File entry = new File(dir, path);
            if (entry.isFile() && isClassFile(entry.getName())) {
                classes.add(classLoader.loadClass(packageName + "." + fileNameToClassName(entry.getName())));
            } else if (entry.isDirectory()) {
                classes.addAll(findClassesWithFile(packageName + "." + entry.getName(), entry));
            }
        }

        return classes;
    }
    /**
     *
     * @param rootPackageName
     * @param jarFileUrl
     * @return
     * @throws Exception
     */
    private List<Class<?>> findClassesWithJarFile(
            final String rootPackageName,
            final URL jarFileUrl
            ) throws Exception {
        final List<Class<?>> classes = new ArrayList<Class<?>>();

        final JarURLConnection jarUrlConnection = (JarURLConnection)jarFileUrl.openConnection();
        JarFile jarFile = null;

        try {
            jarFile = jarUrlConnection.getJarFile();
            final Enumeration<JarEntry> jarEnum = jarFile.entries();

            final String packageNameAsResourceName = packageNameToResourceName(rootPackageName);

            while (jarEnum.hasMoreElements()) {
                final JarEntry jarEntry = jarEnum.nextElement();
                if (jarEntry.getName().startsWith(packageNameAsResourceName) && isClassFile(jarEntry.getName())) {
                    classes.add(classLoader.loadClass(resourceNameToClassName(jarEntry.getName())));
                }
            }
        } finally {
            if (jarFile != null) {
                jarFile.close();
            }
        }

        return classes;
    }
}
