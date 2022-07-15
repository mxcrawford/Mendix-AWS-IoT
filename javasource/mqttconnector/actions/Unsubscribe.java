// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package mqttconnector.actions;

import com.mendix.core.Core;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import mqttconnector.implementation.UnsubscribeAction;

public class Unsubscribe extends CustomJavaAction<java.lang.Void>
{
	private IMendixObject __ConnectionDetail;
	private mqttconnector.proxies.ConnectionDetail ConnectionDetail;
	private java.lang.String Topic;

	public Unsubscribe(IContext context, IMendixObject ConnectionDetail, java.lang.String Topic)
	{
		super(context);
		this.__ConnectionDetail = ConnectionDetail;
		this.Topic = Topic;
	}

	@java.lang.Override
	public java.lang.Void executeAction() throws Exception
	{
		this.ConnectionDetail = this.__ConnectionDetail == null ? null : mqttconnector.proxies.ConnectionDetail.initialize(getContext(), __ConnectionDetail);

		// BEGIN USER CODE
		String clientIdPrefix = String.valueOf(Core.getConfiguration().getConstantValue("MqttConnector.ClientIdPrefix")) + Core.getConfiguration().getXASId();
		UnsubscribeAction unsubscribeAction = new UnsubscribeAction(clientIdPrefix, ConnectionDetail, Topic);
		return unsubscribeAction.execute();
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "Unsubscribe";
	}

	// BEGIN EXTRA CODE

	// END EXTRA CODE
}
