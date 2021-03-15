public class Main {

    public static void main(String[] args) throws InterruptedException {
        final Driver driver = new Driver();

        Runnable runnable = () -> {
            try {
                driver.execute();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        int numberOfThreads = 5;
        Thread[] threadArray = new Thread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            threadArray[i] = new Thread(runnable, "T" + i);
        }

        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i].start();
        }

        for (int i = 0; i < threadArray.length; i++) {
            threadArray[i].join();
        }
    }
}


