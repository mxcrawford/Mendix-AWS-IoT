package mqttconnector.implementation;

import com.mendix.core.CoreException;
import com.mendix.datahub.connector.mqtt.BrokerConnection;
import com.mendix.datahub.connector.mqtt.BrokerConnectionException;
import com.mendix.datahub.connector.mqtt.operation.BrokerSubscriptionException;
import com.mendix.datahub.connector.mqtt.utils.CryptoException;
import mqttconnector.actions.MessageReceivedListener;
import mqttconnector.proxies.*;

import static mqttconnector.implementation.Commons.*;

public class SubscribeAction {

    private final String clientIdPrefix;
    private final ConnectionDetail connectionDetail;
    private final String topic;
    private final MessageReceivedListener messageReceivedListener;
    private final QoS qoS;


    public SubscribeAction( java.lang.String clientIdPrefix, ConnectionDetail connectionDetail1, java.lang.String topic, MessageReceivedListener messageReceivedListener, QoS qoS) {

        this.clientIdPrefix = clientIdPrefix;
        this.connectionDetail = connectionDetail1;
        this.topic = topic;
        this.messageReceivedListener = messageReceivedListener;
        this.qoS = qoS;
    }

    public Void execute() throws BrokerSubscriptionException, CoreException, BrokerConnectionException, CryptoException {
        Boolean valid = validateParameters(connectionDetail, topic);
        if (Boolean.FALSE.equals(valid)) {
            throw new BrokerSubscriptionException("Connection Details Missing or Invalid Host Name provided");
        }
        var brokerConnection = new BrokerConnection(getBrokerConnection(clientIdPrefix, connectionDetail));
        brokerConnection.subscribe(topic, getQoS(qoS), messageReceivedListener);
        return null;
    }






}
