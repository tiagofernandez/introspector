package introspector;

class ImplementationMatcher implements Matcher {

  private final Class<?> parent;

  ImplementationMatcher(Class<?> parentType) {
    this.parent = parentType;
  }

  public boolean matches(Class<?> type) {
    return type != null && parent.isAssignableFrom(type);
  }
}