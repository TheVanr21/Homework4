import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {

    public static final int QUEUE_SIZE = 100;
    public static final int TEXT_QUANTITY = 10_000;
    public static final int TEXT_SIZE = 100_000;

    public static BlockingQueue<String> queueForA = new ArrayBlockingQueue<>(QUEUE_SIZE);
    public static BlockingQueue<String> queueForB = new ArrayBlockingQueue<>(QUEUE_SIZE);
    public static BlockingQueue<String> queueForC = new ArrayBlockingQueue<>(QUEUE_SIZE);

    public static void main(String[] args) throws InterruptedException {
        new Thread(
                () -> {
                    for (int i = 0; i < TEXT_QUANTITY; i++) {
                        String text = generateText("abc", TEXT_SIZE);
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
                    String maxCountString = "";
                    int maxCount = 0;
                    for (int i = 0; i < TEXT_QUANTITY; i++) {
                        try {
                            String text = queue.take();
                            int count = count(target, text);
                            if (count > maxCount) {
                                maxCountString = text;
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