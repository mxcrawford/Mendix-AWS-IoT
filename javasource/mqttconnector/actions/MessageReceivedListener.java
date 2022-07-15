package mqttconnector.actions;

import com.mendix.core.Core;
import com.mendix.datahub.connector.mqtt.TopicMessageListener;
import com.mendix.logging.ILogNode;
import com.mendix.systemwideinterfaces.core.IContext;
import static com.mendix.core.Core.microflowCall;

public class MessageReceivedListener implements TopicMessageListener {
    private final String microflowName;
    private final IContext context;
    public MessageReceivedListener(IContext context, String microflowName){
        this.microflowName = microflowName;
        this.context = context;
    }
    @Override
    public void onMessageReceived(String topic, String payload) {
                microflowCall(microflowName)
                        .inTransaction(true)
                        .withParam("Topic", topic)
                        .withParam("Payload", payload)
                        .execute(context);
    }
}
