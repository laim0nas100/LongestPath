/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.antcolony;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lt.lb.commons.F;
import lt.lb.commons.containers.NumberValue;
import lt.lb.commons.containers.tuples.Pair;
import lt.lb.commons.containers.tuples.Tuple;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.GNode;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.graphtheory.paths.PathGenerator.ILinkPicker;
import lt.lb.commons.misc.Interval;
import lt.lb.commons.misc.rng.RandomRange;
import lt.lb.commons.misc.rng.RandomRanges;

/**
 *
 * @author Lemmin
 */
public class PheromoneBasedLinkPicker implements ILinkPicker {

    
    public PheromoneBasedLinkPicker(Info info){
        this.info = info;
    }
    public Info info;

    @Override
    public Optional<GLink> apply(Tuple<PathGenerator.GraphInfo, GNode> t) {
        PathGenerator.GraphInfo graphInfo = t.g1;
        GNode g2 = t.g2;
        Orgraph graph = graphInfo.graph;
        Set<Long> visited = graphInfo.visitedNodes;
        List<GLink> links = graph.resolveLinkedTo(g2, n -> !visited.contains(n));
        if (links.isEmpty()) {
            return Optional.empty();
        }
        ArrayList<RandomRange<GLink>> linkRange = new ArrayList<>();
        Interval clamper = new Interval(1d / graph.nodes.size(), 1d); // make low probabilty to select negative pheromone links
        boolean useGreed = info.useGreed < info.rng.nextDouble();
        F.iterate(links, (i, link) -> {
            long from, to;
            from = link.nodeFrom;
            to = link.nodeTo;
            NumberValue<Double> pheromone = info.getPheromone(new Pair<>(from, to));
            double sum = 0;

            if (pheromone != null) {
                sum = pheromone.get();
            }

            if (useGreed) {
                int nodeDegree = graph.getNode(from).get().linksTo.size();
                //apply greedy node degree nodifier
                sum *= nodeDegree;
            }

            linkRange.add(new RandomRange<>(link, sum));
        });

        RandomRanges range = new RandomRanges(linkRange);
        Double nextDouble = info.rng.nextDouble(range.getLimit());
        RandomRange<GLink> randomRange = range.pickRandom(nextDouble);
        return Optional.of(randomRange.value);
    }

}
