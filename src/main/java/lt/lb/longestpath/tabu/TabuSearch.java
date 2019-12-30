/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.tabu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.containers.values.NumberValue;
import lt.lb.commons.containers.tuples.Tuple;
import lt.lb.commons.containers.tuples.Tuple3;
import lt.lb.commons.graphtheory.Algorithms;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.commons.misc.rng.RandomRange;
import lt.lb.commons.misc.rng.RandomRanges;
import lt.lb.longestpath.API;
import lt.lb.longestpath.anneal.Annealing;
import lt.lb.longestpath.anneal.Annealing.PathProduce;

/**
 *
 * @author laim0nas100
 */
public class TabuSearch {

    public static void tabuSolutions(Orgraph gr, RandomDistribution rng, TabuInfo info, TabuResult result) {
        Long pickRandom = rng.pickRandom(gr.nodes.keySet());

        RandomRange<PathGenerator.ILinkPicker> r1 = new RandomRange(PathGenerator.nodeDegreeDistributed(rng, false), 0.5);
        RandomRange<PathGenerator.ILinkPicker> r2 = new RandomRange(PathGenerator.nodeWeightDistributed(rng, false), 0.5);

        PathGenerator.ILinkPicker linkPickerCombined = PathGenerator.linkPickerCombined(new RandomRanges<>(r1, r2), rng);

        List<GLink> pathLinks = PathGenerator.generateLongPathBidirectional(gr, pickRandom, linkPickerCombined);
        List<Long> path = API.getNodesIDs(pathLinks);

        result.bestSoFar = path;
        result.bestSoFarWeight = Algorithms.getPathWeight(path, gr);

        int i = 1;

        long stagnation = 0;

        double tabuDecay = 0.99;

        HashMap<List<Long>, NumberValue<Double>> tabu = new HashMap<>();

        while (i < info.iterationLimit) {
            i++;
            Annealing.NeighborhoodTuple hood = Annealing.neighborhoodDefault(gr, path, 0);
            result.failedGenerations += hood.fails.get();
            result.successGenerations += hood.success.get();
            F.iterate(tabu, (k, v) -> {
                v.set(v.multiplyAndGet(tabuDecay));
            });
            int improvements = 0;
            List<RandomRange<Tuple3<List<Long>, Double, PathProduce>>> ranges = new ArrayList<>();
            for (Tuple<List<Long>, Annealing.PathProduce> tuplePath : hood.paths) {

                result.weightEvaluations++;

                List<Long> newPath = tuplePath.g1;
                // optimization to append on the ends
                long lastNode = newPath.get(newPath.size() - 1);
                ArrayList<GLink> links = API.getLinks(newPath, gr);

                List<GLink> betterPath = PathGenerator.genericUniquePathBidirectionalVisitContinued(gr, lastNode, links, new HashSet<>(newPath), linkPickerCombined);
                newPath = API.getNodesIDs(betterPath);
                double newPathLength = Algorithms.getPathWeight(newPath, gr);
                if (newPathLength > result.bestSoFarWeight) {
                    result.bestSoFarWeight = newPathLength;
                    result.bestSoFar = newPath;
                    improvements++;
                }
                double freq = tabu.getOrDefault(newPath, NumberValue.of(1d)).get();
                ranges.add(new RandomRange(new Tuple3(newPath, newPathLength, tuplePath.g2), newPathLength / freq));
            }
            RandomRanges<Tuple3<List<Long>, Double, PathProduce>> rr = new RandomRanges(ranges);

            RandomRange<Tuple3<List<Long>, Double, PathProduce>> pick = rr.pickRandom(rng.nextDouble());
            Tuple3<List<Long>, Double, PathProduce> get = pick.get();
            if (tabu.getOrDefault(get.g1, NumberValue.of(0d)).get() < 1) {
                path = get.g1;
                if (tabu.containsKey(path)) {
                    tabu.get(path).incrementAndGet();
                } else {
                    tabu.put(path, NumberValue.of(1d));
                }
            }
            if (improvements == 0) {
                stagnation++;
            } else {
                stagnation = 0;
            }
            Log.print("Iteration ", i, "Improvements ", improvements);
            result.straightImprovements += improvements;
            if (stagnation > info.maxStagnation) {
                result.reachedIteration = i;
                result.maxStagnationReached = stagnation;
                break;
            }
        }

    }

