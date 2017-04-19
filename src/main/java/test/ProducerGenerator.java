package test;

import javax.jms.*;

/**
 * Created by mike on 3/6/17.
 */
public class ProducerGenerator {
    public SessionGenerator sg = null;

    public int deliveryMode = DeliveryMode.NON_PERSISTENT;

    public Session session = null;

    public ProducerGenerator(SessionGenerator sg) {
        this.sg = sg;
    }

    public ProducerGenerator(SessionGenerator sg, int deliveryMode) {
        this.sg = sg;
        this.deliveryMode = deliveryMode;
    }

    public void setSessionGenerator(SessionGenerator sg) throws JMSException {
        this.sg = sg;
    }

    public void setDeliveryMode(int deliveryMode) {
        this.deliveryMode = deliveryMode;
    }

    public MessageProducer getMessageProducerOfQueue(String queue) throws JMSException {
        if (sg != null) {
            session = sg.getSession();
            MessageProducer mp = session.createProducer(session.createQueue(queue));
            mp.setDeliveryMode(deliveryMode);
            return mp;
        }
        else {
            return null;
        }
    }

    public Session getSession() {
        return session;
    }

    public void close() {
        try {
            int ac = sg.getAcknowledgeMode();
            if (ac == Session.CLIENT_ACKNOWLEDGE || ac == Session.SESSION_TRANSACTED) {
                session.commit();
            }
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
