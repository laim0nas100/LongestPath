/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.antcolony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lt.lb.commons.F;
import lt.lb.commons.containers.NumberValue;
import lt.lb.commons.containers.Value;
import lt.lb.commons.containers.tuples.Pair;
import lt.lb.commons.graphtheory.Algorithms;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.GNode;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.misc.ExtComparator;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.longestpath.API;

/**
 *
 * @author Lemmin
 */
public class ACS {

    public ACS() {

    }

    public ExtComparator<AntBoi> cmp = ExtComparator.of((a, b) -> Double.compare(a.cost.get(), b.cost.get()));
    public ArrayList<AntBoi> bests = new ArrayList<>();
    public int antCount = 10;

    public ArrayList<Long> constructInitialSolution(Info info) {
        HashMap<Long, GNode> nodes = info.graph.nodes;
        Integer node = info.rng.nextInt(nodes.size());
        List<GLink> generateLongPathBidirectional = PathGenerator.generateLongPathBidirectional(info.graph, node, PathGenerator.nodeDegreeDistributed(info.rng));
        return API.getNodesIDs(generateLongPathBidirectional);

    }

    public AntBoi search(Info info, int iterations, int ants, double cLocalPhero, double decay) {
        Value<AntBoi> best = new Value<>(new AntBoi(info, this.constructInitialSolution(info)));
        RandomDistribution rng = info.rng;
        double initPheromone = 1d / best.get().cost.get();
        //init pheromone table
        info.linkPheromones = new HashMap<>();
        Long nodeSize = (long) info.graph.nodes.size();
        F.iterate(info.graph.links.values(), (i, link) -> {
            Pair<Long> pair = new Pair<>(link.nodeFrom, link.nodeTo);
            info.setPheromone(pair, initPheromone);
        });
        PheromoneBasedLinkPicker picker = new PheromoneBasedLinkPicker(info);
        F.repeat(iterations, i -> {
            F.repeat(ants, j -> {
                Long randomNode = rng.nextLong(nodeSize);
                List<GLink> generateLongPathBidirectional = PathGenerator.generateLongPathBidirectional(info.graph, randomNode, picker);
                ArrayList<Long> nodes = API.getNodesIDs(generateLongPathBidirectional);
                AntBoi ant = new AntBoi(info, nodes);
                AntBoi prevBest = best.get();
                best.set(cmp.max(best.get(), ant));

                if (prevBest != best.get()) {
                    this.bests.add(prevBest);
                }
                this.localPheromoneUpdate(info, ant, cLocalPhero, initPheromone);
            });
            this.globalPheromoneUpdate(info, best.get(), decay);

        });
        return best.get();
    }

    public void iteration() {

    }

    public void localPheromoneUpdate(Info info, AntBoi ant, double localPhero, double initPhero) {
        ArrayList<GLink> links = API.getLinks(ant.currentPath, info.graph);
        F.iterate(links, (i, link) -> {
            Pair<Long> pair = new Pair<>(link.nodeFrom, link.nodeTo);
            NumberValue<Double> pheromone = info.getPheromone(pair);
            double val = (1d - localPhero) * pheromone.get() + (localPhero * initPhero);
            pheromone.set(val);
        });
    }

    public void globalPheromoneUpdate(Info info, AntBoi ant, double decay) {
        ArrayList<GLink> links = API.getLinks(ant.currentPath, info.graph);
        F.iterate(links, (i, link) -> {
            Pair<Long> pair = new Pair<>(link.nodeFrom, link.nodeTo);
            double cost = Algorithms.getPathWeight(links);
            NumberValue<Double> pheromone = info.getPheromone(pair);
            double val = (1d - decay) * pheromone.get() + (decay * cost);
            pheromone.set(val);
        });
    }

}
