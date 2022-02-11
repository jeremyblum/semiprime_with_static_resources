package edu.psu.jjb24.factorprimes;

import java.math.BigInteger;
import java.util.Random;

public class FactoringTask {
    private Thread thread;
    private final BigInteger semiprime; // Semiprime to factor
    private BigInteger lastTested;      // last potential factor tested
    private BigInteger factor;          // a prime factor of semiprime

    public FactoringTask(BigInteger semiprime, BigInteger lastTested) {
        this.semiprime = semiprime;  // Q: Do we need to ensure these are synchronized?
        this.lastTested = lastTested;
    }

    // Q: We want to make sure that semiprime, lastTested, and factor can be accessed
    // from different threads in a thread-safe way.  What changes should we make?
    // Do we need to change the constructor

    public              BigInteger getSemiprime() {
        return semiprime;
    }

    public synchronized void setLastTested(BigInteger lastTested) {
        this.lastTested = lastTested;
    }

    public synchronized BigInteger getLastTested() {
        return lastTested;
    }

    public synchronized BigInteger getFactor() {
        return factor;
    }

    public synchronized void setFactor (BigInteger factor) {
        this.factor = factor;
    }

    // Q: Right now what thread is execute going to occur on?  How do I fix this?
    public void execute(){
        thread = new Thread(() -> {
            // Q: How do I set testFactor equal to lastTested in a thread safe way?
            BigInteger testFactor = getLastTested();
            if (testFactor == null) {
                testFactor = BigInteger.probablePrime(semiprime.bitLength()/2,
                        new Random());
            }
            while (!semiprime.mod(testFactor).equals(BigInteger.ZERO)) {
                // Q: How to update lastTested with testFactor in a thread safe way?
                setLastTested(testFactor);

                testFactor = testFactor.nextProbablePrime();

                // After we complete cancel below, ask “What am I forgetting?”
                if (Thread.interrupted()) return;
            }
            setFactor(testFactor);
        });
        thread.start();
    }

    // Q: If cancel is invoked, how do I stop the background processing?
    public void cancel() {
        if (thread != null) thread.interrupt();
    }
}




