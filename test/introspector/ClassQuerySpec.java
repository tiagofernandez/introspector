package introspector;

import introspector.mock.*;
import org.junit.Test;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Category;
import org.junit.runners.Suite;

import java.lang.annotation.Retention;
import java.util.Arrays;

import static introspector.ClassQuery.from;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
public class ClassQuerySpec {

  @Test
  public void it_should_get_annotated_classes() {
    assertTrue(
      from("introspector.mock").
      searchClassesAnnotatedWith(Canine.class).
      contains(Dog.class)
    );
    assertTrue(
      from("introspector.mock").
      searchClassesAnnotatedWith(Feline.class).
      contains(Cat.class)
    );
  }

  @Test
  public void it_should_get_implementation_classes() {
    assertTrue(
      from("introspector.mock").
      searchClassesImplementing(Animal.class).
      containsAll(Arrays.asList(Dog.class, Cat.class, Snake.class))
    );
  }

  @Test
  public void it_should_get_stuff_inside_jars() {
    assertTrue(
      from("org.junit.experimental.categories").
      searchClassesAnnotatedWith(Retention.class).
      contains(Category.class)
    );
    assertTrue(
      from("org.junit.experimental.categories").
      searchClassesImplementing(Suite.class).
      contains(Categories.class)
    );
  }
}