/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic.impl;

import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.RandomDistribution;
import lt.lb.longestpath.genetic.GeneticSolution;
import lt.lb.longestpath.genetic.GraphAgent;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentMutator;

/**
 *
 * @author laim0nas100
 */
public class GraphAgentMutator implements AgentMutator<GraphAgent> {

    public Orgraph gr;
    public RandomDistribution rnd;

    public GraphAgentMutator(Orgraph g, RandomDistribution r) {
        rnd = r;
        gr = g;
    }

    @Override
    public void mutate(GraphAgent agent) {
        GraphAgent mutate = GeneticSolution.mutate(rnd, gr, agent);
        if(mutate.nodes.isEmpty()){
            GraphAgent.emptyMutation.incrementAndGet();
            // font apply negative mutation
            return;
        }
        
        Log.print("Valid mutation?",GeneticSolution.isPathValid(gr, mutate.path),new GraphAgent(agent)," => ",mutate);
        agent.links = mutate.links;
        agent.nodes = mutate.nodes;
        agent.path = mutate.path;
        agent.fitness = mutate.fitness;
        
    }

}
