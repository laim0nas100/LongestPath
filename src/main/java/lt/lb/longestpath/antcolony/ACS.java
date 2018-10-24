/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.antcolony;

import java.util.ArrayList;
import java.util.HashMap;
import lt.lb.commons.graphtheory.GNode;
import lt.lb.commons.misc.RandomDistribution;
import lt.lb.commons.threads.TaskBatcher;

/**
 *
 * @author Lemmin
 */
public class ACS {
    
    public ACS(){
        
    }
    
    public Info info;
    public ArrayList<AntBoi> ants = new ArrayList<>();
    public int antCount = 10;
    public void init(){
        RandomDistribution rng = info.rng;
        HashMap<Long, GNode> nodes = info.graph.nodes;
        for(int i = 0; i < antCount; i++){
            
            AntBoi boi = new AntBoi(rng.nextLong((long)nodes.size()));
            boi.info = info;
            ants.add(boi);
        }
    }
    public void iteration(){
        
    }
    
}
