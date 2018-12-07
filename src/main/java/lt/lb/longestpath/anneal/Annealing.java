package lt.lb.longestpath.anneal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lt.lb.commons.F;
import lt.lb.commons.Lambda;
import lt.lb.commons.Log;
import lt.lb.commons.containers.tuples.Tuple;
import lt.lb.commons.graphtheory.Algorithms;
import lt.lb.commons.graphtheory.GLink;
import lt.lb.commons.graphtheory.Orgraph;
import lt.lb.commons.graphtheory.paths.PathGenerator;
import lt.lb.commons.graphtheory.paths.PathGenerator.ILinkPicker;
import lt.lb.commons.interfaces.Equator;
import lt.lb.commons.interfaces.ReadOnlyIterator;
import lt.lb.commons.misc.rng.RandomDistribution;
import lt.lb.commons.misc.rng.RandomRange;
import lt.lb.commons.misc.rng.RandomRanges;
import lt.lb.commons.threads.FastExecutor;
import lt.lb.commons.threads.TaskBatcher;
import lt.lb.longestpath.API;

/**
 *
 * @author laim0nas100
 */
public class Annealing {

    public static Executor exe = Executors.newCachedThreadPool();

    public static enum Move {
        SWAP, INSERT, REMOVE
    }

    public static class PathProduce {

        public Move type;
        public Long one, two;

        public PathProduce(Move type, Long one, Long two) {
            this.type = type;
            this.one = one;
            this.two = two;
        }
    }

    public static class NeighborhoodTuple {

        public AtomicLong fails = new AtomicLong();
        public AtomicLong success = new AtomicLong();
        public Collection<Tuple<List<Long>, PathProduce>> paths = new ConcurrentLinkedDeque<>();

    }
    
    public static Lambda.L2RS<NeighborhoodTuple> add = (t1, t2) -> {
        NeighborhoodTuple tuple = new NeighborhoodTuple();
        tuple.fails = new AtomicLong(t1.fails.addAndGet(t2.fails.get()));
        tuple.success = new AtomicLong(t1.success.addAndGet(t2.success.get()));
        tuple.paths.addAll(t1.paths);
        tuple.paths.addAll(t2.paths);
        return tuple;
    };

    public static NeighborhoodTuple neighborhoodSwap(Orgraph gr, List<Long> path, int depth) {
        Log.print("Apply swap");
        NeighborhoodTuple create = new NeighborhoodTuple();
        F.iterate(path, (i, v1) -> {
            TaskBatcher batcher = new TaskBatcher(exe);

            F.iterate(path, 1, (j, v2) -> {
                batcher.execute(() -> {
                    List<Long> newPath = new ArrayList<>(path.size());
                    newPath.addAll(path);
                    F.swap(newPath, i, j);
                    if (depth > 0) {
                        NeighborhoodTuple inner = neighborhoodSwap(gr, newPath, depth - 1);
                        add.apply(create, inner);
                    }

                    String pathValid = API.isPathValid(gr, newPath);
                    if (pathValid.equalsIgnoreCase("Yes")) {
                        create.paths.add(new Tuple<>(newPath, new PathProduce(Move.SWAP, path.get(i), path.get(j))));
                        create.success.incrementAndGet();
                    } else {
                        create.fails.incrementAndGet();
                    }
                });

            });
            batcher.awaitFailOnFirst();
        });

        return create;
    }

    public static NeighborhoodTuple neighborhoodRemove(Orgraph gr, List<Long> path, int depth) {
        Log.print("Apply remove");
        NeighborhoodTuple create = new NeighborhoodTuple();
        TaskBatcher batcher = new TaskBatcher(exe);
        F.iterate(path, (i, v) -> {
            batcher.execute(() -> {
                List<Long> newPath = new ArrayList<>(path.size());
                newPath.addAll(new ArrayList<>(path));
                newPath.remove((int) i);

                if (depth > 0) {
                    NeighborhoodTuple inner = neighborhoodRemove(gr, newPath, depth - 1);
                    add.apply(create, inner);
                }
                String pathValid = API.isPathValid(gr, newPath);
                if (pathValid.equalsIgnoreCase("Yes")) {
                    create.paths.add(new Tuple<>(newPath, new PathProduce(Move.REMOVE, path.get(i), null)));
                    create.success.incrementAndGet();
                } else {
                    create.fails.incrementAndGet();
                }
            });

        });
        batcher.awaitFailOnFirst();
        return create;
    }

