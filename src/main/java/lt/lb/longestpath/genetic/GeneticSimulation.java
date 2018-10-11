/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.GraphGenerator;
import lt.lb.commons.misc.RandomDistribution;
import lt.lb.commons.threads.FastWaitingExecutor;
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

    public double crossover_chance = 0.5;
    public NeatPool<GraphAgent> pool;
    public Orgraph graph;

    public GeneticSimulation(int nodeCount, int population, int times) {
        graph = new Orgraph();
        Random r = new Random(1337);
        Supplier<Double> sup = () -> r.nextDouble();
        RandomDistribution uniform = RandomDistribution.uniform(sup);
        RandomDistribution dice2 = RandomDistribution.dice(sup, 2);
        GraphGenerator.generateSimpleConnected(dice2, graph, nodeCount, dice2.getDoubleSupplier());
        GraphGenerator.addSomeBidirectionalLinksToAllNodes(dice2, graph, 5, dice2.getDoubleSupplier());

        NEATConfig<GraphAgent> config = new NEATConfig<GraphAgent>() {
            @Override
            public Pool<GraphAgent> getPool() {
                return pool;
            }

            @Override
            public AgentMaker<GraphAgent> getMaker() {
                return new GraphAgentMaker(graph, uniform, population);
            }

            @Override
            public AgentBreeder<GraphAgent> getBreeder() {
                return new GraphAgentBreeder(graph, uniform, crossover_chance);
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
        pool.similarity = 0.5d;
        pool.distinctSpecies = 5;
        pool.maxSpecies = 5;

        Log.print("Initial population");
        F.iterate(pool.getPopulation(), (index, g) -> {
            Log.print(g, GeneticSolution.isPathValid(graph, g.path));
        });
        graph.sanityCheck();
        ArrayList<GraphAgent> bestByGeneration = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            pool.newGeneration();
            F.iterate(pool.getPopulation(), (index, g) -> {
                Log.print(g, GeneticSolution.isPathValid(graph, g.path));
            });
            GraphAgent currentBest = (GraphAgent) pool.allTimeBest;
            bestByGeneration.add(currentBest);
            Log.print("CurrentBest:" + currentBest);
        }
        Log.print("Bests:");
        Log.printLines(bestByGeneration);
        Log.print("Species:" + pool.getSubpopulations().size());
        Log.println("", "All time best:", "Length:" + pool.allTimeBest.fitness, "Path:" + pool.allTimeBest.path);
        Log.print("Is path valid though?", GeneticSolution.isPathValid(graph, pool.allTimeBest.path));
        
        Log.print("Links:",graph.links.size());
        Log.print("Links bidirectional:",graph.bidirectionalLinkCount());
        Log.print("Nodes:",graph.nodes.size());
        
        Log.print("Times created GraphAgent:"+GraphAgent.timesCreated.get());
        Log.print("Times mutation was discarded:"+GraphAgent.emptyMutation.get());
        Log.print("Times crossover was invalid:"+GraphAgent.invalidCrossover.get());
        Log.print("Time crossover was ok:"+GraphAgent.successfullCrossover.get());
        graph.sanityCheck();
        Log.printLines(graph.doesNotHaveAPair());
        
        ArrayList<GLink> links = new ArrayList<>(graph.links.values());
        Collections.sort(links, (a, b) -> {
            int c = (int) (a.nodeFrom - b.nodeFrom);
            if (c == 0) {
                c = (int) (a.nodeTo - b.nodeTo);
            }

            return c;
        });
        Log.print("LINKS::::::::::::");
//        Log.printLines(links);
        
    }

}
