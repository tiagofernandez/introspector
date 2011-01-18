Introspector
===

Introspector is an utility that offers classpath scanning using nothing but
core Java libraries. That means you just need to add instrospector-1.0.0.jar
to your classpath, and you are good to go.

If you need something extremely simple or you are just tired of dealing with
dozens of transitive dependencies while all you need is a single library,
this tool might be for you. Use it at your own risk, though.


Usage
---

    import static introspector.ClassQuery.from;

    Set<Class> annotatedClasses =
      from("any.package.you.want").
      searchClassesAnnotatedWith(SomeAnnotation.class);

    Set<Class> implementationClasses =
      from("any.package.you.want").
      searchClassesImplementing(SomeInterface.class);


Author
---

Tiago Fernandez (2011) | [Twitter][t]

[t]: http://twitter.com/tiagofernandez