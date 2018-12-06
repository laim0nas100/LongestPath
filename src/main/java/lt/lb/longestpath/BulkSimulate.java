/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lt.lb.commons.F;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.longestpath.anneal.AnnealingInfo;
import lt.lb.longestpath.antcolony.AntsSimulationParams;
import lt.lb.longestpath.genetic.GeneticSimulationParams;
import lt.lb.longestpath.tabu.TabuInfo;

/**
 *
 * @author laim0nas100
 */
public class BulkSimulate {

    public static class ANTS {

        public static Supplier<AntsSimulationParams> defaultAnts = () -> {
            AntsSimulationParams p = new AntsSimulationParams();
            p.ants = 10;
            p.decay = 0.1;
            p.localPheromoneInfluence = 0.1;
            p.greedyChance = 0.3;
            p.greedyDegree = 0.6;
            return p;
        };

        public static List<AntsSimulationParams> decay() {
            List<AntsSimulationParams> list = new ArrayList<>();

            double decay = 0.1;
            while (decay < 0.7) {
                AntsSimulationParams p = defaultAnts.get();
                list.add(p);
                p.decay = decay;
                decay += 0.1;
            }

            return list;
        }

        public static List<AntsSimulationParams> localPheromoneInfl() {
            List<AntsSimulationParams> list = new ArrayList<>();

            double infl = 0.1;
            while (infl < 0.7) {
                AntsSimulationParams p = defaultAnts.get();
                list.add(p);
                p.localPheromoneInfluence = infl;
                infl += 0.1;
            }

            return list;
        }

        public static List<AntsSimulationParams> greedyChance() {
            List<AntsSimulationParams> list = new ArrayList<>();

            double chance = 0.1;
            while (chance < 0.7) {
                AntsSimulationParams p = defaultAnts.get();
                list.add(p);
                p.greedyChance = chance;
                chance += 0.1;
            }

            return list;
        }

        public static List<AntsSimulationParams> greedyDegreeChance() {
            List<AntsSimulationParams> list = new ArrayList<>();

            double chance = 0.1;
            while (chance < 0.7) {
                AntsSimulationParams p = defaultAnts.get();
                list.add(p);
                p.greedyDegree = chance;
                chance += 0.1;
            }

            return list;
        }

        public static void simulate(int times, Orgraph gr, String output, List<AntsSimulationParams> list) {

            Simulation.genericSimulate(gr, times, output, list, (g, param, time, path) -> {
                F.unsafeRun(() -> {
                    Simulation.ANTS.logAntSimulations(g, param, times, path);
                });
            });
        }

        public static void allAntSimulations(Orgraph graph) {

            simulate(10, graph, "bulk/ANTS/localPheromoneInfl", BulkSimulate.ANTS.localPheromoneInfl());
            simulate(10, graph, "bulk/ANTS/decay", BulkSimulate.ANTS.decay());
            simulate(10, graph, "bulk/ANTS/greedy", BulkSimulate.ANTS.greedyChance());
            simulate(10, graph, "bulk/ANTS/greedyDegree", BulkSimulate.ANTS.greedyDegreeChance());
        }
    }

    public static class GA {

        public static Supplier<GeneticSimulationParams> defaultGA = () -> {
            GeneticSimulationParams p = new GeneticSimulationParams();
            p.population = 40;
            p.crossoverChance = 0.5;
            p.distinctSpecies = 5;
            p.initSimilarity = 0.5;
            p.maxSpecies = 10;
            return p;
        };

        public static List<GeneticSimulationParams> population() {
            List<GeneticSimulationParams> list = new ArrayList<>();
            int pop = 40;
            for (int i = 0; i < 5; i++) {
                GeneticSimulationParams p = defaultGA.get();
                p.population = pop + 10 * i;
                list.add(p);
            }
            return list;
        }

        public static List<GeneticSimulationParams> species() {
            List<GeneticSimulationParams> list = new ArrayList<>();
            int spec = 5;
            for (int i = 0; i < 5; i++) {
                GeneticSimulationParams p = defaultGA.get();
                p.distinctSpecies = spec + 2 * i;
                list.add(p);
            }
            return list;
        }

