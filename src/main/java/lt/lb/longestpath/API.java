/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lt.lb.commons.ArrayOp;
import lt.lb.commons.F;
import lt.lb.commons.Lambda;
import lt.lb.commons.containers.tuples.Pair;
import lt.lb.commons.containers.tuples.Tuple;
import lt.lb.commons.containers.tuples.Tuple3;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.GNode;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.PathGenerator.ILinkPicker;
import lt.lb.commons.interfaces.Equator;
import lt.lb.commons.interfaces.ReadOnlyIterator;
import lt.lb.commons.io.FileReader;
import lt.lb.commons.misc.ExtComparator;
import lt.lb.commons.misc.rng.RandomDistribution;

/**
 *
 * @author Lemmin
 */
public class API {

    public static Lambda.L1R<GLink, Pair<Long>> link2Pair = link -> new Pair<>(link.nodeFrom, link.nodeTo);
    public static Lambda.L1R<Pair<Long>, GLink> pair2link = p -> new GLink(p.g1, p.g2, 1d);
    public static Equator<List<Long>> pathEquator = Objects::equals;

    public static <T> ArrayList<T> reversed(List<T> list) {
        ArrayList<T> reversed = new ArrayList<>();
        reversed.addAll(list);
        Collections.reverse(reversed);
        return reversed;
    }

    public static ArrayList<Long> getNodesIDs(List<GLink> path) {
        ArrayList<Long> nodes = new ArrayList<>();
        if (path.isEmpty()) {
            return nodes;
        }
        nodes.add(path.get(0).nodeFrom);
        F.iterate(path, (i, link) -> {
            nodes.add(link.nodeTo);
        });
        return nodes;
    }

    public static ArrayList<GLink> getLinks(List<Long> nodes, Orgraph gr) {
        ArrayList<GLink> links = new ArrayList<>(nodes.size());
        Long[] arr = ArrayOp.newArray(nodes, Long.class);
        for (int i = 1; i < arr.length; i++) {
            links.add(gr.getLink(arr[i - 1], arr[i]).get());
        }
        return links;

    }

    public static ArrayList<GNode> getNodes(Orgraph gr, List<GLink> path) {
        ArrayList<GNode> nodes = new ArrayList<>();
        if (path.isEmpty()) {
            return nodes;
        }
        F.iterate(getNodesIDs(path), (i, ID) -> {
            Optional<GNode> node = gr.getNode(ID);
            nodes.add(node.get());
        });
        return nodes;
    }

    public static List<Long> getIntersections(Orgraph gr, List<GLink> path1, List<GLink> path2) {
        List<GNode> nodes1 = getNodes(gr, path1);
        List<GNode> nodes2 = getNodes(gr, path2);
        Set<Long> nodeTable1 = nodes1.stream().map(n -> n.ID).collect(Collectors.toSet());
        Set<Long> nodeTable2 = nodes2.stream().map(n -> n.ID).collect(Collectors.toSet());
        return nodeTable1.stream().filter(n -> nodeTable2.contains(n)).collect(Collectors.toList());
    }

    public static <T> ArrayList<T> copy(Collection<T> col) {
        ArrayList<T> copy = new ArrayList<>(col.size());
        copy.addAll(col);
        return copy;
    }

    /**
     *
     * @param gr
     * @param nodes
     * @return Yes/Reason
     */
    public static String isPathValid(Orgraph gr, List<Long> nodes) {
        for (int i = 1; i < nodes.size(); i++) {
            Long prev = nodes.get(i - 1);
            Long n = nodes.get(i);

            if (gr.linkExists(prev, n)) {
                // all good
            } else {
                return "No such link:" + prev + " -> " + n;
            }
        }
        return "Yes";
    }

    public static void exportGraph(Orgraph gr, String path) throws FileNotFoundException, UnsupportedEncodingException {
        ReadOnlyIterator<String> iter = ReadOnlyIterator.of(gr.links.values().stream().sorted(linkComparatorPretty).map(link -> link.nodeFrom + " " + link.nodeTo + " " + link.weight));
        FileReader.writeToFile(path, iter);
    }

    public static void importGraph(Orgraph gr, String path) throws FileNotFoundException, IOException {
        ArrayList<String> readFromFile = FileReader.readFromFile(path);
        F.iterate(readFromFile, (i, line) -> {
            String[] s = line.split(" ");
            Long nodeFrom = Long.parseLong(s[0]);
            Long nodeTo = Long.parseLong(s[1]);
            Double w = Double.parseDouble(s[2]);
            gr.addLink(gr.newLink(nodeFrom, nodeTo, w));
        });
    }

    public static final ExtComparator<GLink> linkComparatorPretty = ExtComparator.ofValues(a -> a.nodeFrom, a -> a.nodeTo);

    public static ILinkPicker probabilityJoinedPickers(Collection<Tuple<Double, ILinkPicker>> pickers, RandomDistribution rng) {
        return (Tuple3<Orgraph, Set<Long>, GNode> t) -> {
            LinkedList<ILinkPicker> picker = rng.pickRandomDistributed(1, pickers);
            if (picker.isEmpty()) {
                return Optional.empty();
            }
            return picker.getFirst().apply(t);
        };
    }
}
