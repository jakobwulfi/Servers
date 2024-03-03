public class WaitingThread extends Thread {
    private volatile boolean running = true;

    public void stopWaiting() {
        System.out.println("Connection found!");
        running = false;
    }

    public void run() {
        while (running) {
            try {
                System.out.println("Waiting for connection....");
                Thread.sleep(9000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}