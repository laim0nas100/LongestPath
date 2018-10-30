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
import lt.lb.longestpath.genetic.GeneticSimulationParams;
import lt.lb.longestpath.genetic.GraphAgent;
import lt.lb.neurevol.evolution.NEAT.NeatPool;

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

        GA.simulate200(1);
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

        public static void logAntSimulations(Orgraph gr, AntsSimulationParams par, int times, String path) throws IOException {
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
                sim.set(new AntsSimulation(gr, rng, par));
            };

            Lambda.L0R<Object[]> printer = Lambda.of(() -> {
                AntsSimulation s = sim.get();
                return new Object[]{
                    nodes,
                    links,
                    par.ants,
                    par.maxStagnation,
                    s.acs.iteration.get(),
                    s.acs.bests.size() + 1,
                    s.info.evluations.get(),
                    s.bestBoi.cost.get()
                };
            });
            
            logSimulations(times, path, run, format, printer);
            

        }

    }

    public static class GA{
        
        public static void simulate200(int times) throws IOException{
            GeneticSimulationParams param = new GeneticSimulationParams();
            param.maxStagnation = 50;
            param.iterations = 1000;
            simulate(times, "200.txt", "200GA.txt", param);
        }
        
        public static void simulate(int times, String pathGraph, String output, GeneticSimulationParams par) throws IOException {
            Orgraph graph = new Orgraph();
            API.importGraph(graph, pathGraph);
            logGASimulations(graph, par, times, output);
        }
        
        public static void logGASimulations(Orgraph gr, GeneticSimulationParams par, int times, String path) throws IOException{
            int nodes = gr.nodes.size();
            int links = gr.bidirectionalLinkCount();
            String[] format = ArrayOp.asArray(
                    "Nodes",
                    "Bi-Links",
                    "Population",
                    "AlowedStagnation",
                    "TargetSpecies",
                    "MaxSpecies",
                    "Species at end",
                    "Generation reached",
                    "Ok crossovers",
                    "Invalid crossovers",
                    "Improvements",
                    "Path evaluations",
                    "Best cost"
            );
            Value<GeneticSimulation> sim = new Value<>();
            Lambda.L0R<Object[]> printer = Lambda.of(() -> {
                NeatPool p = sim.get().pool;
                return new Object[]{
                    nodes,
                    links,
                    par.population,
                    par.maxStagnation,
                    par.distinctSpecies,
                    par.maxSpecies,
                    p.species.size(),
                    p.generation,
                    GraphAgent.successfullCrossover.get(),
                    GraphAgent.invalidCrossover.get(),
                    sim.get().improvements,
                    GraphAgent.timesFitnessComputed.get(),
                    p.allTimeBest.fitness
                };
            });
            Runnable run = () -> {
                GraphAgent.emptyMutation.set(0L);
                GraphAgent.invalidCrossover.set(0);
                GraphAgent.successfullCrossover.set(0);
                GraphAgent.timesFitnessComputed.set(0);
                sim.set(new GeneticSimulation(gr, rng, par));
                
            };
            
            logSimulations(times, path, run, format, printer);
        }
    }
    
}
