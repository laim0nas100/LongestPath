/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.genetic;

import java.util.Collection;
import lt.lb.longestpath.genetic.GeneticSolution.GraphAgent;
import lt.lb.neurevol.evolution.NEAT.interfaces.AgentSimilarityEvaluator;

/**
 *
 * @author laim0nas100
 */
public class GraphAgentSimilarityEvaluator implements AgentSimilarityEvaluator<GraphAgent> {

    public int intersection(Collection c1, Collection c2) {
        int count = 0;
        for (Object ob : c1) {
            count += c2.contains(ob) ? 1 : 0;
        }
        return count;
    }

    public int disjoint(Collection c1, Collection c2) {
        int commonCount = intersection(c1, c2);
        int sum = c1.size() + c2.size();
        return sum - 2 * commonCount;
    }

    // use common nodes
    @Override
    public double similarity(GraphAgent g1, GraphAgent g2) {
        int maxNodeCount = Math.max(g1.path.size(), g2.path.size());
        double inter = intersection(g1.path, g2.path) + 1;

        return disjoint(g1.path,g2.path) * 0.8;

    }

}
