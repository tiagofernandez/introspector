package introspector.mock;

@Feline
public class Cat implements Animal {

  private final String name;

  public Cat(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}