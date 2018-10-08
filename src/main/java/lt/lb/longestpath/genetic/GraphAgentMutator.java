/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic;

import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.RandomDistribution;
import lt.lb.longestpath.genetic.GeneticSolution.GraphAgent;
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
        String debug = agent.debug();
        GraphAgent mutate = GeneticSolution.mutate(rnd, gr, agent);
        String pathValid = GeneticSimulation.isPathValid(gr, mutate.path);
        
        if(mutate.path.isEmpty()){
            // dont apply empty mutation
            return;
        }
        
        if(!pathValid.equalsIgnoreCase("yes")){
            return;
        }
        
        agent.id = mutate.id;
        agent.influenceGlobally = mutate.influenceGlobally;
        agent.links = mutate.links;
        agent.path = mutate.path;
        agent.fitness = mutate.fitness;
        Log.println("Mutation:",debug,agent.debug());
        
    }

}
