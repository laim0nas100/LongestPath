/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lt.lb.commons.F;
import lt.lb.commons.Log;
import lt.lb.commons.containers.tuples.Pair;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.longestpath.API;
import lt.lb.longestpath.genetic.GeneticSolution;
import lt.lb.longestpath.genetic.GraphAgent;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentBreeder;

/**
 *
 * @author laim0nas100
 */
public class GraphAgentBreeder implements AgentBreeder<GraphAgent> {

    public double crossover_chance = 0.6;
    public RandomDistribution uniform;
    public RandomDistribution low;
    public Orgraph gr;

    public GraphAgentBreeder(Orgraph g, RandomDistribution uniform, double cross) {
        this.uniform = uniform;
        gr = g;
        this.crossover_chance = cross;
    }

    @Override
    public List<GraphAgent> breedChild(List<GraphAgent> agents) {
        int size = agents.size();
        GraphAgent child;
        if (size > 1 && uniform.nextDouble() > crossover_chance) {
            ArrayList<GraphAgent> crossover = crossover(agents);
            if (!crossover.isEmpty()) {
                return crossover;
            }
        }
        GraphAgent get = agents.get(uniform.nextInt(size));
        child = new GraphAgent(get);

        return Arrays.asList(child);
    }
    

    private ArrayList<GraphAgent> crossover(List<GraphAgent> list) {
        LinkedList<GraphAgent> parents = uniform.pickRandom(list, 2);
        GraphAgent p1 = parents.peekFirst();
        GraphAgent p2 = parents.peekLast();

        List<GLink> bridges = GeneticSolution.getBridges(gr, p1.links.get(), p2.links.get());
        Predicate<Pair<Long>> good = (p)->{
            List<Long> asList = Arrays.asList(p.g1,p.g2);
            return !(p1.nodes.containsAll(asList) && p2.nodes.containsAll(asList));
        };
        Log.print("Bridges");
        
        List<Pair<Long>> pairs = bridges.stream().map(API.link2Pair).filter(good).collect(Collectors.toList());
        
        
        
        if (pairs.isEmpty()) {
            Log.print("Empty corssover pairs :(");
            return new ArrayList<>(0);
        }
        

        Pair<Long> bridge = uniform.pickRandom(pairs);
        ArrayList<GraphAgent> crossoverCommonLink = GeneticSolution.crossoverCommonLink(gr, p1, p2, bridge);
        ArrayList invalid = F.filterInPlace(crossoverCommonLink, p->p.isValid());
        if(!invalid.isEmpty()){
            GraphAgent.invalidCrossover.addAndGet(invalid.size());
        }
        if(crossoverCommonLink.isEmpty()){
            Log.print("Empty crossover :(");
        }
        for(GraphAgent a:crossoverCommonLink){
            
            String valid = API.isPathValid(gr, a.path);
            if(!valid.equalsIgnoreCase("Yes")){
                throw new IllegalStateException(valid+" at:"+a);
            }else{
                Log.print("Valid crossover",a);
                GraphAgent.successfullCrossover.incrementAndGet();
            }
        }
        return crossoverCommonLink;
    }

}


/*
7, 53, 81, 90, 12, 39, 49, 67, 18, 29, 48, 55, 42
*/