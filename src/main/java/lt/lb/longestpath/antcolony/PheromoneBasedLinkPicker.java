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
import lt.lb.commons.containers.tuples.Tuple3;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.GNode;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.PathGenerator.ILinkPicker;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.commons.misc.rng.RandomRange;
import lt.lb.commons.misc.rng.RandomRanges;

/**
 *
 * @author Lemmin
 */
public class PheromoneBasedLinkPicker implements ILinkPicker {

    public PheromoneBasedLinkPicker(ACSinfo info) {
        this.info = info;
    }
    public ACSinfo info;

    @Override
    public Optional<GLink> apply(Tuple3<Orgraph, Set<Long>, GNode> t) {
        GNode g2 = t.g3;
        Orgraph graph = t.g1;
        Set<Long> visited = t.g2;
        List<GLink> links = graph.resolveLinkedTo(g2, n -> !visited.contains(n));
        if (links.isEmpty()) {
            return Optional.empty();
        }
        ArrayList<RandomRange<GLink>> linkRange = new ArrayList<>();
        RandomDistribution rng = info.rng.get();
        boolean useGreed = info.useGreed < rng.nextDouble();
        boolean useDegree = useGreed && (info.greedDegree < rng.nextDouble());
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
                if (useDegree) {
                    int nodeDegree = graph.getNode(to).get().linksTo.size();
                    //apply greedy node degree nodifier
                    sum *= nodeDegree;
                } else {
                    sum += link.weight;
                }

            }

            linkRange.add(new RandomRange<>(link, sum));
        });

        RandomRanges range = new RandomRanges(linkRange);
        Double nextDouble = rng.nextDouble(range.getLimit());
        RandomRange<GLink> randomRange = range.pickRandom(nextDouble);
        return Optional.of(randomRange.get());
    }

}
