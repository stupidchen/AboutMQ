package test;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * Created by mike on 3/10/17.
 */
public class ConnectionGenerator {
    private String USERNAME = ActiveMQConnection.DEFAULT_USER;

    private String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;

    private String BROKERURL = ActiveMQConnection.DEFAULT_BROKER_URL;

    public ConnectionGenerator(String username, String password, String brokerUrl) {
        this.USERNAME = username;
        this.PASSWORD = password;
        this.BROKERURL = brokerUrl;
    }

    public ConnectionGenerator(String brokerUrl) {
        this.BROKERURL =brokerUrl;
    }

    public ConnectionGenerator setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
        return this;
    }

    public ConnectionGenerator setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
        return this;
    }

    public ConnectionGenerator setBROKERURL(String BROKERURL) {
        this.BROKERURL = BROKERURL;
        return this;
    }

    public Connection getConnection() {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, BROKERURL);
            factory.setUseAsyncSend(true);
            factory.setProducerWindowSize(102400);
            Connection connection = factory.createConnection();
            connection.start();
            return connection;
        } catch (JMSException e) {
            e.printStackTrace();
            return null;
        }
    }
}
