/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic;

import lt.lb.longestpath.genetic.GeneticSolution;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Algorithms;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.GNode;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.GraphGenerator;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.misc.RandomDistribution;
import lt.lb.commons.threads.FastWaitingExecutor;
import lt.lb.commons.threads.FastExecutor;
import lt.lb.longestpath.genetic.GeneticSolution.GraphAgent;
import lt.lb.longestpath.genetic.GraphAgentBreeder;
import lt.lb.longestpath.genetic.GraphAgentMaker;
import lt.lb.longestpath.genetic.GraphAgentMutator;
import lt.lb.longestpath.genetic.GraphAgentSimilarityEvaluator;
import lt.lb.longestpath.genetic.GraphAgentSorter;
import lt.lb.neurevol.evolution.Control.NEATConfig;
import lt.lb.neurevol.evolution.NEAT.Agent;
import lt.lb.neurevol.evolution.NEAT.Genome;
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

    public int nodeCount = 500;
    public int population = 200;
    public NeatPool<GraphAgent> pool;

    public GeneticSimulation(int times) {

        Orgraph graph = new Orgraph();
        Random r = new Random(1337);
        Supplier<Double> sup = () -> r.nextDouble();
        RandomDistribution uniform = RandomDistribution.uniform(sup);
        RandomDistribution dice2 = RandomDistribution.dice(sup, 2);
        GraphGenerator.generateSimpleConnected(dice2, graph, nodeCount, () -> 1d);
//        GraphGenerator.densify(uniform, graph, 5, ()->1d);
        Log.print("Graph generated");

        NEATConfig<GeneticSolution.GraphAgent> config = new NEATConfig<GeneticSolution.GraphAgent>() {
            @Override
            public Pool<GeneticSolution.GraphAgent> getPool() {
                return pool;
            }

            @Override
            public AgentMaker<GeneticSolution.GraphAgent> getMaker() {
                return new GraphAgentMaker(graph, uniform);
            }

            @Override
            public AgentBreeder<GeneticSolution.GraphAgent> getBreeder() {
                return new GraphAgentBreeder(graph, uniform);
            }

            @Override
            public AgentMutator<GeneticSolution.GraphAgent> getMutator() {
                return new GraphAgentMutator(graph, uniform);
            }

            @Override
            public AgentSorter<GeneticSolution.GraphAgent> getSorter() {
                return new GraphAgentSorter();
            }

            @Override
            public AgentSimilarityEvaluator<GeneticSolution.GraphAgent> getSimilarityEvaluator() {
                return new GraphAgentSimilarityEvaluator();
            }

            @Override
            public Species<GeneticSolution.GraphAgent> newSpecies() {
                Species<GeneticSolution.GraphAgent> s = new Species<>();
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
        pool.maxStaleness = 5;
        pool.similarity = 1;
        pool.distinctSpecies = 4;
//        pool.strictSimilarity = true;
        ArrayList<GraphAgent> bestByGeneration = new ArrayList<>();
        
        Log.print("initial GENERATION",pool.getGeneration());
            for(GraphAgent g:pool.getPopulation()){
                Log.print(g.debug());
            }
        for (int i = 0; i < times; i++) {
            pool.newGeneration();
            List<GraphAgent> population1 = pool.getPopulation();
            Log.print("GENERATION",pool.getGeneration());
            for(GraphAgent g:population1){
                Log.print(g.debug());
            }
            
            GraphAgent currentBest = (GraphAgent) pool.allTimeBest;
            bestByGeneration.add(currentBest);
            Log.print("CurrentBest:" + currentBest);
        }
        Log.print("Bests:");
        Log.printLines(bestByGeneration);
        Log.print("Species:"+ pool.getSubpopulations().size());
        Log.println("","All time best:", "Length:" + pool.allTimeBest.path.size(), "Path:" + pool.allTimeBest.path);
        Log.print("Is path valid though?",isPathValid(graph,pool.allTimeBest.path));
        
    }
    
    public static String isPathValid(Orgraph gr,List<Long> nodes){
        for(int i = 1; i < nodes.size(); i++){
            Long prev = nodes.get(i-1);
            Long n = nodes.get(i);
            Optional<GNode> node = gr.getNode(prev);
            Optional<GNode> node1 = gr.getNode(n);
            if(node.isPresent() && node1.isPresent()){
                if(node.get().linksTo.contains(node1.get().ID)){
                    // all good
                }else{
                    return "No such link:"+node.get().ID +" -> "+node1.get().ID;
                }
            }
        }
        return "Yes";
    }

}
