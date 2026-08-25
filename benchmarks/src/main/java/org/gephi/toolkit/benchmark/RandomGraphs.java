package org.gephi.toolkit.benchmark;

import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.generator.plugin.RandomGraph;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.project.api.Workspace;
import org.openide.util.Lookup;

final class RandomGraphs {

    private RandomGraphs() {
    }

    static GraphModel newRandomGraphModel(int nodeCount) {
        Container container = Lookup.getDefault().lookup(Container.Factory.class).newContainer();
        RandomGraph randomGraph = new RandomGraph();
        randomGraph.setNumberOfNodes(nodeCount);
        randomGraph.setWiringProbability(0.01);
        randomGraph.generate(container.getLoader());

        Workspace workspace = Lookup.getDefault().lookup(ImportController.class)
                .process(container, new DefaultProcessor(), null);

        return Lookup.getDefault().lookup(GraphController.class).getGraphModel(workspace);
    }
}
