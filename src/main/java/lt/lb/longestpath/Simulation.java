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
import lt.lb.longestpath.antcolony.AntsSimulationParams;

/**
 *
 * @author Laimonas-Beniusis-PC
 */
public class Simulation {

    public static ThreadLocal<RandomDistribution> rng = ThreadLocal.withInitial(() -> RandomDistribution.uniform(new FastRandom()));

    public static void main(String[] str) throws IOException {

        Log.main().async = true;
        Log.main().keepBufferForFile = false;
        Log.main().stackTrace = false;

        simulate200(2);
        Log.print("END");

        F.unsafeRun(() -> {
            Log.await(1, TimeUnit.HOURS);
            System.exit(0);
        });

    }

    public static void simulate(int times, String pathGraph, String output, AntsSimulationParams asi) throws IOException {
        Orgraph graph = new Orgraph();
        API.importGraph(graph, pathGraph);
        logAntSimulations(graph, asi, rng, times, output);
    }

    public static void simulate200(int times) throws IOException {

        AntsSimulationParams asi = new AntsSimulationParams();
        asi.maxStagnation = 50;
        asi.iterations = 1000;
        simulate(times, "200.txt", "200ants.txt", asi);
    }
    
    public static void simulate1000(int times) throws IOException {
        AntsSimulationParams asi = new AntsSimulationParams();
        asi.maxStagnation = 50;
        asi.iterations = 1000;
        asi.ants = 50;
        simulate(times, "1000.txt", "1000ants.txt", asi);

    }

    public static void logAntSimulations(Orgraph gr, AntsSimulationParams asi, ThreadLocal<RandomDistribution> rng, int times, String path) throws IOException {
        Log file = new Log();
        file.async = true;
        file.display = false;
        file.stackTrace = false;
        file.surroundString = false;
        file.threadName = false;
        file.timeStamp = false;

        Log.changeStream(file, Log.LogStream.FILE, path);

        int nodes = gr.nodes.size();
        int links = gr.bidirectionalLinkCount();
        Log.print(file, "Nodes", "Bi-Links", "Ants", "AlowedStagnation", "Iteration reached", "Improvements", "Path evaluations", "Best cost");
        for (int i = 0; i < times; i++) {
            AntsSimulation sim = new AntsSimulation(gr, rng, asi);
            Log.print(file, nodes, links, asi.ants, asi.maxStagnation, sim.acs.iteration.get(), sim.acs.bests.size() + 1, sim.info.evluations.get(), sim.bestBoi.cost.get());
        }
        Log.close(file);

    }

    

}
