/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import lt.lb.commons.UUIDgenerator;
import lt.lb.commons.containers.LazyValue;
import lt.lb.commons.graphtheory.Algorithms;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.neurevol.evolution.NEAT.Agent;
import lt.lb.neurevol.evolution.NEAT.imp.FloatFitness;

/**
 *
 * @author laim0nas100
 */
public class GraphAgent extends Agent {

    public Orgraph graph;
    public Set<Long> nodes;
    public List<Long> path;
    public LazyValue<List<GLink>> links;

    public int pathSimpleLength() {
        return path.size();
    }

    public static AtomicLong timesFitnessComputed = new AtomicLong(0);
    public static AtomicLong emptyMutation = new AtomicLong(0);
    public static AtomicLong invalidCrossover = new AtomicLong(0);
    public static AtomicLong successfullCrossover = new AtomicLong(0);

    public GraphAgent(Collection<Long> path, Orgraph gr) {
        id = UUIDgenerator.nextUUID("GraphGenome");
        nodes = new HashSet<>(path);
        this.path = new LinkedList<>(path);
        links = new LazyValue<>(() -> GeneticSolution.getLinks(this.path, gr));
        this.graph = gr;
        if(isValid()){
            computeFitness();
        }

    }

    public void computeFitness() {
        timesFitnessComputed.incrementAndGet();
        this.fitness = new FloatFitness(Algorithms.getPathWeight(path, graph).floatValue());
    }

    public GraphAgent(GraphAgent agent) {
        this(agent.path, agent.graph);
    }

    public String toString() {
        return path.toString();
    }

    public boolean isValid() {
        return path.size() == nodes.size();
    }

}