    public static NeighborhoodTuple neighborhoodVertexCombine(Orgraph gr, List<Long> path, int depth) {
        Log.print("Apply vertex combine");
        NeighborhoodTuple create = new NeighborhoodTuple();
        Stream<Long> filter = gr.nodes.keySet().stream().filter(n -> !path.contains(n));
        ReadOnlyIterator<Long> unsusedVertex = ReadOnlyIterator.of(filter);
        F.iterate(unsusedVertex, (i, v) -> {
            TaskBatcher batcher = new TaskBatcher(exe);
            F.iterate(path, (j, v2) -> {
                batcher.execute(() -> {
                    List<Long> newPath = new ArrayList<>(path.size());
                    newPath.addAll(new ArrayList<>(path));
                    if (i >= newPath.size()) {
                        newPath.add(v);
                    } else {
                        newPath.add(i, v);
                    }

                    if (depth > 0) {
                        NeighborhoodTuple inner = neighborhoodVertexCombine(gr, newPath, depth - 1);
                        add.apply(create, inner);
                    }

                    String pathValid = API.isPathValid(gr, newPath);
                    if (pathValid.equalsIgnoreCase("Yes")) {
                        create.paths.add(new Tuple<>(newPath, new PathProduce(Move.INSERT, v, null)));
                        create.success.incrementAndGet();
                    } else {
                        create.fails.incrementAndGet();
                    }
                });

            });
            batcher.awaitFailOnFirst();
        });
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
        Log.print("Apply combiners");
        NeighborhoodTuple applyCombiners = Annealing.applyCombiners(gr, path, Arrays.asList(comSwap, comRemove, comVertex));
        if (depth == 0) {
            Predicate<Tuple<List<Long>, PathProduce>> filterDistinct = F.filterDistinct(Equator.valueEquator(t -> t.g1));
            F.filterInPlace(applyCombiners.paths, filterDistinct);
        }
        return applyCombiners;
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

    public static void anneal(Orgraph gr, RandomDistribution rng, AnnealingInfo info, AnnealingResult result) {

        Long pickRandom = rng.pickRandom(gr.nodes.keySet());

        RandomRange<ILinkPicker> r1 = new RandomRange(PathGenerator.nodeDegreeDistributed(rng, false), 0.5);
        RandomRange<ILinkPicker> r2 = new RandomRange(PathGenerator.nodeWeightDistributed(rng, false), 0.5);

        ILinkPicker linkPickerCombined = PathGenerator.linkPickerCombined(new RandomRanges<>(r1, r2), rng);

        List<GLink> pathLinks = PathGenerator.generateLongPathBidirectional(gr, pickRandom, linkPickerCombined);
        List<Long> path = API.getNodesIDs(pathLinks);
        Log.print("Starting path =", path);

        double temp = info.startingTemp;
        double finalTemp = info.finalTemp;

        int stagnation = 0;
        Double currentPathWeight = Algorithms.getPathWeight(path, gr);

        while (temp > finalTemp) {
            int limit = (int) (info.iterationsPerTemp * temp);
            Log.print("With temp:", temp);

            int iteration = 0;

            boolean improved = false;
            while (iteration < limit) {
                iteration++;
                NeighborhoodTuple def = Annealing.neighborhoodDefault(gr, path, 0);
                List<Long> randPath = rng.pickRandom(def.paths).g1;

                // optimization to append on the ends
                long lastNode = randPath.get(randPath.size() - 1);
                ArrayList<GLink> links = API.getLinks(randPath, gr);

                List<GLink> betterPath = PathGenerator.genericUniquePathBidirectionalVisitContinued(gr, lastNode, links, new HashSet<>(randPath), linkPickerCombined);
                randPath = API.getNodesIDs(betterPath);

                Log.print("Generated paths", def.paths.size());
                Double newPathWeight = Algorithms.getPathWeight(randPath, gr);
                result.weightEvaluations++;
                result.failedGenerations += def.fails.get();
                result.successGenerations += def.success.get();
                double delta = newPathWeight - currentPathWeight;
                Log.print("Iteration ", iteration, delta);
                if (delta > 0) {
                    currentPathWeight = newPathWeight;
                    path = randPath;
                    result.straightImprovements++;
                } else {
                    if (rng.nextDouble() < Math.exp(delta / temp)) {
                        currentPathWeight = newPathWeight;
                        path = randPath;
                        result.deteriorations++;
                    }
                }

                if (result.bestSoFar == null) {
                    result.bestSoFar = path;
                    result.bestSoFarWeight = currentPathWeight;
                } else {
                    if (result.bestSoFarWeight < currentPathWeight) {
                        result.bestSoFar = path;
                        result.bestSoFarWeight = currentPathWeight;
                        improved = true;
                        result.globalImprovements++;
                    }
                }

            }
            if (improved) {
                stagnation = 0;
            } else {
                stagnation++;
            }
            temp = info.tempUpdate.apply(temp);
            Log.print("Stagnation:", stagnation);
            if (stagnation >= info.maxStagnation) {
                break;
            }
        }
        result.reachedTemp = temp;
        result.maxStagnationReached = stagnation;
    }

}
