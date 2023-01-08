package Pregel;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class Graph<NV, EV> {
    private ExtendedNode<NV, EV>[] nodes;

    public Graph(List<Node<NV>> nodes, List<Edge<EV>> edges) {
        this.nodes = new ExtendedNode[nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            if (nodes.get(i).id > nodes.size()) {
                throw new ArrayIndexOutOfBoundsException("Unknown id in edges list");
            }
            this.nodes[nodes.get(i).id] = new ExtendedNode<NV, EV>(nodes.get(i));
        }


        for (int i = 0; i < edges.size(); ++i) {
            if (edges.get(i).sourceId > nodes.size() || edges.get(i).dstId > nodes.size()) {
                throw new ArrayIndexOutOfBoundsException("Unknown id in edges list: " + edges.get(i).sourceId + "->" + edges.get(i).dstId);
            }
            this.nodes[edges.get(i).sourceId].addEdge(edges.get(i), this.nodes[edges.get(i).dstId]);
        }
    }

    public Graph(File edgeFile, String separator, int verticesCount, NV defaultVertexValue, EV defaultEdgeValue, boolean addOppositeEdges) throws IOException {
        this.nodes = new ExtendedNode[verticesCount];

        HashMap<Integer, Integer> mapping = new HashMap<>(verticesCount);
        int maxKey = 0;

        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(edgeFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] split = line.split(separator);
            Integer fromTmp = Integer.valueOf(split[0]);
            Integer toTmp = Integer.valueOf(split[1]);

            Integer from = mapping.putIfAbsent(fromTmp, maxKey);
            if (from == null) {
                from = maxKey;
                this.nodes[from] = new ExtendedNode<NV, EV>(new Node<NV>(defaultVertexValue, maxKey++));
            }
            Integer to = mapping.putIfAbsent(toTmp, maxKey);
            if (to == null) {
                to = maxKey;
                this.nodes[to] = new ExtendedNode<NV, EV>(new Node<NV>(defaultVertexValue, maxKey++));
            }

            if (this.nodes[to] == null || this.nodes[from] == null) {
                return;
            }

            this.nodes[from].addEdge(new Edge<>(defaultEdgeValue, from, to), this.nodes[to]);

            if (addOppositeEdges) {
                this.nodes[to].addEdge(new Edge<>(defaultEdgeValue, to, from), this.nodes[from]);
            }
        }
    }

    private Graph(ExtendedNode<NV, EV>[] nodes) {
        this.nodes = nodes;
    }

    public Stream<EdgeTriplet<NV, EV>> toEdgeStream() {
        return Arrays.stream(nodes).flatMap(lst -> lst.getNeighbors().stream().map(ec -> new EdgeTriplet<NV, EV>(lst.node, ec.to.node, ec.edge)));
    }

    public Stream<Node<NV>> toNodeStream() {
        return Arrays.stream(nodes).map(extNode -> extNode.node);
    }

    Stream<ExtendedNode<NV, EV>> toExtendedNodeStream() {
        return Arrays.stream(nodes);
    }

    public <NV2, EV2> Graph<NV2, EV2> mapTo(Function<NV, NV2> mapNodes, Function<EV, EV2> mapEdges) {
        ExtendedNode<NV2, EV2>[] newNodes = Arrays.stream(this.nodes).map(x -> new ExtendedNode<NV2, EV2>(new Node<NV2>(mapNodes.apply(x.getValue()), x.node.id), new ArrayList<>())).toArray(size -> new ExtendedNode[size]);
        for (int i = 0; i < this.nodes.length; i++) {
            for (int j = 0; j < this.nodes[i].getNeighbors().size(); j++) {
                Edge<EV> cur = this.nodes[i].getNeighbors().get(j).edge;
                newNodes[i].addEdge(new Edge<EV2>(mapEdges.apply(cur.getValue()), cur.sourceId, cur.dstId), newNodes[cur.dstId]);
            }
        }
        return new Graph<NV2, EV2>(newNodes);
    }
}
