package com.github.laim0nas100.dbstore.idgen;

import com.github.laim0nas100.uncheckedutils.SafeOpt;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.jdbi.v3.core.Jdbi;

/**
 *
 * @author laim0nas100
 */
public class CachedCounter implements IdGenerator {

    protected CounterTable delegatedCounter;
    protected AtomicLong cachedCounter = null;
    private volatile long counterEnd;
    protected Lock lock = new ReentrantLock(true);
    protected long localIncrement;
    protected long counterIncrement;

    public CachedCounter(CounterTable delegatedCounter, long localIncrement) {
        this.delegatedCounter
                = Objects.requireNonNull(delegatedCounter);

        if (localIncrement <= 0) {
            throw new IllegalArgumentException(
                    "localIncrement must be greater than zero");
        }

        if (delegatedCounter.increment <= 0) {
            throw new IllegalArgumentException(
                    "counter increment must be greater than zero");
        }

        if (localIncrement > delegatedCounter.increment) {
            throw new IllegalArgumentException(
                    "localIncrement cannot exceed reserved increment");
        }

        this.counterIncrement = delegatedCounter.increment;
        this.localIncrement = localIncrement;

    }

    @Override
    public SafeOpt<Long> getAndIncrement() {
        while (true) {
            AtomicLong counter = cachedCounter;

            // First initialization.
            if (counter == null) {
                lock.lock();
                try {
                    if (cachedCounter == null) {
                        SafeOpt<Long> starting = delegatedCounter.getAndIncrement();
                        if (starting.hasError()) {
                            return starting;
                        }
                        long start = starting.get();

                        counterEnd = start + counterIncrement;
                        cachedCounter = new AtomicLong(start);
                        return starting;
                    }
                } finally {
                    lock.unlock();
                }

                continue;
            }

            long current = counter.get();
            long next = current + localIncrement;

            // Current increment still fits in the reserved block.
            if (next < counterEnd) {
                if (counter.compareAndSet(current, next)) {
                    return SafeOpt.of(current);
                }
                continue;
            }

            // Need to reserve another block.
            lock.lock();
            try {
                // Another thread may have refreshed the block
                // while we were waiting for the lock.
                counter = cachedCounter;

                current = counter.get();
                next = current + localIncrement;

                if (next < counterEnd) {
                    continue;
                }

                SafeOpt<Long> nextStart = delegatedCounter.getAndIncrement();
                if (nextStart.hasError()) {
                    return nextStart;
                }
                long start = nextStart.get();

                counterEnd = start + counterIncrement;
                counter.set(start);
                return nextStart;

            } finally {
                lock.unlock();
            }
        }
    }

    @Override
    public Jdbi getJdbi() {
        return delegatedCounter.getJdbi();
    }

    @Override
    public SafeOpt<Long> getCurrent() {
        return delegatedCounter.getCurrent();
    }

}
