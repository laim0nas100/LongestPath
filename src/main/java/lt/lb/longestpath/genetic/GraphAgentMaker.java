/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.misc.RandomDistribution;
import lt.lb.commons.threads.FastWaitingExecutor;
import lt.lb.commons.threads.Promise;
import lt.lb.longestpath.genetic.GeneticSolution.GraphAgent;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentMaker;

/**
 *
 * @author laim0nas100
 */
public class GraphAgentMaker implements AgentMaker<GraphAgent> {

    public int population = 50;

    public Orgraph gr;
    public RandomDistribution rnd;

    public GraphAgentMaker(Orgraph g, RandomDistribution rnd) {
        this.rnd = rnd;
        gr = g;
    }

    @Override
    public Collection<GraphAgent> initializeGeneration() {

        GraphAgent[] list = new GraphAgent[population];
        for (int i = 0; i < population; i++) {
            final int j = i;
            List<GLink> path = PathGenerator.generateLongPathBidirectional(gr, rnd.pickRandom(gr.nodes.keySet()), PathGenerator.nodeDegreeDistributed(rnd));
            GraphAgent graphAgent = new GeneticSolution.GraphAgent(GeneticSolution.getNodesIDs(path));
            Log.print("Generated valid?", GeneticSimulation.isPathValid(gr, graphAgent.path), graphAgent.debug());
            list[j] = graphAgent;

        }
        return Arrays.asList(list);
    }

}
