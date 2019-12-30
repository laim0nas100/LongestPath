/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lt.lb.longestpath.genetic.GeneticSimulation;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import lt.lb.commons.ArrayOp;
import lt.lb.commons.F;
import lt.lb.commons.func.Lambda;
import lt.lb.commons.Log;
import lt.lb.commons.containers.values.Value;
import lt.lb.commons.containers.tuples.Tuple;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.GraphGenerator;
import lt.lb.commons.misc.rng.FastRandom;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.commons.threads.FastWaitingExecutor;
import lt.lb.commons.threads.sync.WaitTime;
import lt.lb.longestpath.anneal.Annealing;
import lt.lb.longestpath.anneal.AnnealingInfo;
import lt.lb.longestpath.anneal.AnnealingResult;
import lt.lb.longestpath.antcolony.AntsSimulation;
import lt.lb.longestpath.antcolony.AntsSimulationParams;
import lt.lb.longestpath.genetic.GeneticSimulationParams;
import lt.lb.longestpath.genetic.GraphAgent;
import lt.lb.longestpath.tabu.TabuInfo;
import lt.lb.longestpath.tabu.TabuResult;
import lt.lb.longestpath.tabu.TabuSearch;
import lt.lb.neurevol.evolution.NEAT.NeatPool;

/**
 *
 * @author Laimonas-Beniusis-PC
 */
public class Simulation {

    public static ThreadLocal<RandomDistribution> rng = ThreadLocal.withInitial(() -> RandomDistribution.uniform(new FastRandom(123)));

