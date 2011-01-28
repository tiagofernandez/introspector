package introspector;

import introspector.mock.Cat;
import introspector.mock.Dog;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ObjectPopulatorSpec {

  @Test
  public void it_should_populate_a_bean() {
    Dog dog = new Dog("Odie");
    Cat cat = new Cat("Garfield");

    ObjectPopulator.from(dog).to(cat);
    assertEquals(dog.getName(), cat.getName());
  }
}