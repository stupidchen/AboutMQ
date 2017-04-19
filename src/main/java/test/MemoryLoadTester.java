package test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;

/**
 * Created by mike on 3/13/17.
 */
public class MemoryLoadTester extends Tester {
    private static String testName = "MemoryLoadTester";

    private static int round = 3000;

    public void test() {
        File outputFile = new File(testName + fileExt);
        PrintStream ps;
        try {
            ps = new PrintStream(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        final long[] time = new long[2];
        final CountDownLatch s = new CountDownLatch(1);
        Thread sendTask = new Thread(new Runnable() {
            public void run() {
                time[0] = TestHelper.testSend();
                s.countDown();
            }
        });

        Thread receiveTask = new Thread(new Runnable() {
            public void run() {
                try {
                    s.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time[1] = TestHelper.testReceive();
            }
        });

        TestHelper.setBrokerUrl("tcp://VB3:61616");
        TestHelper.init();
        int connection = 10;
        for (int i = 100; ; i++) {
            TestHelper.setTimeout(i * 1500);
            TestHelper.setProducerThreadNumber(connection);
            TestHelper.setConsumerThreadNumber(connection);
            TestHelper.setSendMessageNumber(i * round / connection);
            TestHelper.setReceiveMessageNumber(i * round / connection);
            TestHelper.setTestQueue(testName + "_round_" + i);
            sendTask.start();
            receiveTask.start();
            long sendTime = time[0];
            int totalSendMsg = i * round;
            String sendHint = "Produced " + totalSendMsg + " messages: " + sendTime + " ms, " + (double)totalSendMsg / sendTime * 1000 + " per messages/s.";
            long receiveTime = time[1];
            int totalReceiveMsg = i * round;
            String receiveHint = "Consumed " + totalReceiveMsg + " messages: " + receiveTime + " ms, " + (double)totalReceiveMsg / receiveTime * 1000 + " per messages/s.";
            System.out.println(sendHint);
            System.out.println(receiveHint);
            ps.println(sendHint);
            ps.println(receiveHint);
            if (time[0] == -1 || time[1] == -1) break;
        }
        TestHelper.close();
        ps.close();
    }
}
