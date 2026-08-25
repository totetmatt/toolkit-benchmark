# toolkit-benchmark

JMH benchmarks comparing `gephi-toolkit:0.11.2` (Maven Central) against a local
`layout-plugin:0.11.3-SNAPSHOT` build.

Both versions provide a class with the same name
(`org.gephi.layout.plugin.forceAtlas.ForceAtlas`, `...forceAtlas2.ForceAtlas2`, etc.), so they
can't be regular dependencies of the same module at once. The `layout-plugin-local` module works
around this with `maven-shade-plugin`: it repackages the local build's classes (and its
`META-INF/services` SPI entries) under the `local.*` prefix. The `benchmarks` module then depends
on both gephi-toolkit and this relocated jar side by side.

## Build

```bash
mvn install
```

Re-run this whenever `layout-plugin-local` changes — `benchmarks` resolves it from your local
`~/.m2` repo, not from an in-memory reactor.

## Run benchmarks

All benchmarks:

```bash
mvn -pl benchmarks exec:java
```

One class, or a name filter (JMH treats the argument as a regex):

```bash
mvn -pl benchmarks exec:java -Dexec.args="ForceAtlas2StableVsLocalBenchmark"
```

Quick smoke test (short warmup/measurement, useful while iterating):

```bash
mvn -pl benchmarks exec:java -Dexec.args="ForceAtlas2StableVsLocalBenchmark -wi 1 -i 2 -w 500ms -r 500ms"
```

List available benchmarks without running them:

```bash
mvn -pl benchmarks exec:java -Dexec.args="-l"
```

Silence the harmless `SEVERE ... InaccessibleObjectException` NetBeans logs on JDK 9+
(no effect on results):

```bash
MAVEN_OPTS="--add-opens java.base/java.net=ALL-UNNAMED" mvn -pl benchmarks exec:java
```

## Benchmarks

- `ForceAtlasBuilderBenchmark` — cost of building a `ForceAtlas` layout via each version's
  `LayoutBuilder`.
- `ForceAtlas2StableVsLocalBenchmark` — cost of one `ForceAtlas2.goAlgo()` iteration on a fixed
  200-node random graph, default settings, stable vs. local.

## Notes

- Benchmarks run with `@Fork(0)` (in-process). `mvn exec:java` resolves the classpath through a
  custom classloader, not the JVM's `java.class.path`, so JMH's normal forked-JVM mode can't
  launch its subprocess. Fine for local iteration; not representative of a fully isolated,
  publishable measurement.
- There's no uber jar. Given gephi-toolkit's huge dependency tree (NetBeans platform, Batik, POI,
  PDFBox, several JDBC drivers, ...), shading it all into one runnable jar added build time and
  noise for no benefit — the actual class-collision problem is already solved by
  `layout-plugin-local`.
