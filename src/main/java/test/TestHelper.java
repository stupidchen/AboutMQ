package test;

import javax.jms.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by mike on 3/6/17.
 */
public class TestHelper {
    private static String testQueue = "Test1";

    private static String testMessage = "Test";

    private static String brokerUrl = "failover:(tcp://VB3:61616)";

    private static int sendMessageNumber = 5000;

    private static int producerThreadNumber = 8;

    private static int receiveMessageNumber = 5000;

    private static int consumerThreadNumber = 8;

    private static long timeout = 1000 * 1000000;

    private static SessionGenerator psg;

    private static SessionGenerator csg;

    public static void setTestQueue(String testQueue) {
        TestHelper.testQueue = testQueue;
    }

    public static void setTestMessage(String testMessage) {
        TestHelper.testMessage = testMessage;
    }

    public static void setBrokerUrl(String brokerUrl) {
        TestHelper.brokerUrl = brokerUrl;
    }

    public static void setSendMessageNumber(int sendMessageNumber) {
        TestHelper.sendMessageNumber = sendMessageNumber;
    }

    public static void setProducerThreadNumber(int producerThreadNumber) {
        TestHelper.producerThreadNumber = producerThreadNumber;
    }

    public static void setReceiveMessageNumber(int receiveMessageNumber) {
        TestHelper.receiveMessageNumber = receiveMessageNumber;
    }

    public static void setConsumerThreadNumber(int consumerMessageThread) {
        TestHelper.consumerThreadNumber = consumerMessageThread;
    }

    public static void setTimeout(long timeout) {
        TestHelper.timeout = timeout;
    }

    public static void init() {
        psg = new SessionGenerator(new ConnectionGenerator(brokerUrl))
                .setTransacted(false)
                .setAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);

        csg = new SessionGenerator(new ConnectionGenerator(brokerUrl))
                .setTransacted(false)
                .setAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
    }

    public static long testSend() {
//        final CountDownLatch startCount = new CountDownLatch(1);
        final CountDownLatch endCount = new CountDownLatch(producerThreadNumber);
        ExecutorService es = Executors.newFixedThreadPool(producerThreadNumber);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < producerThreadNumber; i++) {
            es.execute(new Runnable() {
                public void run() {
                    try {
                        ProducerGenerator pg = new ProducerGenerator(psg, DeliveryMode.PERSISTENT);
                        MessageProducer producer = pg.getMessageProducerOfQueue(testQueue);
//                        startCount.await();
                        for (int j = 0; j < sendMessageNumber; j++) {
                            TextMessage textMessage = pg.getSession().createTextMessage();
                            textMessage.setText(testMessage);
                            producer.send(textMessage);
                        }
                        pg.close();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    } finally {
                        endCount.countDown();
                    }
               }
            });
        }
//        startCount.countDown();
        long time = -1;
        try {
            endCount.await();
            time = System.currentTimeMillis() - startTime;
            if (time > timeout) time = -1;
            return time;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (time != -1)
                es.shutdown();
            else
                es.shutdownNow();
        }

        return time;
    }

    public static long testReceive() {
        //final CountDownLatch startCount = new CountDownLatch(1);
        final CountDownLatch endCount = new CountDownLatch(consumerThreadNumber);
        ExecutorService es = Executors.newFixedThreadPool(consumerThreadNumber);
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < consumerThreadNumber; i++) {
            es.execute(new Runnable() {
                public void run() {
                    try {
                        ConsumerGenerator cg = new ConsumerGenerator(csg);
                        MessageConsumer consumer = cg.getMessageConsumerOfQueue(testQueue);
                        //startCount.await();
                        for (int j = 0; j < receiveMessageNumber; j++) {
                            TextMessage msg = (TextMessage) consumer.receive();
                        }
                        cg.close();
                    } catch (JMSException e) {
                        e.printStackTrace();
                    } finally {
                        endCount.countDown();
                    }
                }
            });
        }
//        startCount.countDown();
        long time = -1;
        try {
            endCount.await();
            time = System.currentTimeMillis() - startTime;
            if (time > timeout) {
                time = -1;
            }
            return time;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (time != -1)
                es.shutdown();
            else
                es.shutdownNow();
        }

        return time;
    }

    public static void close() {
        psg.close();
        csg.close();
    }

    public static void main(String args[]) {
        setTestMessage(MessageGenerator.generateMessage(MessageGenerator._MB));
        init();
        long sendTime = testSend();
        int totalSendMsg = sendMessageNumber * producerThreadNumber;
        System.out.println("Produced " + totalSendMsg + " messages: " + sendTime + " ms, " + (double)totalSendMsg / sendTime * 1000 + " per messages/s.");
        long receiveTime = testReceive();
        int totalReceiveMsg = receiveMessageNumber * consumerThreadNumber;
        System.out.println("Consumed " + totalReceiveMsg + " messages: " + receiveTime + " ms, " + (double)totalReceiveMsg / receiveTime * 1000 + " per messages/s.");
        close();
    }
}
