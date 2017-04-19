package test;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;

/**
 * Created by mike on 3/6/17.
 */
public class ConsumerGenerator {
    private SessionGenerator sg = null;

    private Session session;

    public ConsumerGenerator(SessionGenerator sg) {
        this.sg = sg;
    }

    public void setSessionGenerator(SessionGenerator sg) {
        this.sg = sg;
    }

    public MessageConsumer getMessageConsumerOfQueue(String queue) throws JMSException {
        if (sg != null) {
            session = sg.getSession();
            return session.createConsumer(session.createQueue(queue));
        }
        else {
            return null;
        }
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
