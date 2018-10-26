/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath;

import java.io.IOException;
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

    public static void main(String[] str) throws IOException {

        Log.main().async = false;
        Log.main().keepBufferForFile = false;
        Log.main().stackTrace = false;
        
        simulate200(50);
        Log.print("END");

        F.unsafeRun(() -> {
            Log.await(1, TimeUnit.HOURS);
            System.exit(0);
        });

    }

    public static void simulate200(int times) throws IOException {
        Log file = new Log();
        file.async = true;
        file.display = false;
        file.stackTrace = false;
        file.surroundString = false;
        file.threadName = false;
        file.timeStamp = false;

        Log.changeStream(file, Log.LogStream.FILE, "200ants.txt");

        ThreadLocal<RandomDistribution> rng = ThreadLocal.withInitial(() -> RandomDistribution.uniform(new FastRandom()));
        Orgraph graph = new Orgraph();
        F.unsafeRun(() -> {
            API.importGraph(graph, "200.txt");
        });
        AntsSimulationInfo asi = new AntsSimulationInfo();
        asi.maxStagnation = 50;
        asi.iterations = 1000;

        int nodes = graph.nodes.size();
        int links = graph.bidirectionalLinkCount();
        Log.print(file, "Nodes", "Bi-Links", "Ants", "Iteration reached", "AlowedStagnation", "Improvements", "Best cost");
        for (int i = 0; i < times; i++) {
            AntsSimulation sim = new AntsSimulation(graph, rng, asi);
            Log.print(file, nodes, links, asi.ants, sim.acs.iteration.get(), asi.maxStagnation, sim.acs.bests.size() + 1, sim.bestBoi.cost.get());
        }
        Log.close(file);

    }
    
    public static void simulate1000(int times) throws IOException {
        Log file = new Log();
        file.async = true;
        file.display = false;
        file.stackTrace = false;
        file.surroundString = false;
        file.threadName = false;
        file.timeStamp = false;

        Log.changeStream(file, Log.LogStream.FILE, "1000ants.txt");

        ThreadLocal<RandomDistribution> rng = ThreadLocal.withInitial(() -> RandomDistribution.uniform(new FastRandom()));
        Orgraph graph = new Orgraph();
        F.unsafeRun(() -> {
            API.importGraph(graph, "1000.txt");
        });
        AntsSimulationInfo asi = new AntsSimulationInfo();
        asi.maxStagnation = 50;
        asi.iterations = 1000;

        int nodes = graph.nodes.size();
        int links = graph.bidirectionalLinkCount();
        Log.print(file, "Nodes", "Bi-Links", "Ants", "Iteration reached", "AlowedStagnation", "Improvements", "Best cost");
        for (int i = 0; i < times; i++) {
            AntsSimulation sim = new AntsSimulation(graph, rng, asi);
            Log.print(file, nodes, links, asi.ants, sim.acs.iteration.get(), asi.maxStagnation, sim.acs.bests.size() + 1, sim.bestBoi.cost.get());
        }
        Log.close(file);

    }

}
