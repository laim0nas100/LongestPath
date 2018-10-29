/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lt.lb.commons.ArrayOp;
import lt.lb.commons.containers.tuples.Pair;
import lt.lb.commons.graphtheory.*;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.interfaces.Equator;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.longestpath.API;

/**
 *
 * @author laim0nas100
 */
public class GeneticSolution {

    public static GraphAgent mergedGenome(Orgraph gr, List<Long> first, Pair<Long> middle, List<Long> last) {
        List<Long> list = new ArrayList<>();
        list.addAll(first);
        if (!first.contains(middle.g1)) {
            list.add(middle.g1);
        }
        if (!last.contains(middle.g2)) {
            list.add(middle.g2);
        }
        list.addAll(last);
        return new GraphAgent(list, gr);
    }

    public static ArrayList<GraphAgent> crossoverCommonLink(Orgraph gr, GraphAgent g1, GraphAgent g2, Pair<Long> bridge) {
        //assume they are valid for crossover

        if (g1.nodes.contains(bridge.g1)) {
            if (g2.nodes.contains(bridge.g1)) {
                bridge = bridge.reverse();
            } else {
                throw new IllegalArgumentException("cant create bridge: " + bridge + " " + g1 + g2);
            }
        } else if (g2.nodes.contains(bridge.g1)) {
            if (!g1.nodes.contains(bridge.g2)) {
                throw new IllegalArgumentException("cant create bridge: " + bridge.reverse() + " " + g1 + g2);
            }
            bridge = bridge.reverse();
        } else {
            throw new IllegalArgumentException("cant create bridge: " + bridge + " " + g1 + g2);
        }

        Pair<Long> b = bridge;

        int cut1 = F.find(g1.path, (i, l) -> {
            return Objects.equals(l, b.g1);
        }).get().g1;
        int cut2 = F.find(g2.path, (i, l) -> {
            return Objects.equals(l, b.g2);
        }).get().g1;

        List<Long>[] subpaths = ArrayOp.replicate(4, List.class, () -> new ArrayList<>());
        //cut1

        F.iterate(g1.path, (i, l) -> {
            if (i < cut1) {
                subpaths[0].add(l);
            } else {
                subpaths[1].add(l);
            }
        });
        F.iterate(g2.path, (i, l) -> {
            if (i < cut2) {
                subpaths[2].add(l);
            } else {
                subpaths[3].add(l);
            }
        });

        Log.print("Crossover things");
        Log.print("Bridge", bridge);
        Log.print("Parents:");
        Log.print(g1);
        Log.print(g2);
        Log.print("Cuts:", cut1, cut2);
        for (List<Long> list : subpaths) {
            Log.print(list);
        }

        /*
         * children
         * 0 + link + 2
         * 0 + link + 3
         * 1 + link + 2
         * 1 + link + 3
         */
        ArrayList<GraphAgent> children = new ArrayList<>();
        children.add(mergedGenome(gr, subpaths[0], b, subpaths[3]));
        children.add(mergedGenome(gr, subpaths[0], b, API.reversed(subpaths[2])));
        children.add(mergedGenome(gr, subpaths[2], b.reverse(), subpaths[1]));
        children.add(mergedGenome(gr, API.reversed(subpaths[3]), b.reverse(), subpaths[1]));

        Log.print("Children:");
        F.iterate(children, (i, c) -> {
            Log.print(i, c);
        });

        F.iterate(children, (i, g) -> {
            if (g.nodes.isEmpty()) {
                Log.print(g.id, "is empty after");
            }
        });

//        F.filterParallel(children, a->!a.nodes.isEmpty(), r->r.run());
        return children;
    }

    public static GraphAgent mutate(RandomDistribution rnd, Orgraph gr, GraphAgent g) {
        if (g.nodes.isEmpty()) {
            throw new IllegalArgumentException(g.id + g + " is empty");
        }

        Log.print("\nValid before?", API.isPathValid(gr, g.path), g.path);
        Integer indexOf = rnd.nextInt(g.path.size());
        boolean left = rnd.nextBoolean();
        long startNode = g.path.get(indexOf);
        List<Long> nodes = new ArrayList<>();
        F.iterate(g.path, (i, n) -> {
            if (left) {
                if (i <= indexOf) {
                    nodes.add(n);
                }
            } else if (i >= indexOf) {
                nodes.add(n);
            }
        });
        if (!left) {
            Collections.reverse(nodes);
        }
        Log.print("Mutation node", startNode, "@", indexOf, "left?", left);
        Log.print("Mutate:", g.path);
        Log.print("Cut path", nodes);
        ArrayList<GLink> path = API.getLinks(nodes, gr);
        Log.print("Got path", path);
        Set<Long> visited = new HashSet<>(nodes);

        List<GLink> genericUniquePathVisitContinued = PathGenerator.genericUniquePathVisitContinued(gr, startNode, path, visited, PathGenerator.nodeDegreeDistributed(rnd));
        ArrayList<Long> nodesIDs = API.getNodesIDs(genericUniquePathVisitContinued);
        Log.print("New nodes:", nodesIDs);
        return new GraphAgent(nodesIDs, gr);
    }

    public static ArrayList<GLink> getPossibleLinks(Orgraph gr, GNode n1, List<GNode> nodes) {
        ArrayList<GLink> links = new ArrayList<>();

        F.iterate(nodes, (i, n) -> {
            if (n1.linkedFrom.contains(n.ID)) {
                links.add(gr.getLink(n.ID, n1.ID).get());
            }
            if (n1.linksTo.contains(n.ID)) {
                links.add(gr.getLink(n1.ID, n.ID).get());
            }
        });
        return links;
    }

    public static ArrayList<GLink> getBridges(Orgraph gr, List<GLink> path1, List<GLink> path2) {

        List<GNode> nodes1 = API.getNodes(gr, path1);
        List<GNode> nodes2 = API.getNodes(gr, path2);

        ArrayList<GLink> bridges = new ArrayList<>();

        F.iterate(nodes1, (i, n) -> {
            bridges.addAll(getPossibleLinks(gr, n, nodes2));
        });
        Stream<GLink> stream = bridges.stream().filter(F.filterDistinct(GLink::equalNodesBidirectional));
        return F.fillCollection(stream,new ArrayList<>());

    }

}
