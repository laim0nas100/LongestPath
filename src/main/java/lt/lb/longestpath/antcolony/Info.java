/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.antcolony;

import java.util.Map;
import java.util.Optional;
import lt.lb.commons.containers.NumberValue;
import lt.lb.commons.containers.Pair;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.RandomDistribution;

/**
 *
 * @author Lemmin
 */
public class Info {

    public Orgraph graph;
    public RandomDistribution rng;

    public Optional<NumberValue<Double>> getPheromone(Pair<Long> pair) {
        if (pair.g1 > pair.g2) {
            pair = pair.reverse();
        }
        if (linkPheromones.containsKey(pair)) {
            return Optional.of(linkPheromones.get(pair));
        } else {
            return Optional.empty();
        }

    }

    public void setPheromone(Pair<Long> pair, Double val) {
        if (pair.g1 > pair.g2) {
            pair = pair.reverse();
        }

        linkPheromones.put(pair, NumberValue.of(val));
    }

    public Map<Pair<Long>, NumberValue<Double>> linkPheromones;
}
