/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath;

import lt.lb.longestpath.genetic.GeneticSimulation;
import java.util.concurrent.TimeUnit;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.FastRandom;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.longestpath.antcolony.AntsSimulation;
import lt.lb.longestpath.antcolony.AntsSimulationInfo;

/**
 *
 * @author Laimonas-Beniusis-PC
 */
public class Simulation {

    public static void main(String[] str) {

        Log.main().async = true;
        Log.main().keepBufferForFile = false;
        
        Orgraph graph = new Orgraph();
        F.unsafeRun(()->{
            API.importGraph(graph, "MyGraph.txt");
        });
        Log.printLines(graph.links.values());
//nodeCount, population, generetions
//        new GeneticSimulation(200,50,30);

        AntsSimulationInfo asi = new AntsSimulationInfo();
        FastRandom rng = new FastRandom(1337);
        RandomDistribution uniform = RandomDistribution.uniform(rng::nextDouble);
        
        new AntsSimulation(graph,uniform,asi);
        Log.print("END");
        F.unsafeRun(() -> {
            Log.await(1, TimeUnit.HOURS);
            System.exit(0);
        });

    }
    
    
}
