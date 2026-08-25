package org.gephi.toolkit.benchmark;

import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.GraphModel;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

/**
 * Cost of a single gephi-toolkit ForceAtlas2 iteration, with vs. without the Barnes-Hut
 * approximation, across graph sizes. Barnes-Hut trades exact O(n^2) repulsion for an O(n log n)
 * quad-tree approximation, so the gap should widen as nodeCount grows.
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Fork(0)
public class ForceAtlas2BarnesHutBenchmark {

    @State(Scope.Thread)
    public static class LayoutState {

        @Param({"2000"})
        int nodeCount;

        @Param({"true", "false"})
        boolean barnesHutOptimize;

        private ForceAtlas2 layout;

        @Setup(Level.Trial)
        public void setup() {
            GraphModel graphModel = RandomGraphs.newRandomGraphModel(nodeCount);

            layout = new ForceAtlas2Builder().buildLayout();
            layout.setGraphModel(graphModel);
            layout.setBarnesHutOptimize(barnesHutOptimize);
            layout.initAlgo();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            layout.endAlgo();
        }
    }

    @Benchmark
    public void forceAtlas2Iteration(LayoutState state) {
        state.layout.goAlgo();
    }
}
