/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.antcolony;

import java.util.Map;
import lt.lb.commons.containers.NumberValue;
import lt.lb.commons.containers.tuples.Pair;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.RandomDistribution;

/**
 *
 * @author Lemmin
 */
public class Info {

    public Orgraph graph;
    public double useGreed = 0.5;
    public RandomDistribution rng;

    public NumberValue<Double> getPheromone(Pair<Long> pair) {
        if (pair.g1 > pair.g2) {
            pair = pair.reverse();
        }
        if (linkPheromones.containsKey(pair)) {
            return linkPheromones.get(pair);
        } else {
            return null;
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
