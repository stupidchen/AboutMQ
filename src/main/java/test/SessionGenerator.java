package test;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import java.util.List;
import java.util.Vector;

/**
 * Created by mike on 3/10/17.
 */
public class SessionGenerator {
    private int acknowledgeMode = Session.AUTO_ACKNOWLEDGE;

    private boolean transacted = false;

    private ConnectionGenerator cg = null;

    private List<Connection> connections;

    public SessionGenerator(ConnectionGenerator cg) {
        this.cg = cg;
        connections = new Vector<Connection>();
    }

    public SessionGenerator setCg(ConnectionGenerator cg) {
        this.cg = cg;
        return this;
    }

    public SessionGenerator setTransacted(boolean transacted) {
        this.transacted = transacted;
        return this;
    }

    public int getAcknowledgeMode() {
        return acknowledgeMode;
    }

    public SessionGenerator setAcknowledgeMode(int acknowledgeMode) {
        this.acknowledgeMode = acknowledgeMode;
        return this;
    }

    public Session getSession() throws JMSException {
        if (cg != null) {
            Connection connection = cg.getConnection();
            connections.add(connection);
            Session session = connection.createSession(transacted, acknowledgeMode);
            return session;
        }
        else {
            return null;
        }
    }

    public void close() {
        for (int i = 0; i < connections.size(); i++) {
            try {
                Connection connection = connections.get(i);
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
}
