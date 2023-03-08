package at.ac.uibk.swa.util;

import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

public class DoublePredicateCountingCollector<T>
        implements Collector<T, DoubleCounter, DoubleCounter>
{
    private final Predicate<T> firstPredicate;
    private final Predicate<T> secondPredicate;

    public DoublePredicateCountingCollector(
            Predicate<T> firstPredicate, Predicate<T> secondPredicate)
    {
        this.firstPredicate = firstPredicate;
        this.secondPredicate = secondPredicate;
    }

    @Override
    public Supplier<DoubleCounter> supplier() {
        return DoubleCounter::new;
    }

    @Override
    public BiConsumer<DoubleCounter, T> accumulator() {
        return (acc, elem) -> {
            if (this.firstPredicate.test(elem)) {
                acc.first();
            }
            if (this.secondPredicate.test(elem)) {
                acc.second();
            }
        };
    }

    @Override
    public BinaryOperator<DoubleCounter> combiner() {
        return (acc1, acc2) -> new DoubleCounter(
                acc1.getMatchesFirst() + acc2.getMatchesFirst(),
                acc1.getMatchesSecond() + acc2.getMatchesSecond()
        );
    }

    @Override
    public Function<DoubleCounter, DoubleCounter> finisher() {
        return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED);
    }
}