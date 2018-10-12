/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath;

import lt.lb.longestpath.genetic.GeneticSimulation;
import java.util.concurrent.TimeUnit;
import lt.lb.commons.F;
import lt.lb.commons.Log;

/**
 *
 * @author Laimonas-Beniusis-PC
 */
public class Simulation {

    public static void main(String[] str) {

        Log.instant = true;
        Log.keepBuffer = false;
        //nodeCount, population, generetions
        new GeneticSimulation(200,50,20);
        Log.print("END");
        F.unsafeRun(() -> {
            Log.await(1, TimeUnit.HOURS);
        });

    }
}
