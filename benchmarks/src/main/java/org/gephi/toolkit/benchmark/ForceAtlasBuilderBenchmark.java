package org.gephi.toolkit.benchmark;

import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;

/**
 * Compares the gephi-toolkit 0.11.2 bundled layout-plugin against the local layout-plugin
 * 0.11.3-SNAPSHOT build (relocated by the layout-plugin-local module), so both can be exercised
 * side by side in the same JVM.
 *
 * Fork(0): run in-process. Benchmarks are launched via "mvn exec:java", which resolves the
 * project classpath through a custom classloader rather than the JVM's java.class.path - JMH's
 * default forked-JVM mode can't see that classpath, so its subprocess fails to start.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(0)
public class ForceAtlasBuilderBenchmark {

    @Benchmark
    public Object toolkitForceAtlasBuilder() {
        return new org.gephi.layout.plugin.forceAtlas.ForceAtlas().buildLayout();
    }

    @Benchmark
    public Object localForceAtlasBuilder() {
        return new local.org.gephi.layout.plugin.forceAtlas.ForceAtlas().buildLayout();
    }
}
