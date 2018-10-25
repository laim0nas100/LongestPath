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

/**
 *
 * @author Lemmin
 */
public class PheromoneBasedLinkPicker implements ILinkPicker {

    public static class RandomCollectiveRange<T> {

        public List<RandomRange<T>> ranges;

        public Double getLimit() {
            double lim = 0;
            for (RandomRange rr : ranges) {
                lim += rr.sum;
            }
            return lim;
        }

        public RandomCollectiveRange(List<RandomRange<T>> ranges) {

            F.filterInPlace(ranges, r -> r.sum > 0d);
            if (ranges.isEmpty()) {
                throw new IllegalArgumentException("No positive ranges found");
            }
            this.ranges = ranges;
        }

        public RandomRange<T> pickMax() {
            return this.ranges.stream().sorted((r1, r2) -> Double.compare(r1.sum, r2.sum)).findFirst().get();
        }

        public RandomRange<T> pickMin() {
            return this.ranges.stream().sorted((r1, r2) -> -Double.compare(r1.sum, r2.sum)).findFirst().get();
        }

        public RandomRange<T> pickRandom(Double dub) {
            Double limit = this.getLimit();
            if (dub < 0 || dub > limit) {
                throw new IllegalArgumentException(dub + " limit is " + limit);
            }
            double cur = 0;
            int selected = 0;
            for (int i = 0; i < ranges.size(); i++) {
                RandomRange rr = ranges.get(i);
                cur += rr.sum;
                if (cur > dub) {
                    selected = i;
                    break;
                }
                
            }
            return ranges.get(selected);
        }

    }

    public static class RandomRange<T> {

        public T val;
        public Double sum;

        public RandomRange(T name, Double sum) {
            this.val = name;
            this.sum = sum;
        }
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
        F.iterate(links, (i, link) -> {
            long from, to;
            from = link.nodeFrom;
            to = link.nodeTo;
            Optional<NumberValue<Double>> pheromone = info.getPheromone(new Pair<>(from, to));
            double sum = 1;

            if (pheromone.isPresent()) {
                sum = 1 - pheromone.get().get();
                sum = clamper.clamp(sum);
            }
            int nodeDegree = graph.getNode(from).get().linksTo.size();
            //apply greedy node degree nodifier
            sum *= nodeDegree;
            
            linkRange.add(new RandomRange<>(link, sum));
        });

        RandomCollectiveRange range = new RandomCollectiveRange(linkRange);
        Double nextDouble = info.rng.nextDouble(range.getLimit());
        RandomRange<GLink> randomRange = range.pickRandom(nextDouble);
        return Optional.of(randomRange.val);
    }

}
