package org.gephi.toolkit.benchmark;

import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.GraphModel;
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

/**
 * Default-settings ForceAtlas2 iteration cost on a fixed 200-node graph: gephi-toolkit's
 * bundled ForceAtlas2 (0.11.2) vs. the local layout-plugin build (0.11.3-SNAPSHOT, relocated
 * under local.*). Each variant gets its own graph instance; layout settings are left untouched
 * (no Barnes-Hut override) so both run with whatever ForceAtlas2 defaults to for this node count.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(0)
public class ForceAtlas2StableVsLocalBenchmark {

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
    public static class LocalState {

        private local.org.gephi.layout.plugin.forceAtlas2.ForceAtlas2 layout;

        @Setup(Level.Trial)
        public void setup() {
            GraphModel graphModel = RandomGraphs.newRandomGraphModel(NODE_COUNT);
            layout = new local.org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder().buildLayout();
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
    public void local(LocalState state) {
        state.layout.goAlgo();
    }
}
