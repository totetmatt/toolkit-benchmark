package org.gephi.toolkit.benchmark;

import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;
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

        @Param({"200", "2000"})
        int nodeCount;

        @Param({"true", "false"})
        boolean barnesHutOptimize;

        private ForceAtlas2 layout;

        @Setup(Level.Trial)
        public void setup() {
            GraphModel graphModel = randomGraphModel(nodeCount);

            layout = new ForceAtlas2Builder().buildLayout();
            layout.setGraphModel(graphModel);
            layout.setBarnesHutOptimize(barnesHutOptimize);
            layout.initAlgo();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            layout.endAlgo();
        }

        private static GraphModel randomGraphModel(int nodeCount) {
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            pc.newProject();
            Workspace workspace = pc.getCurrentWorkspace();

            Container container = Lookup.getDefault().lookup(Container.Factory.class).newContainer();
            RandomGraph randomGraph = new RandomGraph();
            randomGraph.setNumberOfNodes(nodeCount);
            randomGraph.setWiringProbability(0.01);
            randomGraph.generate(container.getLoader());

            Lookup.getDefault().lookup(ImportController.class)
                    .process(container, new DefaultProcessor(), workspace);

            return Lookup.getDefault().lookup(GraphController.class).getGraphModel();
        }
    }

    @Benchmark
    public void forceAtlas2Iteration(LayoutState state) {
        state.layout.goAlgo();
    }
}
