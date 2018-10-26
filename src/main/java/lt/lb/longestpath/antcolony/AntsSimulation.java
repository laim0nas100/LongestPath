/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.antcolony;

import lt.lb.commons.Log;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.longestpath.API;

/**
 *
 * @author Lemmin
 */
public class AntsSimulation {
    
    public AntBoi bestBoi;
    public ACS acs;
    public AntsSimulation(Orgraph graph, ThreadLocal<RandomDistribution> uniform, AntsSimulationInfo asi){
        Info info = new Info();
        info.graph = graph;
        info.rng = uniform;
        info.useGreed = asi.greedyChance;
        
        acs = new ACS();
        AntBoi search = acs.search(info, asi.iterations, asi.ants, asi.maxStagnation, asi.localPheromoneInfluence, asi.decay);
        bestBoi = search;
        Log.printLines(acs.bests);
        Log.print(search);
        Log.print("Is it valid?",API.isPathValid(info.graph, search.currentPath));
        
    }
}
