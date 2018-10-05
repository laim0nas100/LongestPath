/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.GraphGenerator;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.misc.RandomDistribution;
import lt.lb.longestpath.GeneticSolution;
import org.junit.Test;

/**
 *
 * @author Laimonas-Beniusis-PC
 */
public class Tests {

    public Tests() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void go() {

        Log.instant = true;
        int nodeCount = 20;
        Orgraph graph = new Orgraph();
        Random r = new Random(1337);
        Supplier<Double> sup = () -> r.nextDouble();
        RandomDistribution uniform = RandomDistribution.uniform(sup);
        RandomDistribution dice2 = RandomDistribution.dice(sup, 2);
        GraphGenerator.generateSimpleConnected(dice2, graph, nodeCount, () -> 1d);
        Log.print("Generated graph");
        for (int i = 0; i < 10; i++) {
            List<GLink> path = PathGenerator.generateLongPathBidirectional(graph, uniform.pickRandom(graph.nodes.keySet()), PathGenerator.nodeDegreeDistributed(uniform));
            Log.print(path);
            Log.print(GeneticSolution.getNodesIDs(path));
        }

    }
}
