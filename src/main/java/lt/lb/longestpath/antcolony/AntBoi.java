/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.antcolony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.GNode;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.longestpath.genetic.GeneticSolution;

/**
 *
 * @author Lemmin
 */
public class AntBoi {
    
    public Info info;
    public ArrayList<Long> currentPath = new ArrayList<>();
    
    
    
    public AntBoi(Long startingNode){
        currentPath.add(startingNode);
    }
    
    public void constructInitialSolution(){
        HashMap<Long, GNode> nodes = info.graph.nodes;
        Integer node = info.rng.nextInt(nodes.size());
        List<GLink> generateLongPathBidirectional = PathGenerator.generateLongPathBidirectional(info.graph, node, PathGenerator.nodeDegreeDistributed(info.rng));
        ArrayList<Long> nodesIDs = GeneticSolution.getNodesIDs(generateLongPathBidirectional);
        
    }
    
    
    public Long currentNode(){
        return currentPath.get(currentPath.size()-1);
    }
    
    public boolean pickNextNode(){
        throw new UnsupportedOperationException("not yet");
    }
    
    
    
}
