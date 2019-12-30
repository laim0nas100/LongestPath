/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.antcolony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.containers.values.BooleanValue;
import lt.lb.commons.containers.values.NumberValue;
import lt.lb.commons.containers.values.Value;
import lt.lb.commons.containers.tuples.Pair;
import lt.lb.commons.containers.values.IntegerValue;
import lt.lb.commons.graphtheory.Algorithms;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.GNode;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.misc.ExtComparator;
import lt.lb.commons.threads.FastWaitingExecutor;
import lt.lb.commons.threads.TaskBatcher;
import lt.lb.longestpath.API;

/**
 *
 * @author Lemmin
 */
public class ACS {

    public ACS() {

    }

    public IntegerValue iteration;
    public IntegerValue currStagnation;
    public ExtComparator<AntBoi> cmp = ExtComparator.of((a, b) -> Double.compare(a.cost.get(), b.cost.get()));
    public ArrayList<AntBoi> bests = new ArrayList<>();

    public ArrayList<Long> constructInitialSolution(ACSinfo info) {
        HashMap<Long, GNode> nodes = info.graph.nodes;
        Integer node = info.rng.get().nextInt(nodes.size());
        List<GLink> generateLongPathBidirectional = PathGenerator.generateLongPathBidirectional(info.graph, node, PathGenerator.nodeDegreeDistributed(info.rng.get(), false));
        return API.getNodesIDs(generateLongPathBidirectional);

    }

    public AntBoi search(ACSinfo info, int iterations, int ants, int maxStagnation, double localPheromoneInfluence, double decay) {
        Log.print("Start ACS simulation");
        Value<AntBoi> best = new Value<>(new AntBoi(info, this.constructInitialSolution(info)));
        currStagnation = new IntegerValue(0);

        double initPheromone = 1d / best.get().cost.get();
        Log.print("Init pheromone:" + initPheromone);
        //init pheromone table
        info.linkPheromones = new HashMap<>();
        Long nodeSize = (long) info.graph.nodes.size();
        F.iterate(info.graph.links.values(), (i, link) -> {
            Pair<Long> pair = new Pair<>(link.nodeFrom, link.nodeTo);
            info.setPheromone(pair, initPheromone);
        });
        FastWaitingExecutor exeMain = new FastWaitingExecutor(ants);

        PheromoneBasedLinkPicker picker = new PheromoneBasedLinkPicker(info);
        iteration =  new IntegerValue(0);
        for (; iteration.get() < iterations; iteration.incrementAndGet()) {
            BooleanValue updated = BooleanValue.FALSE();
            ConcurrentLinkedDeque<Runnable> updates = new ConcurrentLinkedDeque<>();
            TaskBatcher batcher = new TaskBatcher(exeMain);
            for (int j = 0; j < ants; j++) {

                batcher.execute(() -> {
                    Long randomNode = info.rng.get().nextLong(nodeSize);
                    List<GLink> generateLongPathBidirectional = PathGenerator.generateLongPathBidirectional(info.graph, randomNode, picker);
                    ArrayList<Long> nodes = API.getNodesIDs(generateLongPathBidirectional);

                    updates.add(() -> {
                        AntBoi ant = new AntBoi(info, nodes);

                        AntBoi prevBest = best.get();
                        best.set(cmp.max(best.get(), ant));

                        if (prevBest != best.get()) {
                            this.bests.add(prevBest);
                            updated.setTrue();
                        }
                        this.localPheromoneUpdate(info, ant, localPheromoneInfluence, initPheromone);
                    });
                });

            }
            batcher.awaitFailOnFirst();
            for (Runnable up : updates) {
                up.run();
            }
            Log.print("Iteration " + iteration.get(), " Stagnation " + currStagnation.get());
            this.globalPheromoneUpdate(info, best.get(), decay);
            if (updated.get()) {
                currStagnation.set(0);
            } else {
                currStagnation.incrementAndGet();
            }
            if (currStagnation.get() > maxStagnation) {
                break;
            }
        }
        return best.get();
    }

    public void localPheromoneUpdate(ACSinfo info, AntBoi ant, double localPhero, double initPhero) {
        ArrayList<GLink> links = API.getLinks(ant.currentPath, info.graph);
        F.iterate(links, (i, link) -> {
            Pair<Long> pair = new Pair<>(link.nodeFrom, link.nodeTo);
            NumberValue<Double> pheromone = info.getPheromone(pair);
            double val = (1d - localPhero) * pheromone.get() + (localPhero * initPhero);
            pheromone.set(val);
        });
    }

    public void globalPheromoneUpdate(ACSinfo info, AntBoi ant, double decay) {
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
