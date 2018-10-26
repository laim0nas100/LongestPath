/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import java.util.function.Supplier;
import lt.lb.commons.F;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.GraphGenerator;
import lt.lb.commons.misc.rng.FastRandom;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.longestpath.API;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author laim0nas100
 */
public class GenerateGraphs {

    FastRandom rng = new FastRandom(1337);

    @Ignore
    @Test
    public void generate200() {
        F.unsafeRun(() -> {
            API.exportGraph(this.generate(200, 4), "200.txt");
        });
    }

    @Ignore
    @Test
    public void generate1000() {
        F.unsafeRun(() -> {
            API.exportGraph(this.generate(1000, 10), "1000.txt");
        });
    }

    @Ignore
    @Test
    public void generate10000() {
        F.unsafeRun(() -> {
            API.exportGraph(this.generate(10000, 200), "10000.txt");
        });
    }

    public Orgraph generate(int nodeCount, int preferedNodeDegree) {
        Orgraph graph = new Orgraph();
        Supplier<Double> sup = () -> rng.nextDouble();
        RandomDistribution dice2 = RandomDistribution.dice(sup, 2);
        Supplier<Double> dice2Sup = () -> (double) dice2.nextInt(1, 10);
        GraphGenerator.generateSimpleConnected(dice2, graph, nodeCount, dice2Sup);
        GraphGenerator.addSomeBidirectionalLinksToAllNodes(dice2, graph, preferedNodeDegree, dice2Sup);
        return graph;
    }
}
