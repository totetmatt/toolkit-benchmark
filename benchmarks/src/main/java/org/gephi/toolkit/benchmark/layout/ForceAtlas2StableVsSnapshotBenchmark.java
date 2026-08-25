package org.gephi.toolkit.benchmark.layout;

import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.GraphModel;
import org.gephi.toolkit.benchmark.RandomGraphs;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(0)
public class ForceAtlas2StableVsSnapshotBenchmark {

    private static final int NODE_COUNT = 200;

    @State(Scope.Thread)
    public static class StableState {

        private org.gephi.layout.plugin.forceAtlas2.ForceAtlas2 layout;

        @Setup(Level.Trial)
        public void setup() {
            GraphModel graphModel = RandomGraphs.newRandomGraphModel(NODE_COUNT);
            layout = new org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder().buildLayout();
            layout.setGraphModel(graphModel);
            layout.initAlgo();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            layout.endAlgo();
        }
    }

    @State(Scope.Thread)
    public static class SnapshotState {

        private snapshot.org.gephi.layout.plugin.forceAtlas2.ForceAtlas2 layout;

        @Setup(Level.Trial)
        public void setup() {
            GraphModel graphModel = RandomGraphs.newRandomGraphModel(NODE_COUNT);
            layout = new snapshot.org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder().buildLayout();
            layout.setGraphModel(graphModel);
            layout.initAlgo();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            layout.endAlgo();
        }
    }

    @Benchmark
    public void stable(StableState state) {
        state.layout.goAlgo();
    }

    @Benchmark
    public void snapshot(SnapshotState state) {
        state.layout.goAlgo();
    }
}
