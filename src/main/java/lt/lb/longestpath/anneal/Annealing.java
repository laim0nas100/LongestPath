package lt.lb.longestpath.anneal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lt.lb.commons.F;
import lt.lb.commons.Lambda;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.interfaces.ReadOnlyIterator;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.commons.misc.rng.RandomRange;
import lt.lb.commons.misc.rng.RandomRanges;
import lt.lb.longestpath.API;

/**
 *
 * @author laim0nas100
 */
public class Annealing {
    
    public static class NeighborhoodTuple{
        int fails = 0;
        int success = 0;
        List<List<Long>> paths = new LinkedList<>();
        
    }
    public static Lambda.L2RS<NeighborhoodTuple> add = (t1, t2) -> {
        NeighborhoodTuple tuple = new NeighborhoodTuple();
        tuple.fails = t1.fails + t2.fails;
        tuple.success = t1.success + t2.success;
        tuple.paths.addAll(t1.paths);
        tuple.paths.addAll(t2.paths);
        return tuple;
    };
    
    public static NeighborhoodTuple neighborhoodSwap(Orgraph gr, List<Long> path, int depth) {
        
        NeighborhoodTuple create = new NeighborhoodTuple();
        F.iterate(path, (i, v1) -> {
            F.iterate(path, 1, (j, v2) -> {
                List<Long> newPath = new ArrayList<>(path.size());
                newPath.addAll(path);
                F.swap(path, i, j);
                if (depth > 0) {
                    NeighborhoodTuple inner = neighborhoodSwap(gr, newPath, depth - 1);
                    add.apply(create, inner);
                }
                
                String pathValid = API.isPathValid(gr, newPath);
                if (pathValid.equalsIgnoreCase("Yes")) {
                    create.paths.add(newPath);
                }
            });
        });
        if (depth == 0) {
            Predicate<List<Long>> filterDistinct = F.filterDistinct(Objects::equals);
            F.filterInPlace(create.paths, filterDistinct);
        }
        return create;
    }
    
    public static NeighborhoodTuple neighborhoodRemove(Orgraph gr, List<Long> path, int depth) {
        
        NeighborhoodTuple create = new NeighborhoodTuple();
        F.iterate(path, (i, v) -> {
            List<Long> newPath = new ArrayList<>(path.size());
            newPath.addAll(path);
            newPath.remove((int) i);
            
            if (depth > 0) {
                NeighborhoodTuple inner = neighborhoodRemove(gr, newPath, depth - 1);
                add.apply(create, inner);
            }
            String pathValid = API.isPathValid(gr, newPath);
            if (pathValid.equalsIgnoreCase("Yes")) {
                create.paths.add(newPath);
            }
        });
        if (depth == 0) {
            Predicate<List<Long>> filterDistinct = F.filterDistinct(Objects::equals);
            F.filterInPlace(create.paths, filterDistinct);
        }
        return create;
    }
    
    public static NeighborhoodTuple neighborhoodVertexCombine(Orgraph gr, List<Long> path, int depth) {
        
        NeighborhoodTuple create = new NeighborhoodTuple();
        Stream<Long> filter = gr.nodes.keySet().stream().filter(n -> !path.contains(n));
        ReadOnlyIterator<Long> unsusedVertex = ReadOnlyIterator.of(filter);
        F.iterate(unsusedVertex, (i, v) -> {
            F.iterate(path, (j, v2) -> {
                List<Long> newPath = new ArrayList<>(path.size());
                newPath.addAll(path);
                newPath.add(i, v);
                if (depth > 0) {
                    NeighborhoodTuple inner = neighborhoodVertexCombine(gr, newPath, depth - 1);
                    add.apply(create, inner);
                }
                
                String pathValid = API.isPathValid(gr, newPath);
                if (pathValid.equalsIgnoreCase("Yes")) {
                    create.paths.add(newPath);
                }
            });
        });
        if (depth == 0) {
            Predicate<List<Long>> filterDistinct = F.filterDistinct(Objects::equals);
            F.filterInPlace(create.paths, filterDistinct);
        }
        return create;
    }

    /**
     *
     * @param gr
     * @param path
     * @param depth
     * 
     * @return evaluations [valid,invalid] and list of candidates
     */
    public static NeighborhoodTuple neighborhoodDefault(Orgraph gr, List<Long> path, int depth) {
        
        NeighborhoodCombiner comRemove = (g, p) -> {
            return Annealing.neighborhoodRemove(g, p, depth);
        };
        NeighborhoodCombiner comSwap = (g, p) -> {
            return Annealing.neighborhoodSwap(g, p, depth);
        };
        NeighborhoodCombiner comVertex = (g, p) -> {
            return Annealing.neighborhoodVertexCombine(g, p, depth);
        };
        
        return Annealing.applyCombiners(gr, path, Arrays.asList(comSwap, comRemove, comVertex));
    }
    
    public static interface NeighborhoodCombiner extends Lambda.L2R<Orgraph, List<Long>, NeighborhoodTuple> {
        
    }
    
    public static NeighborhoodTuple neighborhoodPickRandom(Orgraph gr, List<Long> path, RandomDistribution rng, RandomRanges<NeighborhoodCombiner> ranges) {
        
        RandomRange<NeighborhoodCombiner> pickRandom = ranges.pickRandom(rng.nextDouble(ranges.getLimit()));
        NeighborhoodTuple apply = pickRandom.get().apply(gr, path);
        return apply;
    }

    public static NeighborhoodTuple applyCombiners(Orgraph gr, List<Long> path, List<NeighborhoodCombiner> combiners) {
        NeighborhoodTuple sum = new NeighborhoodTuple();
        for (NeighborhoodCombiner comb : combiners) {
            NeighborhoodTuple apply = comb.apply(gr, path);
            sum = add.apply(sum, apply);
        }
        return sum;
    }
    
}
