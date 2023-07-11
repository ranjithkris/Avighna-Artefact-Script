package de.fraunhofer.iem;

import soot.Scene;
import soot.SceneTransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RemoveClinitTransformer extends SceneTransformer {

    @Override
    protected void internalTransform(String s, Map<String, String> map) {
        CallGraph callGraph = Scene.v().getCallGraph();

        List<Edge> edgesToBeRemoved = new ArrayList<>();

        for (Edge edge : callGraph) {
            if (edge.getTgt().method().getName().equals("<clinit>") &&
                    edge.getTgt().method().getDeclaringClass().getPackageName().startsWith("de.fraunhofer.iem")) {
                edgesToBeRemoved.add(edge);
            }
        }

        for (Edge edge : edgesToBeRemoved) {
            callGraph.removeEdge(edge);
        }
    }
}
