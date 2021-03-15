import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class Driver {
    Semaphore poolPermits = new Semaphore(5, true);
    long backoffDelay = 5000;

    public void execute() throws InterruptedException {
        // Get session
        if (poolPermits.tryAcquire(1, TimeUnit.MILLISECONDS)) {
            System.out.printf("%s acquired permit from semaphore.%n", getThreadName());

            //Lets say we execute a query and get a retryable exception, we need to backoff now:
            synchronized (this) {
                System.out.printf("%s entering synchronized block.%n", getThreadName());

                if (backoffDelay != 0) {
                    System.out.printf("%s waiting for %s milliseconds now, relinquishing lock now.%n", getThreadName(), backoffDelay);
                    wait(backoffDelay);
                }
                System.out.printf("%s finished waiting. Exiting out of synchronized block.%n", getThreadName());
            }

            // Execute was successful and now release session.
            poolPermits.release();
            System.out.printf("%s Released permit to semaphore.%n", getThreadName());

        } else {

            // We weren't able to retrieve a session.
            System.out.printf("%s did not acquire permit from semaphore.%n", getThreadName());
        }
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }
}

/*
Currently prints:
    T0 acquired permit from semaphore.
    T2 acquired permit from semaphore.
    T1 acquired permit from semaphore.
    T3 acquired permit from semaphore.
    T4 acquired permit from semaphore.
    T0 entering synchronized block.
    T0 waiting for 5000 milliseconds now, relinquishing lock now.
    T4 entering synchronized block.
    T4 waiting for 5000 milliseconds now, relinquishing lock now.
    T3 entering synchronized block.
    T3 waiting for 5000 milliseconds now, relinquishing lock now.
    T1 entering synchronized block.
    T1 waiting for 5000 milliseconds now, relinquishing lock now.
    T2 entering synchronized block.
    T2 waiting for 5000 milliseconds now, relinquishing lock now.
    T4 finished waiting. Exiting out of synchronized block.
    T3 finished waiting. Exiting out of synchronized block.
    T3 Released permit to semaphore.
    T4 Released permit to semaphore.
    T0 finished waiting. Exiting out of synchronized block.
    T0 Released permit to semaphore.
    T2 finished waiting. Exiting out of synchronized block.
    T2 Released permit to semaphore.
    T1 finished waiting. Exiting out of synchronized block.
    T1 Released permit to semaphore.

    Process finished with exit code 0
 */