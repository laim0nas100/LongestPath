/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath;

import java.util.concurrent.TimeUnit;
import lt.lb.commons.F;
import lt.lb.commons.Log;

/**
 *
 * @author Laimonas-Beniusis-PC
 */
public class Simulation {
    
    
    
    public static void main(String[] str) {

       new GeneticSimulation(50);
       Log.print("END");
       F.unsafeRun(()->{
           Log.await(1, TimeUnit.HOURS);
       });

    }
}
