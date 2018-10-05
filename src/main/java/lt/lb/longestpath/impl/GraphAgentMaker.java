/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.misc.RandomDistribution;
import lt.lb.longestpath.GeneticSolution;
import lt.lb.longestpath.GeneticSolution.GraphAgent;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentMaker;

/**
 *
 * @author laim0nas100
 */
public class GraphAgentMaker implements AgentMaker<GraphAgent> {

    public int population = 100;
    public Orgraph gr;
    public RandomDistribution rnd;
    
    public GraphAgentMaker(Orgraph g,RandomDistribution rnd){
        this.rnd = rnd;
        gr = g;
    }

    @Override
    public Collection<GraphAgent> initializeGeneration() {
        ArrayList<GeneticSolution.GraphAgent> list = new ArrayList<>();
        for (int i = 0; i < population; i++) {
            List<GLink> path = PathGenerator.generateLongPathBidirectional(gr, rnd.pickRandom(gr.nodes.keySet()), PathGenerator.nodeDegreeDistributed(rnd));
            list.add(new GeneticSolution.GraphAgent(GeneticSolution.getNodesIDs(path)));
        }
        return list;
    }

}
