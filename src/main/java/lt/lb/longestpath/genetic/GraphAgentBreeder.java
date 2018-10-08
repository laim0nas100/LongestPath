/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.containers.Pair;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.RandomDistribution;
import lt.lb.longestpath.genetic.GeneticSolution.GraphAgent;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentBreeder;

/**
 *
 * @author laim0nas100
 */
public class GraphAgentBreeder implements AgentBreeder<GraphAgent> {

    public double crossover_chance = -0.6;
    public RandomDistribution uniform;
    public RandomDistribution low;
    public Orgraph gr;

    public GraphAgentBreeder(Orgraph g, RandomDistribution uniform) {
        this.uniform = uniform;
        gr = g;
        low = RandomDistribution.xPower(uniform.getDoubleSupplier(), 2);
    }

    @Override
    public List<GraphAgent> breedChild(List<GraphAgent> agents) {
        int size = agents.size();
        if (size > 1 && uniform.nextDouble() < crossover_chance) {
            ArrayList<GraphAgent> crossover = crossover(new ArrayList<>(agents));
            if (!crossover.isEmpty()) {
                return crossover;
            }
        }
        ArrayList<GraphAgent> newAgents = new ArrayList<>(agents.size());
        F.iterate(agents, (i,a)->{
            newAgents.add(new GraphAgent(a.path));
        });

        return newAgents;
    }

    private ArrayList<GraphAgent> crossover(List<GraphAgent> list) {
        LinkedList<GraphAgent> parents = uniform.pickRandomPreferLow(list, 2, list.size(), 1);
        GraphAgent p1 = parents.peekFirst();
        GraphAgent p2 = parents.peekLast();
        
        String d1 = p1.debug();
        String d2 = p2.debug();

        List<GLink> bridges = GeneticSolution.getBridges(gr, p1.links, p2.links);
        ArrayList<Pair<Long>> pairs = new ArrayList<>(bridges.size());
        F.convertCollection(GeneticSolution.link2Pair, bridges, pairs);
        Predicate<Pair<Long>> containsBoth = (p)->{
            List<Long> asList = Arrays.asList(p.g1,p.g2);
            return p1.path.containsAll(asList) && p2.path.containsAll(asList);
        };
        F.filterParallel(pairs, containsBoth.negate(), r->r.run());
        
        if (pairs.isEmpty()) {
            return new ArrayList<>(0);
        }
        

        GLink pickRandom = uniform.pickRandom(bridges);
        Pair<Long> bridge = GeneticSolution.link2Pair.apply(pickRandom);
        ArrayList<GraphAgent> crossoverCommonLink = GeneticSolution.crossoverCommonLink(gr, p1, p2, bridge);
        for(GraphAgent a:crossoverCommonLink){
            String valid = GeneticSimulation.isPathValid(gr, a.path);
            if(!valid.equalsIgnoreCase("Yes")){
                throw new IllegalStateException(valid+" at:"+a);
            }
        }
        Log.println("Corssover ok:",d1,d2,p1.debug(),p2.debug());
        
        return crossoverCommonLink;
    }

}
