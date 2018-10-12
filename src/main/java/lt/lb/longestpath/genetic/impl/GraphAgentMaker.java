/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.misc.RandomDistribution;
import lt.lb.longestpath.genetic.GeneticSolution;
import lt.lb.longestpath.genetic.GraphAgent;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentMaker;

/**
 *
 * @author laim0nas100
 */
public class GraphAgentMaker implements AgentMaker<GraphAgent> {

    public int population = 100;
    public Orgraph gr;
    public RandomDistribution rnd;

    public GraphAgentMaker(Orgraph g, RandomDistribution rnd, int population) {
        this.rnd = rnd;
        gr = g;
        this.population = population;
    }

    @Override
    public Collection<GraphAgent> initializeGeneration() {
        ArrayList<GraphAgent> list = new ArrayList<>();
        for (int i = 0; i < population; i++) {
            List<GLink> path = PathGenerator.generateLongPathBidirectional(gr, rnd.pickRandom(gr.nodes.keySet()), PathGenerator.nodeDegreeDistributed(rnd));
            GraphAgent agent = new GraphAgent(GeneticSolution.getNodesIDs(path), gr);
            list.add(agent);
            Log.print("is valid?", GeneticSolution.isPathValid(gr, agent.path), agent);
            agent.computeFitness();
        }
        return list;
    }

}
