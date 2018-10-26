/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath;

import java.io.IOException;
import lt.lb.longestpath.genetic.GeneticSimulation;
import java.util.concurrent.TimeUnit;
import lt.lb.commons.ArrayOp;
import lt.lb.commons.F;
import lt.lb.commons.Lambda;
import lt.lb.commons.Log;
import lt.lb.commons.containers.Value;
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

        ANTS.simulate200(10);
        Log.print("END");

        F.unsafeRun(() -> {
            Log.await(1, TimeUnit.HOURS);
            System.exit(0);
        });

    }

    public static Log getFileLog(String path) throws IOException {
        Log file = new Log();
        file.async = true;
        file.display = false;
        file.stackTrace = false;
        file.surroundString = false;
        file.threadName = false;
        file.timeStamp = false;

        Log.changeStream(file, Log.LogStream.FILE, path);
        return file;

    }

    public static void logSimulations(int times, String path, Runnable run, String[] format, Lambda.L0R<Object[]> result) throws IOException {
        Log file = getFileLog(path);
        Log.print(file, format);
        for (int i = 0; i < times; i++) {
            run.run();
            Log.print(file, result.apply());
        }
        Log.close(file);
    }

    public static class ANTS {

        public static void simulate(int times, String pathGraph, String output, AntsSimulationParams asi) throws IOException {
            Orgraph graph = new Orgraph();
            API.importGraph(graph, pathGraph);
            logAntSimulations(graph, asi, times, output);
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

        public static void logAntSimulations(Orgraph gr, AntsSimulationParams asi, int times, String path) throws IOException {
            int nodes = gr.nodes.size();
            int links = gr.bidirectionalLinkCount();
            String[] format = ArrayOp.asArray(
                    "Nodes",
                    "Bi-Links",
                    "Ants",
                    "AlowedStagnation",
                    "Iteration reached",
                    "Improvements",
                    "Path evaluations",
                    "Best cost"
            );
            Value<AntsSimulation> sim = new Value<>();
            Runnable run = () -> {
                sim.set(new AntsSimulation(gr, rng, asi));
            };

            Lambda.L0R<Object[]> printer = Lambda.of(() -> {
                AntsSimulation s = sim.get();
                return new Object[]{
                    nodes, links,
                    asi.ants,
                    asi.maxStagnation,
                    s.acs.iteration.get(),
                    s.acs.bests.size() + 1,
                    s.info.evluations.get(),
                    s.bestBoi.cost.get()
                };
            });
            
            logSimulations(times, path, run, format, printer);
            

        }

    }

}
