import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static BlockingQueue<String> queueForA = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueForB = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queueForC = new ArrayBlockingQueue<>(100);

    public static void main(String[] args) throws InterruptedException {
        new Thread(
                () -> {
                    for (int i = 0; i < 10_000; i++) {
                        String text = generateText("abc", 100_000);
                        try {
                            queueForA.put(text);
                            queueForB.put(text);
                            queueForC.put(text);
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }
        ).start();

        Thread maxA = createMaxCharThread('a', queueForA);
        maxA.start();
        Thread maxB = createMaxCharThread('b', queueForB);
        maxB.start();
        Thread maxC = createMaxCharThread('c', queueForC);
        maxC.start();

        maxA.join();
        maxB.join();
        maxC.join();

    }

    private static Thread createMaxCharThread(char target, BlockingQueue<String> queue) {
        return new Thread(
                () -> {
                    StringBuilder maxCountString = new StringBuilder();
                    int maxCount = 0;
                    for (int i = 0; i < 10_000; i++) {
                        try {
                            String text = queue.take();
                            int count = count(target, text);
                            if (count > maxCount) {
                                maxCountString.replace(0, maxCountString.length(), text);
                                maxCount = count;
                            }
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                    System.out.println("Максимальное количество символов '" + target + "' (" + maxCount + ") в строке " + maxCountString.substring(0, 100) + "...");
                }
        );
    }

    private static int count(char target, String text) {
        return (int) text.chars().filter(c -> c == target).count();
    }

    private static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}