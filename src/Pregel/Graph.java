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


        for (Edge<EV> edge : edges) {
            if (edge.sourceId > nodes.size() || edge.dstId > nodes.size()) {
                throw new ArrayIndexOutOfBoundsException("Unknown id in edges list: " + edge.sourceId + "->" + edge.dstId);
            }
            this.nodes[edge.sourceId].addEdge(edge, this.nodes[edge.dstId]);
        }
    }

    public static Graph<Integer, Boolean> computeSimpleGraph(File edgeFile, String separator, int verticesCount, boolean addOppositeEdges) throws IOException {
        ExtendedNode<Integer, Boolean>[] nodes = new ExtendedNode[verticesCount];

        HashMap<Integer, Integer> mapping = new HashMap<>(verticesCount);
        int maxKey = 0;

        BufferedReader reader;
        reader = new BufferedReader(new FileReader(edgeFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] split = line.split(separator);
            Integer fromTmp = Integer.valueOf(split[0]);
            Integer toTmp = Integer.valueOf(split[1]);

            Integer from = mapping.putIfAbsent(fromTmp, maxKey);
            if (from == null) {
                from = maxKey;
                nodes[from] = new ExtendedNode<Integer, Boolean>(new Node<>(fromTmp, maxKey++));
            }
            Integer to = mapping.putIfAbsent(toTmp, maxKey);
            if (to == null) {
                to = maxKey;
                nodes[to] = new ExtendedNode<Integer, Boolean>(new Node<>(toTmp, maxKey++));
            }
            nodes[from].addEdge(new Edge<>(true, from, to), nodes[to]);

            if (addOppositeEdges) {
                nodes[to].addEdge(new Edge<>(true, to, from), nodes[from]);
            }
        }

        return new Graph<Integer, Boolean>(nodes);
    }

    private Graph(ExtendedNode<NV, EV>[] nodes) {
        this.nodes = nodes;
    }

    public Stream<EdgeTriplet<NV, EV>> toEdgeStream() {
        return Arrays.stream(nodes).flatMap(lst -> lst.getNeighbors().stream().map(ec -> new EdgeTriplet<NV, EV>(lst, ec.to, ec.edge)));
    }

    public Stream<Node<NV>> toNodeStream() {
        return Arrays.stream(nodes).map(extNode -> extNode.node);
    }

    public Stream<ExtendedNode<NV, EV>> toExtendedNodeStream() {
        return Arrays.stream(nodes);
    }

    public <NV2, EV2> Graph<NV2, EV2> mapTo(Function<NV, NV2> mapNodes, Function<EV, EV2> mapEdges) {
        ExtendedNode<NV2, EV2>[] newNodes = Arrays.stream(this.nodes).map(x -> new ExtendedNode<NV2, EV2>(new Node<>(mapNodes.apply(x.getValue()), x.node.id), new ArrayList<>())).toArray(size -> new ExtendedNode[size]);
        for (int i = 0; i < this.nodes.length; i++) {
            for (int j = 0; j < this.nodes[i].getNeighbors().size(); j++) {
                Edge<EV> cur = this.nodes[i].getNeighbors().get(j).edge;
                newNodes[i].addEdge(new Edge<>(mapEdges.apply(cur.getValue()), cur.sourceId, cur.dstId), newNodes[cur.dstId]);
            }
        }
        return new Graph<NV2, EV2>(newNodes);
    }

    public long countNodes() {
        return nodes.length;
    }

    public void removeSelfEdges() {
        for (ExtendedNode<NV, EV> node : nodes) {
            node.getNeighbors().removeIf(x -> x.edge.dstId == x.edge.sourceId);
        }
    }

    //For undirected graphs where every edge is 2times in edges (one for each direction)
    public Graph<NV, EV> sampleEdgesBidirectional(double s) {
        double dAVG = toEdgeStream().count() / (double) nodes.length;

        long edgeCount = toEdgeStream().count();

        for (ExtendedNode<NV, EV> node : nodes) {
            for (int j = 0; j < node.getNeighbors().size(); j++) {
                final ExtendedNode<NV, EV> from = node;
                final ExtendedNode<NV, EV> to = node.getNeighbors().get(j).to;

                //Prevents edges from been tested twice since for undirected graph every edge
                //is twice in the graph: one time for x->y one time for y->x
                if (from.hashCode() < to.hashCode()) continue;

                double degreeIn = from.getNeighbors().size();
                double degreeOut = to.getNeighbors().size();
                double sparsifier = (dAVG * s) / Math.min(degreeIn, degreeOut);
                if (Math.random() > sparsifier) {
                    //In the case of x->x we just need to remove one edge
                    if (from != to) {
                        to.getNeighbors().removeIf(x -> x.to == from);
                    }
                    from.getNeighbors().remove(j);
                    j--;
                }
            }
        }

        System.out.println("Edgesampling: Before " + edgeCount + " edges now " + toEdgeStream().count() + " edges!");
        return this;
    }
}
