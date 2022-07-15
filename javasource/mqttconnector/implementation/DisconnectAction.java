package mqttconnector.implementation;

import com.mendix.core.CoreException;
import com.mendix.datahub.connector.mqtt.BrokerConnection;
import com.mendix.datahub.connector.mqtt.BrokerConnectionException;
import com.mendix.datahub.connector.mqtt.utils.CryptoException;
import com.mendix.logging.ILogNode;
import mqttconnector.proxies.ConnectionDetail;

import static mqttconnector.implementation.Commons.getBrokerConnection;
import static mqttconnector.implementation.Commons.validateConnectionDetails;

public class DisconnectAction {

    private String clientIdPrefix;
    private ConnectionDetail connectionDetail;
    private static final ILogNode log= CoreProxyImpl.getInstance().getLogger();

    public DisconnectAction(ConnectionDetail connectionDetail1, String clientIdPrefix)
    {
        this.connectionDetail = connectionDetail1;
        this.clientIdPrefix = clientIdPrefix;
    }

    public Void execute() throws CoreException, BrokerConnectionException, CryptoException {
        Boolean valid = validateConnectionDetails(connectionDetail);
        if (Boolean.FALSE.equals(valid)) {
            throw new BrokerConnectionException("Connection Details Missing or Invalid Host Name provided");
        }
        log.debug("Disconnecting from host " + connectionDetail.getBrokerHost());
        var brokerConnection = new BrokerConnection(getBrokerConnection(clientIdPrefix, connectionDetail));
        brokerConnection.disconnect();
        log.debug("Disconnected from host " + connectionDetail.getBrokerHost());
        return null;
    }
}
