# Introspector

Introspector is an utility that offers classpath scanning using nothing but
core Java libraries. That means you just need to add instrospector-x.y.z.jar
to your classpath, and you are good to go.

If you need something extremely simple or you are just tired of dealing with
dozens of transitive dependencies while all you need is a single library, this
tool might be for you. Use it at your own risk, though ;)


## API

### ClassQuery

    import introspector.ClassQuery;

    Set<Class> annotatedClasses = ClassQuery.
      from("any.package.you.want").
      searchClassesAnnotatedWith(SomeAnnotation.class);

    Set<Class> implementationClasses = ClassQuery.
      from("any.package.you.want").
      searchClassesImplementing(SomeInterface.class);

### ObjectPopulator

    import introspector.ObjectPopulator;

    ObjectPopulator.from(source).to(destination);


## Notes

1. This library works fine on Google App Engine.
2. New features and APIs will be added on demand, feel free to ask or just fork.


## Author

Tiago Fernandez (2011) | [Twitter][t]

[t]: http://twitter.com/tiagofernandez