/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.tabu;

import java.util.List;

/**
 *
 * @author laim0nas100
 */
public class TabuResult {
    public long failedGenerations = 0;
    public long successGenerations = 0;
    public long straightImprovements = 0;
    public long weightEvaluations = 0;
    public long maxStagnationReached = 0;
    public long reachedIteration = 0;
    public List<Long> bestSoFar;
    public double bestSoFarWeight;
}