    public static void tabuMoves(Orgraph gr, RandomDistribution rng, TabuInfo info, TabuResult result) {
        Long pickRandom = rng.pickRandom(gr.nodes.keySet());

        RandomRange<PathGenerator.ILinkPicker> r1 = new RandomRange(PathGenerator.nodeDegreeDistributed(rng, false), 0.5);
        RandomRange<PathGenerator.ILinkPicker> r2 = new RandomRange(PathGenerator.nodeWeightDistributed(rng, false), 0.5);

        PathGenerator.ILinkPicker linkPickerCombined = PathGenerator.linkPickerCombined(new RandomRanges<>(r1, r2), rng);

        List<GLink> pathLinks = PathGenerator.generateLongPathBidirectional(gr, pickRandom, linkPickerCombined);
        List<Long> path = API.getNodesIDs(pathLinks);

        result.bestSoFar = path;
        result.bestSoFarWeight = Algorithms.getPathWeight(path, gr);

        int i = 1;

        long stagnation = 0;

        double tabuDecay = 0.99;

        HashMap<PathProduce, NumberValue<Double>> tabu = new HashMap<>();

        while (i < info.iterationLimit) {
            i++;
            Annealing.NeighborhoodTuple hood = Annealing.neighborhoodDefault(gr, path, 0);
            result.failedGenerations += hood.fails.get();
            result.successGenerations += hood.success.get();
            F.iterate(tabu, (k, v) -> {
                v.set(v.multiplyAndGet(tabuDecay));
            });
            int improvements = 0;
            List<RandomRange<Tuple3<List<Long>, Double, PathProduce>>> ranges = new ArrayList<>();
            for (Tuple<List<Long>, Annealing.PathProduce> tuplePath : hood.paths) {

                result.weightEvaluations++;

                List<Long> newPath = tuplePath.g1;
                // optimization to append on the ends
                long lastNode = newPath.get(newPath.size() - 1);
                ArrayList<GLink> links = API.getLinks(newPath, gr);

                List<GLink> betterPath = PathGenerator.genericUniquePathBidirectionalVisitContinued(gr, lastNode, links, new HashSet<>(newPath), linkPickerCombined);
                newPath = API.getNodesIDs(betterPath);
                double newPathLength = Algorithms.getPathWeight(newPath, gr);
                if (newPathLength > result.bestSoFarWeight) {
                    result.bestSoFarWeight = newPathLength;
                    result.bestSoFar = newPath;
                    improvements++;
                }
                double freq = tabu.getOrDefault(newPath, NumberValue.of(1d)).get();
                ranges.add(new RandomRange(new Tuple3(newPath, newPathLength, tuplePath.g2), newPathLength / freq));
            }
            RandomRanges<Tuple3<List<Long>, Double, PathProduce>> rr = new RandomRanges(ranges);

            RandomRange<Tuple3<List<Long>, Double, PathProduce>> pick = rr.pickRandom(rng.nextDouble());
            Tuple3<List<Long>, Double, PathProduce> get = pick.get();
            //apply move?
            boolean applyMove = tabu.getOrDefault(get.g3, NumberValue.of(0d)).get() < 1;
            if (applyMove) {
                path = get.g1;
                if (tabu.containsKey(get.g3)) {
                    tabu.get(get.g3).incrementAndGet();
                } else {
                    tabu.put(get.g3, NumberValue.of(1d));
                }
            }
            if (improvements == 0) {
                stagnation++;
            } else {
                stagnation = 0;
            }
            Log.print("Iteration ", i, "Improvements ", improvements);
            result.straightImprovements += improvements;
            if (stagnation > info.maxStagnation) {
                result.reachedIteration = i;
                result.maxStagnationReached = stagnation;
                break;
            }
        }

    }
}
