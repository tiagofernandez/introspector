package introspector.mock;

@Canine
public class Dog implements Animal {

  private final String name;

  public Dog(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}