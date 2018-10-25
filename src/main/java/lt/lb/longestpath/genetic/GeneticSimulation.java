/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.commons.threads.FastWaitingExecutor;
import lt.lb.longestpath.API;
import lt.lb.longestpath.genetic.impl.GraphAgentBreeder;
import lt.lb.longestpath.genetic.impl.GraphAgentMaker;
import lt.lb.longestpath.genetic.impl.GraphAgentMutator;
import lt.lb.longestpath.genetic.impl.GraphAgentSimilarityEvaluator;
import lt.lb.longestpath.genetic.impl.GraphAgentSorter;
import lt.lb.neurevol.evolution.Control.NEATConfig;
import lt.lb.neurevol.evolution.NEAT.NeatPool;
import lt.lb.neurevol.evolution.NEAT.Species;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentBreeder;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentMaker;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentMutator;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentSimilarityEvaluator;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentSorter;
import lt.lb.neurevol.evolution.NEAT.interfaces.Pool;

/**
 *
 * @author laim0nas100
 */
public class GeneticSimulation {

    public NeatPool<GraphAgent> pool;

    public GeneticSimulation(Orgraph graph, RandomDistribution uniform,GeneticSimulationInfo info) {
        NEATConfig<GraphAgent> config = new NEATConfig<GraphAgent>() {
            @Override
            public Pool<GraphAgent> getPool() {
                return pool;
            }

            @Override
            public AgentMaker<GraphAgent> getMaker() {
                return new GraphAgentMaker(graph, uniform, info.population);
            }

            @Override
            public AgentBreeder<GraphAgent> getBreeder() {
                return new GraphAgentBreeder(graph, uniform, info.crossoverChance);
            }

            @Override
            public AgentMutator<GraphAgent> getMutator() {
                return new GraphAgentMutator(graph, uniform);
            }

            @Override
            public AgentSorter<GraphAgent> getSorter() {
                return new GraphAgentSorter();
            }

            @Override
            public AgentSimilarityEvaluator<GraphAgent> getSimilarityEvaluator() {
                return new GraphAgentSimilarityEvaluator();
            }

            @Override
            public Species<GraphAgent> newSpecies() {
                Species<GraphAgent> s = new Species<>();
                s.conf = this;
                return s;
            }

            Executor fastExe = new FastWaitingExecutor(5, 1, TimeUnit.SECONDS);

            @Override
            public Executor getExecutor() {
                return fastExe;
            }
        };

        pool = new NeatPool(config);
        pool.debug = objs -> {
            Log.print(objs);
            return pool.debug;
        };
        pool.similarity = info.initSimilarity;
        pool.distinctSpecies = info.distinctSpecies;
        pool.maxSpecies = info.maxSpecies;

        Log.print("Initial population");
        F.iterate(pool.getPopulation(), (index, g) -> {
            Log.print(g, API.isPathValid(graph, g.path));
        });
        graph.sanityCheck();
        ArrayList<GraphAgent> bestByGeneration = new ArrayList<>();
        for (int i = 0; i < info.iterations; i++) {
            F.iterate(pool.getPopulation(), (index, g) -> {
//                g.computeFitness();
                Log.print(g, API.isPathValid(graph, g.path));
            });
            pool.newGeneration();

            GraphAgent currentBest = (GraphAgent) pool.allTimeBest;
            bestByGeneration.add(currentBest);
            Log.print("CurrentBest:" + currentBest);
        }
        Log.print("Bests:");
        Log.printLines(bestByGeneration);
        Log.print("Species:" + pool.getSubpopulations().size());
        Log.println("", "All time best:", "Length:" + pool.allTimeBest.fitness, "Path:" + pool.allTimeBest.path);
        Log.print("Is path valid though?", API.isPathValid(graph, pool.allTimeBest.path));

        Log.print("Links:", graph.links.size());
        Log.print("Links bidirectional:", graph.bidirectionalLinkCount());
        Log.print("Nodes:", graph.nodes.size());

        Log.print("Times fitness was computed:" + GraphAgent.timesFitnessComputed.get());
        Log.print("Times mutation was discarded:" + GraphAgent.emptyMutation.get());
        Log.print("Times crossover was invalid:" + GraphAgent.invalidCrossover.get());
        Log.print("Times crossover was ok:" + GraphAgent.successfullCrossover.get());
        graph.sanityCheck();
        Log.printLines(graph.doesNotHaveAPair());

        ArrayList<GLink> links = new ArrayList<>(graph.links.values());
        
        Collections.sort(links,API.linkComparatorPretty);
        Log.print("LINKS::::::::::::");
//        Log.printLines(links);

    }

}
