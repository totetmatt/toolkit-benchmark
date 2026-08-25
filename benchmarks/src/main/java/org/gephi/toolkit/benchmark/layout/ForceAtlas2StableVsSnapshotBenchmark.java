package org.gephi.toolkit.benchmark.layout;

import java.util.concurrent.TimeUnit;
import org.gephi.graph.api.GraphModel;
import org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder;
import org.gephi.layout.spi.Layout;
import org.gephi.toolkit.benchmark.RandomGraphs;
import org.openjdk.jmh.annotations.*;
import snapshot.org.gephi.layout.plugin.forceAtlas2.ForceAtlas2;

@BenchmarkMode(Mode.SampleTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(0)
public class ForceAtlas2StableVsSnapshotBenchmark {


    @State(Scope.Benchmark)
    public static class BenchmarkState  {



        @Param({"100", "1000", "5000"})
        public int nodeCount;

        @Param({"snapshot","stable"})
        public String layout;

        public Layout useLayout = null;

        @Setup(Level.Trial)
        public void setup() {
            GraphModel graphModel = RandomGraphs.newRandomGraphModel(nodeCount);

            if(layout.equals("stable"))
            {
                useLayout= new ForceAtlas2Builder().buildLayout();
            }
            if(layout.equals("snapshot"))
            {
                useLayout = new snapshot.org.gephi.layout.plugin.forceAtlas2.ForceAtlas2Builder().buildLayout();
            }
            if(layout == null){
                throw new RuntimeException("layout is null");
            }
            useLayout.setGraphModel(graphModel);
            useLayout.initAlgo();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
            useLayout.endAlgo();
        }
    }

    @Benchmark
    public void goAlgo(BenchmarkState state) {
        state.useLayout.goAlgo();
    }
}
