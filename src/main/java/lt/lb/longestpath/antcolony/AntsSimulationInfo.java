/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.antcolony;

/**
 *
 * @author Lemmin
 */
public class AntsSimulationInfo {
    public int ants = 10;
    public int iterations = 100;
    public double localPheromoneInfluence = 0.1;
    public double decay = 0.1;
    public double greedyChance = 0.3; // 30%
}