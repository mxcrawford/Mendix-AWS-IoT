package mqttconnector.implementation;

import com.mendix.core.CoreException;
import com.mendix.datahub.connector.mqtt.BrokerConnectionException;
import com.mendix.datahub.connector.mqtt.Connection;
import com.mendix.datahub.connector.mqtt.utils.CryptoException;
import com.mendix.datahub.connector.mqtt.utils.CryptoHelper;
import mqttconnector.proxies.ConnectionDetail;
import mqttconnector.proxies.constants.Constants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Commons {

    private Commons(){

    }

    public static Boolean validateParameters(ConnectionDetail detail, String topic){
        return  (detail != null && (detail.getBrokerHost()!= null && !detail.getBrokerHost().isEmpty()) && (topic !=null && !topic.isEmpty()));
    }
    public static Boolean validateConnectionDetails(ConnectionDetail detail){
        return  (detail != null && (detail.getBrokerHost()!= null && !detail.getBrokerHost().isEmpty()));
    }
    public static int getQoS(mqttconnector.proxies.QoS qos){
        if (qos == null)
            return 0;
        if(qos.name().equals("AtleastOnce"))
            return 1;
        else if(qos.name().equals("ExactlyOnce"))
            return 2;
        else
            return 0;
    }

    public static Connection getBrokerConnection(String clientIdPrefix, ConnectionDetail connectionDetail) throws CoreException, BrokerConnectionException, CryptoException {

        String host = connectionDetail.getBrokerHost();
        boolean matches = host.startsWith("tcp://")
                || host.startsWith("ssl://")
                || host.startsWith("ws://")
                || host.startsWith("wss://");
        if (!matches) {
            host = connectionDetail.getSSL() ? "ssl://" + host: "tcp://" + host;
        }
        var connection = new Connection(clientIdPrefix, host, connectionDetail.getBrokerPort());

        if (connectionDetail.getSSL()) {
            setCertificate(connectionDetail, connection);
        }
        connection.setConnectionTimeout(connectionDetail.getTimeOut());
        connection.setAutomaticReconnect(connectionDetail.getAutoReconnect());
        connection.setCleanSession(connectionDetail.getCleanSession());
        connection.setKeepAliveInterval(connectionDetail.getKeepAliveTime());
        connection.setAuthenticationMethod(getAuthenticationMethod(connectionDetail));
        if(connection.getAuthenticationMethod().equals(Connection.AuthenticationMethod.BASIC) && (connectionDetail.getUserName() == null || connectionDetail.getUserName().isBlank()))
            throw new BrokerConnectionException("User name cannot be empty if authentication method is basic");

        if(connection.getAuthenticationMethod().equals(Connection.AuthenticationMethod.BASIC) && (connectionDetail.getPassword() == null || connectionDetail.getPassword().isBlank()))
            throw new BrokerConnectionException("Password cannot be empty if authentication method is basic");
        connection.setHostUserName(connectionDetail.getUserName());
        if(connection.getAuthenticationMethod().equals(Connection.AuthenticationMethod.BASIC)) {
            new CryptoHelper().inputAndKeyValidation(connectionDetail.getPassword(), Constants.getEncryptionKey());
            connection.setHostPassword(new CryptoHelper().decrypt(connectionDetail.getPassword(), Constants.getEncryptionKey()));
        }
        return connection;
    }

    private static void setCertificate(ConnectionDetail connectionDetail, Connection connection) throws CryptoException {
        String ca = connectionDetail.getCA();
        if (ca != null) {
            InputStream caInput = new ByteArrayInputStream(ca.getBytes(StandardCharsets.UTF_8));
            connection.setCaFile(caInput);
        }

        String clientKey = connectionDetail.getClientKey();
        if (clientKey != null) {
            InputStream clientKeyInput = new ByteArrayInputStream(clientKey.getBytes(StandardCharsets.UTF_8));
            connection.setClientKey(clientKeyInput);
        }

        String clientCertificate = connectionDetail.getClientCertificate();
        if (clientCertificate != null) {
            InputStream clientCertificateInput =new ByteArrayInputStream(clientCertificate.getBytes(StandardCharsets.UTF_8));
            connection.setClientCertificate(clientCertificateInput);
        }
        if (connectionDetail.getCertificatePassword() != null && !connectionDetail.getCertificatePassword().isBlank()) {
            new CryptoHelper().inputAndKeyValidation(connectionDetail.getCertificatePassword(), Constants.getEncryptionKey());
            connection.setPassword(new CryptoHelper().decrypt(connectionDetail.getCertificatePassword(), Constants.getEncryptionKey()));
        }
    }

    private static Connection.AuthenticationMethod getAuthenticationMethod(ConnectionDetail connectionDetail) throws CoreException {
        if(connectionDetail.getAuthenticationMethod() == null)
            return Connection.AuthenticationMethod.NONE;
        switch (connectionDetail.getAuthenticationMethod())
        {
            case NONE:
                return Connection.AuthenticationMethod.NONE;
            case BASIC:
                return Connection.AuthenticationMethod.BASIC;
            default:
                throw new CoreException("Unknown authentication method provided.");
        }
    }
}
