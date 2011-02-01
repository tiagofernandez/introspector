package introspector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.logging.Logger;

/**
 * Utility to find classes in the classpath.
 *
 * <pre>
 *   import introspector.ClassQuery;
 *
 *   Set<Class> annotatedClasses = ClassQuery.
 *    from("any.package.you.want").
 *     searchClassesAnnotatedWith(SomeAnnotation.class);
 *
 *   Set<Class> implementationClasses = ClassQuery.
 *     from("any.package.you.want").
 *     searchClassesImplementing(SomeInterface.class);
 * </pre>
 *
 * @author Tiago Fernandez
 * @since 1.0.0
 */
public final class ClassQuery {

  private static final Logger logger = Logger.getLogger("introspector.ClassQuery");

  private static final byte[] JAR_HEADER = {'P', 'K', 3, 4};

  private final Set<Class> matches = new HashSet<Class>();

  private String[] packageNames;

  private ClassQuery(String... packageNames) {
    this.packageNames = new String[packageNames.length];
    System.arraycopy(packageNames, 0, this.packageNames, 0, packageNames.length);
  }

  public static ClassQuery from(String... packageNames) {
    if (packageNames == null)
      throw new IllegalArgumentException("The package names must be provided.");
    else
      return new ClassQuery(packageNames);
  }

  public Set<Class> searchClassesAnnotatedWith(Class<? extends Annotation> annotation) {
    Matcher matcher = new AnnotationMatcher(annotation);
    for (String packageName : packageNames) {
      find(matcher, packageName);
    }
    return Collections.unmodifiableSet(matches);
  }

  public Set<Class> searchClassesImplementing(Class parent) {
    Matcher matcher = new ImplementationMatcher(parent);
    for (String packageName : packageNames) {
      find(matcher, packageName);
    }
    return Collections.unmodifiableSet(matches);
  }

  private void find(Matcher matcher, String packageName) {
    String path = getPackagePath(packageName);
    try {
      List<String> resources = listResources(path);
      for (String resource : resources) {
        if (isClass(resource))
          examine(resource, matcher);
      }
    }
    catch (IOException ex) {
      logger.warning("Could not read package: " + packageName);
    }
  }

  private void examine(String className, Matcher matcher) {
    try {
      String externalName = getExternalName(className);
      Class type = getClassLoader().loadClass(externalName);
      if (matcher.matches(type))
        matches.add(type);
    }
    catch (Throwable ex) {
      logger.warning("Could not examine class: " + className);
    }
  }

  private List<String> listResources(String path) throws IOException {
    List<String> resources = new ArrayList<String>();
    for (URL url : listUrlResources(path)) {
      List<String> packagedResources = listResources(url, path);
      resources.addAll(packagedResources);
    }
    return resources;
  }

  private List<String> listResources(URL url, String path) throws IOException {
    InputStream stream = null;
    try {
      List<String> resources = new ArrayList<String>();
      URL jarUrl = findJarUrl(url);
      if (jarUrl != null) {
        stream = jarUrl.openStream();
        resources.addAll(listPackagedResources(new JarInputStream(stream), path));
      }
      else {
        stream = url.openStream();
        String prefix = includeTrailingSlash(url.toExternalForm());
        for (String resource : listChildrenResources(path, stream)) {
          String resourcePath = path + '/' + resource;
          resources.add(resourcePath);
          URL childUrl = new URL(prefix + resource);
          resources.addAll(listResources(childUrl, resourcePath));
        }
      }
      return resources;
    }
    finally {
      close(stream);
    }
  }

  private List<String> listPackagedResources(JarInputStream jar, String path) throws IOException {
    List<String> resources = new ArrayList<String>();
    path = includeLeadingAndTrailingSlashes(path);
    for (JarEntry entry; (entry = jar.getNextJarEntry()) != null;) {
      if (!entry.isDirectory()) {
        String name = includeLeadingSlash(entry.getName());
        if (name.startsWith(path))
          resources.add(name.substring(1)); // Trim leading slash
      }
    }
    return resources;
  }

  private List<String> listChildrenResources(String path, InputStream stream) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    List<String> children = new ArrayList<String>();
    for (String line; (line = reader.readLine()) != null;) {
      children.add(line);
      List<URL> urlResources = listUrlResources(path + '/' + line);
      if (urlResources.isEmpty()) {
        children.clear();
        return children;
      }
    }
    return children;
  }

  private List<URL> listUrlResources(String path) throws IOException {
    return Collections.list(getClassLoader().getResources(path));
  }

  private URL findJarUrl(URL url) throws MalformedURLException {
    String externalJarUrl = findExternalJarUrl(url);
    StringBuilder externalUrl = new StringBuilder(externalJarUrl);
    int indexOfJar = externalUrl.lastIndexOf(".jar");
    if (indexOfJar >= 0) {
      externalUrl.setLength(indexOfJar + 4);
      URL jarUrl = new URL(externalUrl.toString());
      if (isJar(jarUrl))
        return jarUrl;
    }
    return null;
  }

  @SuppressWarnings({"InfiniteLoopStatement"})
  private String findExternalJarUrl(URL url) {
    try {
      while (true) {
        // If the file part of the URL is itself a URL, then that URL probably points to the Jar
        String file = url.getFile();
        url = new URL(file);
      }
    }
    catch (MalformedURLException ex) {
      // This will happen at some point and serves as a break in the loop
    }
    return url.toExternalForm();
  }

  @SuppressWarnings({"ResultOfMethodCallIgnored"})
  private boolean isJar(URL url) {
    InputStream inputStream = null;
    try {
      byte[] buffer = new byte[JAR_HEADER.length];
      inputStream = url.openStream();
      inputStream.read(buffer, 0, JAR_HEADER.length);
      return Arrays.equals(buffer, JAR_HEADER);
    }
    catch (Exception ex) {
      // Not a JAR
    }
    finally {
      close(inputStream);
    }
    return false;
  }

  private String includeLeadingAndTrailingSlashes(String path) {
    path = includeLeadingSlash(path);
    path = includeTrailingSlash(path);
    return path;
  }

  private String includeLeadingSlash(String path) {
    return path.startsWith("/") ? path : '/' + path;
  }

  private String includeTrailingSlash(String path) {
    return path.endsWith("/") ? path : path + '/';
  }

  private boolean isClass(String resource) {
    return resource.endsWith(".class");
  }

  private String getPackagePath(String packageName) {
    return packageName == null ? null : packageName.replace('.', '/');
  }

  private String getExternalName(String className) {
    return className.substring(0, className.indexOf('.')).replace('/', '.');
  }

  private ClassLoader getClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  private void close(InputStream inputStream) {
    try {
      if (inputStream != null)
        inputStream.close();
    }
    catch (Exception ex) {
      // Ignore
    }
  }
}