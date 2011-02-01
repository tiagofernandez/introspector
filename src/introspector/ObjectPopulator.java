package introspector;

import java.lang.reflect.Field;

/**
 * Utility to populate objects directly through declared fields.
 *
 * <pre>
 *   import introspector.ObjectPopulator;
 *
 *   ObjectPopulator.from(source).to(destination);
 * </pre>
 *
 * @author Tiago Fernandez
 * @since 1.0.1
 */
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
    for (Field sourceField : source.getClass().getDeclaredFields())
      populateField(sourceField, destination);
  }

  private void populateField(Field sourceField, Object destination) {
    try {
      Class<?> destClass = destination.getClass();
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