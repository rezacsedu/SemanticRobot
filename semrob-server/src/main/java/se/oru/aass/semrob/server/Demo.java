package se.oru.aass.semrob.server;

import java.security.CodeSource;
import java.util.Properties;

import org.apache.xmlrpc.WebServer;


public class Demo {
	
	public static void runDemo(boolean runningFromJar, CodeSource codeSource, Server server) {	
		
		try {     
			
			Properties prop = new Setting().getConfiguration(Setting.CONFIG_FILE_NAME);
			
			WebServer webServer = new WebServer(Integer.parseInt(prop.getProperty("server.port")));		
					
			
			if(server.initialize(runningFromJar, codeSource)) {
				webServer.addHandler(
						prop.getProperty("server.class.nickname"), 
						server);
			}
			    	
			webServer.start();
		   	System.out.println("Server :: READY >> ");
			
		} catch (Exception e) { 
		    System.out.println(e.getMessage()); 
		} 
	}
	
	public static void runDemo(boolean runningFromJar, CodeSource codeSource, Server_3 server, int mapWidthSize, int mapHeightSize, int mapElevationSize) {
		try {     
			
			Properties prop = new Setting().getConfiguration(Setting.CONFIG_FILE_NAME);
			
			WebServer webServer = new WebServer(Integer.parseInt(prop.getProperty("server.port")));		
			if(server.initialize(runningFromJar, codeSource, mapWidthSize, mapHeightSize, mapElevationSize)) {
				webServer.addHandler(
						prop.getProperty("server.class.nickname"), 
						server);
			}
			    	
			webServer.start();
		   	System.out.println("Server :: READY >> ");
			
		} catch (Exception e) { 
		    System.out.println(e.getMessage()); 
		} 
	}
}
