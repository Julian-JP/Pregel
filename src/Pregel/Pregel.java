package Pregel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Pregel {
    public static <NV, EV, M> Graph<NV, EV> apply(Graph<NV, EV> graph, int maxSuperSteps, BiFunction<ExtendedNode<NV, EV>, List<M>, NV> vertexFunction, Function<EdgeTriplet<NV, EV>, M> sendMsg, Consumer<Stream<Node<NV>>> analysis) {

        List<ExtendedNode<NV, EV>> activeNodes = graph.toExtendedNodeStream().collect(Collectors.toList());
        HashSet<ExtendedNode<NV, EV>> inactiveNodes = new HashSet<>();

        for (int i = 0; i < maxSuperSteps; i++) {
            if (i % 5 == 0) {
                analysis.accept(graph.toNodeStream());
            }

            HashMap<ExtendedNode<NV, EV>, List<M>> messages = new HashMap<>();
            List<ExtendedNode<NV, EV>> activeNodesNew = new ArrayList<>(activeNodes.size());
            HashSet<ExtendedNode<NV, EV>> reactivatedActiveNodes = new HashSet<>();


            for (int j = 0; j < activeNodes.size(); j++) {
                for (int k = 0; k < activeNodes.get(j).getNeighbors().size(); ++k) {
                    ExtendedNode from = activeNodes.get(j);
                    ExtendedNode to = activeNodes.get(j).getNeighbors().get(k).to;
                    Edge<EV> edge = activeNodes.get(j).getNeighbors().get(k).edge;

                    M message = sendMsg.apply(new EdgeTriplet<NV, EV>(from, to, edge));
                    if (message == null) continue;

                    if (!messages.containsKey(to)) {
                        messages.put(to, new ArrayList<>());
                        activeNodesNew.add(to);

                        if (inactiveNodes.contains(to)) {
                            inactiveNodes.remove(to);
                        } else {
                            reactivatedActiveNodes.add(to);
                        }
                    }
                    messages.get(to).add(message);
                }

                if (!reactivatedActiveNodes.contains(activeNodes.get(j))) {
                    inactiveNodes.add(activeNodes.get(j));
                }
            }

            activeNodes = activeNodesNew;
            if (activeNodes.isEmpty()) break;

            activeNodes = activeNodes.stream().parallel().peek(x -> x.setValue(vertexFunction.apply(x, messages.get(x)))).collect(Collectors.toList());
        }
        return graph;
    }

    public static <NV, EV, M> Graph<NV, EV> apply(Graph<NV, EV> graph, int maxSuperSteps, BiFunction<ExtendedNode<NV, EV>, List<M>, NV> vertexFunction, Function<EdgeTriplet<NV, EV>, M> sendMsg) {
        return apply(graph, maxSuperSteps, vertexFunction, sendMsg, x -> {});
    }
}