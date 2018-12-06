/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.anneal;

import java.util.function.Function;

/**
 *
 * @author laim0nas100
 */
public class AnnealingInfo {
    public int iterationsPerTemp;
    public double startingTemp;
    public double finalTemp;
    public int maxStagnation;
    public Function<Double,Double> tempUpdate;
}
