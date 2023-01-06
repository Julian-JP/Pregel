package Pregel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Pregel {
    public static <NV, EV, M> Graph<NV, EV> apply(Graph<NV, EV> graph, M initialMessage, int maxSupersteps, BiFunction<NV, List<M>, NV> vertexFunction, Function<EdgeTriplet<NV, EV>, M> sendMsg) {
        long startTime = System.currentTimeMillis();
        long countMessages = 0;
        List<ExtendedNode<NV, EV>> activeNodes = graph.toExtendedNodeStream().collect(Collectors.toList());
        HashSet<ExtendedNode<NV, EV>> inactiveNodes = new HashSet<>();

        for (int i = 0; i < maxSupersteps; i++) {

            HashMap<ExtendedNode<NV, EV>, List<M>> messages = new HashMap<>();
            List<ExtendedNode<NV, EV>> activeNodesNew = new ArrayList<>(activeNodes.size());
            HashSet<ExtendedNode<NV, EV>> reactivatedActiveNodes = new HashSet<>();


            for (int j = 0; j < activeNodes.size(); j++) {
                for (int k = 0; k < activeNodes.get(j).getNeighbors().size(); k++) {
                    ExtendedNode from = activeNodes.get(j);
                    ExtendedNode to = activeNodes.get(j).getNeighbors().get(k).to;
                    Edge<EV> edge = activeNodes.get(j).getNeighbors().get(k).edge;

                    M message = sendMsg.apply(new EdgeTriplet<NV, EV>(from.node, to.node, edge));
                    if (message == null) continue;

                    countMessages++;

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
            if (activeNodes.isEmpty()) {
                break;
            }


            for (int j = 0; j < activeNodes.size(); j++) {
                activeNodes.get(j).setValue(vertexFunction.apply(activeNodes.get(j).getValue(), messages.get(activeNodes.get(j))));
            }
        }

        long timeElapsed = System.currentTimeMillis() - startTime;
        System.out.println("Pregel finished with " + countMessages + " messages in " + timeElapsed + "ms");
        return graph;
    }
}
