/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.anneal;

import java.util.List;

/**
 *
 * @author laim0nas100
 */
public class AnnealingResult {
    public long failedGenerations = 0;
    public long successGenerations = 0;
    public long straightImprovements = 0;
    public long weightEvaluations = 0;
    public long deteriorations = 0;
    public List<Long> bestSoFar;
    public double bestSoFarWeight;
    public long globalImprovements;
    public long maxStagnationReached;
    public double reachedTemp;
    
}
