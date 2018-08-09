package se.oru.aass.semrob.client.infoRequester;

import java.io.IOException;
import java.util.Vector;

import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class ServerConnector {
	
	public static String getServerResponse (String serverIP, 
			int serverPort, 
			String serverClassNickname, 
			String serverMethodName, 
			Vector<Object> params){
		
		try {
			XmlRpcClient client;
			client = new XmlRpcClient(serverIP, serverPort);
			String response;
			response = (String) client.execute(serverClassNickname + "." + serverMethodName, params);
			return response;
				
		} catch (XmlRpcException | IOException e) {
				e.printStackTrace();
				return "";		
		} 	   
	    
	}
}
