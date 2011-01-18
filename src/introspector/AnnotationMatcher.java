package introspector;

import java.lang.annotation.Annotation;

class AnnotationMatcher implements Matcher {

  private final Class<? extends Annotation> annotation;

  AnnotationMatcher(Class<? extends Annotation> annotation) {
    this.annotation = annotation;
  }

  public boolean matches(Class<?> type) {
    return type != null && type.isAnnotationPresent(annotation);
  }
}