        public static List<GeneticSimulationParams> crossover() {
            List<GeneticSimulationParams> list = new ArrayList<>();
            double cross = 0.1;
            for (int i = 0; i < 5; i++) {
                GeneticSimulationParams p = defaultGA.get();
                p.crossoverChance = cross;
                list.add(p);
                cross += 0.1;
            }
            return list;
        }

        public static void simulate(int times, Orgraph gr, String output, List<GeneticSimulationParams> list) {

            Simulation.genericSimulate(gr, times, output, list, (g, param, time, path) -> {
                F.unsafeRun(() -> {
                    Simulation.GA.logGASimulations(g, param, times, path);
                });
            });
        }

        public static void allGASimulations(Orgraph graph) {

            simulate(10, graph, "bulk/GA/population", BulkSimulate.GA.population());
            simulate(10, graph, "bulk/GA/species", BulkSimulate.GA.species());
            simulate(10, graph, "bulk/GA/crossover", BulkSimulate.GA.crossover());
        }

    }

    public static class ANN {

        public static Supplier<AnnealingInfo> defaultAnneal = () -> {
            AnnealingInfo p = new AnnealingInfo();
            p.finalTemp = 0.01;
            p.startingTemp = 1;
            p.iterationsPerTemp = 50;
            p.maxStagnation = 100;
            p.tempUpdate = (a) -> a * 0.96;
            return p;
        };

        public static List<AnnealingInfo> tempUpdate() {
            List<AnnealingInfo> list = new ArrayList<>();
            F.unsafeRun(() -> {
                AnnealingInfo p = defaultAnneal.get();
                p.tempUpdate = d -> d * .95;
                list.add(p);
            });
            F.unsafeRun(() -> {
                AnnealingInfo p = defaultAnneal.get();
                p.tempUpdate = d -> d * .90;
                list.add(p);
            });
            F.unsafeRun(() -> {
                AnnealingInfo p = defaultAnneal.get();
                p.tempUpdate = d -> d * .85;
                list.add(p);
            });
            F.unsafeRun(() -> {
                AnnealingInfo p = defaultAnneal.get();
                p.tempUpdate = d -> d * .80;
                list.add(p);
            });
            F.unsafeRun(() -> {
                AnnealingInfo p = defaultAnneal.get();
                p.tempUpdate = d -> d * .75;
                list.add(p);
            });

            return list;
        }
        
        public static void simulate(int times, Orgraph gr, String output, List<AnnealingInfo> list) {

            Simulation.genericSimulate(gr, times, output, list, (g, param, time, path) -> {
                F.unsafeRun(() -> {
                    Simulation.ANN.logAnnealingSimulations(g, param, times, path);
                });
            });
        }
        
        public static void allAnnealingSimulations(Orgraph graph) {

            simulate(10, graph, "bulk/ANN/tempUpdate", BulkSimulate.ANN.tempUpdate());
        }
    }

    public static class TABU {

        public static Supplier<TabuInfo> defaultTabu = () -> {
            TabuInfo p = new TabuInfo();
            p.iterationLimit = 500;
            p.maxStagnation = 100;
            p.tabuDecay = 1d; // no decay
            return p;
        };

        public static List<TabuInfo> tabuDecay() {
            List<TabuInfo> list = new ArrayList<>();
            double decay = 1d;
            for (int i = 0; i < 5; i++) {
                TabuInfo p = defaultTabu.get();
                p.tabuDecay = decay;
                decay = decay - 0.1;
                list.add(p);
            }
            return list;
        }
        
        public static void simulateMoves(int times, Orgraph gr, String output, List<TabuInfo> list) {

            Simulation.genericSimulate(gr, times, output, list, (g, param, time, path) -> {
                F.unsafeRun(() -> {
                    Simulation.TABU.logTabuMovesSimulations(g, param, times, path);
                });
            });
        }
        public static void simulateSolutions(int times, Orgraph gr, String output, List<TabuInfo> list) {

            Simulation.genericSimulate(gr, times, output, list, (g, param, time, path) -> {
                F.unsafeRun(() -> {
                    Simulation.TABU.logTabuSolutionsSimulations(g, param, times, path);
                });
            });
        }
        
        public static void allTABUSimulations(Orgraph graph) {

            simulateMoves(10, graph, "bulk/TABU/moves_decay", BulkSimulate.TABU.tabuDecay());
            simulateSolutions(10, graph, "bulk/TABU/solutions_decay", BulkSimulate.TABU.tabuDecay());
        }
    }
}
