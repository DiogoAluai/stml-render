package daluai.sttp.sttp_render;

import java.net.URL;

public final class TestUtils {

    private final Class<?> clazz;

    public static TestUtils createUtils(Class<?> clazz) {
        return new TestUtils(clazz);
    }

    public static TestUtils createUtils(Object o) {
        return new TestUtils(o);
    }

    public TestUtils(Object o) {
        this(o.getClass());
    }

    public TestUtils(Class<?> clazz) {
        this.clazz = clazz;
    }

    public URL getResourcePath(String resourceName) {
        return TestUtils.getResourcePath(clazz, resourceName);
    }

    public static URL getResourcePath(Object o, String resourceName) {
        return getResourcePath(o.getClass(), resourceName);
    }


    public static URL getResourcePath(Class<?> clazz, String resourceName) {
        String relativeResourcePath = clazz.getPackageName().replace('.', '/') + '/' + resourceName;
        return clazz.getClassLoader().getResource(relativeResourcePath);
    }
}