    public static void main(String[] str) throws IOException {

        Log.main().async = true;
        Log.main().keepBufferForFile = false;
        Log.main().stackTrace = false;
        
        

//        //simulate ANTS
//        F.checkedRun(()->{
//            AntsSimulationParams asi = new AntsSimulationParams();
//            asi.maxStagnation = 50;
//            asi.iterations = 1000;
//            asi.ants = 10;
//            ANTS.simulate(30, "200.txt", "results/ANTS200.txt", asi);
//        });
//        System.gc();
//        F.checkedRun(()->{
//            AntsSimulationParams asi = new AntsSimulationParams();
//            asi.maxStagnation = 50;
//            asi.iterations = 1000;
//            asi.ants = 50;
//            ANTS.simulate(30, "1000.txt", "results/ANTS1000.txt", asi);
//        });
//        System.gc();
//        F.checkedRun(()->{
//            AntsSimulationParams asi = new AntsSimulationParams();
//            asi.maxStagnation = 50;
//            asi.iterations = 1000;
//            asi.ants = 100;
//            ANTS.simulate(30, "3000.txt", "results/ANTS3000.txt", asi);
//        });
//        System.gc();
//        
        //Simulate GA
//        F.checkedRun(()->{
//            GeneticSimulationParams param = new GeneticSimulationParams();
//            param.maxStagnation = 50;
//            param.iterations = 100;
//            param.population = 100;
//            param.distinctSpecies = param.population / 20;
//            param.crossoverChance = 0.3;
//            param.maxSpecies = param.population /10;
//            GA.simulate(30, "200.txt", "results/GA200.txt", param);
//        });
//        System.gc();
// 
//        F.checkedRun(()->{
//            GeneticSimulationParams param = new GeneticSimulationParams();
//            param.maxStagnation = 50;
//            param.iterations = 100;
//            param.population = 200;
//            param.distinctSpecies = param.population / 20;
//            param.crossoverChance = 0.3;
//            param.maxSpecies = param.population /10;
//            GA.simulate(30, "1000.txt", "results/GA1000.txt", param);
//        });
//        System.gc();
//        
//        F.checkedRun(()->{
//            GeneticSimulationParams param = new GeneticSimulationParams();
//            param.maxStagnation = 50;
//            param.iterations = 100;
//            param.population =200;
//            param.distinctSpecies = param.population / 20;
//            param.crossoverChance = 0.3;
//            param.maxSpecies = param.population /10;
//            GA.simulate(30, "3000.txt", "results/GA3000.txt", param);
//        });
//        System.gc();
//        
//        //Simulate Annealing
//        
//        F.checkedRun(()->{
//            AnnealingInfo param = new AnnealingInfo();
//            param.finalTemp = 0.001;
//            param.startingTemp = 1;
//            param.iterationsPerTemp = 50;
//            param.maxStagnation = 50;
//            param.tempUpdate = (a) -> a * 0.95;
//            ANN.simulate(30, "200.txt", "results/200ANN.txt", param);
//        });
//        System.gc();

//            F.checkedRun(() -> {
//                AnnealingInfo param = new AnnealingInfo();
//                param.finalTemp = 0.001;
//                param.startingTemp = 1;
//                param.iterationsPerTemp = 50;
//                param.maxStagnation = 50;
//                param.tempUpdate = (a) -> a * 0.95;
//                ANN.simulate(30, "1000.txt", "results/1000ANN.txt", param);
//            });
//            System.gc();

//            F.checkedRun(() -> {
//                AnnealingInfo param = new AnnealingInfo();
//                param.finalTemp = 0.001;
//                param.startingTemp = 1;
//                param.iterationsPerTemp = 50;
//                param.maxStagnation = 50;
//                param.tempUpdate = (a) -> a * 0.95;
//                ANN.simulate(30, "3000.txt", "results/3000ANN.txt", param);
//            });
//            System.gc();
//
//        //TABU
//            F.checkedRun(() -> {
//                TabuInfo info = new TabuInfo();
//                info.iterationLimit = 500;
//                info.maxStagnation = 100;
//                info.tabuDecay = 0.90d;
//                TABU.simulateMoves(30, "200.txt", "results/200TABUmoves.txt", info);
//            });
//
//            System.gc();
//
//            F.checkedRun(() -> {
//                TabuInfo info = new TabuInfo();
//                info.iterationLimit = 500;
//                info.maxStagnation = 100;
//                info.tabuDecay = 0.90d;
//                TABU.simulateMoves(30, "1000.txt", "results/1000TABUmoves.txt", info);
//            });
//            System.gc();
//
//            F.checkedRun(() -> {
//                TabuInfo info = new TabuInfo();
//                info.iterationLimit = 500;
//                info.maxStagnation = 100;
//                info.tabuDecay = 0.90d;
//                TABU.simulateMoves(30, "3000.txt", "results/3000TABUmoves.txt", info);
//            });
//            System.gc();
//
//            F.checkedRun(() -> {
//                TabuInfo info = new TabuInfo();
//                info.iterationLimit = 500;
//                info.maxStagnation = 100;
//                info.tabuDecay = 0.90d;
//                TABU.simulateSolutions(30, "200.txt", "results/200TABUsolutions.txt", info);
//            });
//
//            System.gc();
//
            F.checkedRun(() -> {
                TabuInfo info = new TabuInfo();
                info.iterationLimit = 500;
                info.maxStagnation = 100;
                info.tabuDecay = 0.90d;
                TABU.simulateSolutions(30, "1000.txt", "results/1000TABUsolutions.txt", info);
            });
            System.gc();
//
//            F.checkedRun(() -> {
//                TabuInfo info = new TabuInfo();
//                info.iterationLimit = 500;
//                info.maxStagnation = 100;
//                info.tabuDecay = 0.90d;
//                TABU.simulateSolutions(30, "3000.txt", "results/3000TABUsolutions.txt", info);
//            });
//            System.gc();
        
        

//        Orgraph graph = new Orgraph();
//        RandomDistribution r = rng.get();
//        Supplier<Double> sup = () -> {
//            return r.nextInt(1, 10).doubleValue();
//        };
//        GraphGenerator.generateSimpleConnected(r, graph, 3000, sup);
//
//        GraphGenerator.addSomeBidirectionalLinksToAllNodes(rng.get(), graph, 50, sup);
//
//        API.exportGraph(graph, "3000.txt");
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

    public static <T> void genericSimulate(Orgraph gr, int times, String output, List<T> list, Lambda.L4<Orgraph, T, Integer, String> cons) {
        F.iterate(list, (i, t) -> {
            rng = ThreadLocal.withInitial(() -> RandomDistribution.uniform(new FastRandom(123)));
            F.unsafeRun(() -> {
                cons.apply(gr, t, i, output + String.format("%02d", i + 1));
            });
        });
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

    public static class GA {

        public static void simulate200(int times) throws IOException {
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

        public static void logGASimulations(Orgraph gr, GeneticSimulationParams par, int times, String path) throws IOException {
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

    public static class ANN {

        public static void simulate200(int times) throws IOException {
            AnnealingInfo info = new AnnealingInfo();
            info.finalTemp = 0.0001;
            info.startingTemp = 1;
            info.iterationsPerTemp = 50;
            info.maxStagnation = 50;
            info.tempUpdate = (a) -> a * 0.99;
            simulate(times, "200.txt", "200ANN.txt", info);
        }

        public static void simulate(int times, String pathGraph, String output, AnnealingInfo par) throws IOException {
            Orgraph graph = new Orgraph();
            API.importGraph(graph, pathGraph);
            logAnnealingSimulations(graph, par, times, output);
        }

        public static void logAnnealingSimulations(Orgraph gr, AnnealingInfo par, int times, String path) throws IOException {
            int nodes = gr.nodes.size();
            int links = gr.bidirectionalLinkCount();
            String[] format = ArrayOp.asArray(
                    "Nodes",
                    "Bi-Links",
                    "Alowed Stagnation",
                    "Total path generations",
                    "Failed path generations",
                    "Successfull path generations",
                    "Global improvements",
                    "Straight improvements",
                    "Deteriorations",
                    "Reached stagnation",
                    "Reached temp",
                    "Path evaluations",
                    "Best cost"
            );
            Value<AnnealingResult> val = new Value<>();
            Lambda.L0R<Object[]> printer = Lambda.of(() -> {
                AnnealingResult get = val.get();
                return new Object[]{
                    nodes,
                    links,
                    par.maxStagnation,
                    get.successGenerations + get.failedGenerations,
                    get.failedGenerations,
                    get.successGenerations,
                    get.globalImprovements,
                    get.straightImprovements,
                    get.deteriorations,
                    get.maxStagnationReached,
                    get.reachedTemp,
                    get.weightEvaluations,
                    get.bestSoFarWeight

                };
            });
            Runnable run = () -> {
                AnnealingResult result = new AnnealingResult();
                Annealing.anneal(gr, rng.get(), par, result);
                val.set(result);
                Log.print("Is path valid? ", API.isPathValid(gr, result.bestSoFar));
            };

            logSimulations(times, path, run, format, printer);
        }
    }

    public static class TABU {

        public static void simulate200(int times) throws IOException {
            TabuInfo info = new TabuInfo();
            info.iterationLimit = 500;
            info.maxStagnation = 100;
            info.tabuDecay = 1d;
            simulateSolutions(times, "200.txt", "200TABU.txt", info);
        }

        public static void simulateSolutions(int times, String pathGraph, String output, TabuInfo par) throws IOException {
            Orgraph graph = new Orgraph();
            API.importGraph(graph, pathGraph);
            logTabuSolutionsSimulations(graph, par, times, output);
        }

        public static void simulateMoves(int times, String pathGraph, String output, TabuInfo par) throws IOException {
            Orgraph graph = new Orgraph();
            API.importGraph(graph, pathGraph);
            logTabuMovesSimulations(graph, par, times, output);
        }

        public static void logTabuSolutionsSimulations(Orgraph gr, TabuInfo par, int times, String path) throws IOException {
            int nodes = gr.nodes.size();
            int links = gr.bidirectionalLinkCount();
            String[] format = ArrayOp.asArray(
                    "Nodes",
                    "Bi-Links",
                    "Alowed Stagnation",
                    "Total path generations",
                    "Failed path generations",
                    "Successfull path generations",
                    "Straight improvements",
                    "Reached stagnation",
                    "Path evaluations",
                    "Best cost"
            );
            Value<TabuResult> val = new Value<>();
            Lambda.L0R<Object[]> printer = Lambda.of(() -> {
                TabuResult get = val.get();
                return new Object[]{
                    nodes,
                    links,
                    par.maxStagnation,
                    get.successGenerations + get.failedGenerations,
                    get.failedGenerations,
                    get.successGenerations,
                    get.straightImprovements,
                    get.maxStagnationReached,
                    get.weightEvaluations,
                    get.bestSoFarWeight

                };
            });
            Runnable run = () -> {
                TabuResult result = new TabuResult();
                TabuSearch.tabuSolutions(gr, rng.get(), par, result);
                val.set(result);
                Log.print("Is path valid? ", API.isPathValid(gr, result.bestSoFar));
            };

            logSimulations(times, path, run, format, printer);
        }

        public static void logTabuMovesSimulations(Orgraph gr, TabuInfo par, int times, String path) throws IOException {
            int nodes = gr.nodes.size();
            int links = gr.bidirectionalLinkCount();
            String[] format = ArrayOp.asArray(
                    "Nodes",
                    "Bi-Links",
                    "Alowed Stagnation",
                    "Total path generations",
                    "Failed path generations",
                    "Successfull path generations",
                    "Straight improvements",
                    "Reached stagnation",
                    "Path evaluations",
                    "Best cost"
            );
            Value<TabuResult> val = new Value<>();
            Lambda.L0R<Object[]> printer = Lambda.of(() -> {
                TabuResult get = val.get();
                return new Object[]{
                    nodes,
                    links,
                    par.maxStagnation,
                    get.successGenerations + get.failedGenerations,
                    get.failedGenerations,
                    get.successGenerations,
                    get.straightImprovements,
                    get.maxStagnationReached,
                    get.weightEvaluations,
                    get.bestSoFarWeight

                };
            });
            Runnable run = () -> {
                TabuResult result = new TabuResult();
                TabuSearch.tabuMoves(gr, rng.get(), par, result);
                val.set(result);
                Log.print("Is path valid? ", API.isPathValid(gr, result.bestSoFar));
            };

            logSimulations(times, path, run, format, printer);
        }
    }

}
