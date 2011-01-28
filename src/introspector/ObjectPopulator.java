package introspector;

import java.lang.reflect.Field;

public final class ObjectPopulator {

  private final Object source;

  private ObjectPopulator(Object source) {
    if (source == null)
      throw new IllegalArgumentException("The source must not be null");
    else
      this.source = source;
  }

  public static ObjectPopulator from(Object source) {
    return new ObjectPopulator(source);
  }
  
  public void to(Object... destinations) {
    if (destinations != null) {
      for (Object destination : destinations) {
        if (destination != null)
          populate(destination);
      }
    }
  }

  private void populate(Object destination) {
    Class<?> destClass = destination.getClass();
    for (Field sourceField : source.getClass().getDeclaredFields()) {
      try {
        Field destField = destClass.getDeclaredField(sourceField.getName());
        destField.setAccessible(true);
        sourceField.setAccessible(true);
        destField.set(destination, sourceField.get(source));
      }
      catch (Exception ex) {
        // Keep walking ;)
      }
    }
  }
}