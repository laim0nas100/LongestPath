/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.longestpath.antcolony;

import java.util.ArrayList;
import java.util.List;
import lt.lb.commons.containers.caching.LazyValue;
import lt.lb.commons.graphtheory.Algorithms;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.longestpath.API;

/**
 *
 * @author Lemmin
 */
public class AntBoi {

    public ACSinfo info;
    public ArrayList<Long> currentPath = new ArrayList<>();

    public LazyValue<List<GLink>> links = new LazyValue<>(() -> {
        return API.getLinks(currentPath, info.graph);
    });

    public LazyValue<Double> cost = new LazyValue<>(() -> {
        info.evluations.incrementAndGet();
        return Algorithms.getPathWeight(currentPath, info.graph);
    });

    public AntBoi(ACSinfo info, List<Long> list) {
        currentPath.addAll(list);
        this.info = info;
    }

    @Override
    public String toString() {
        return this.cost.get() + " " + this.currentPath;
    }

}
