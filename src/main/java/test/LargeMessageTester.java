package test;

/**
 * Created by mike on 3/16/17.
 */
public class LargeMessageTester extends Tester {
    public void test() {
        TestHelper.setTestQueue("LargeMessage");
        TestHelper.setReceiveMessageNumber(1);
        TestHelper.setProducerThreadNumber(1);
        TestHelper.setSendMessageNumber(1);
        TestHelper.setConsumerThreadNumber(1);
        TestHelper.setTestMessage(MessageGenerator.generateMessage(MessageGenerator._MB * 100));
        TestHelper.init();
        long sendTime = TestHelper.testSend();
        int totalSendMsg = 1;
        System.out.println("Produced " + totalSendMsg + " messages: " + sendTime + " ms, " + (double)totalSendMsg / sendTime * 1000 + " per messages/s.");
        long receiveTime = TestHelper.testReceive();
        int totalReceiveMsg = 1;
        System.out.println("Consumed " + totalReceiveMsg + " messages: " + receiveTime + " ms, " + (double)totalReceiveMsg / receiveTime * 1000 + " per messages/s.");
        TestHelper.close();
    }

    public static void main(String args[]) {
        new LargeMessageTester().test();
    }
}
