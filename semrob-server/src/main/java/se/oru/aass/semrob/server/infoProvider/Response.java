package se.oru.aass.semrob.server.infoProvider;

public interface Response {
	public static final String SPLITTER_SERVER_RESPONSE_INSTANCE = "#";
	public static final String SPLITTER_SERVER_RESPONSE_INSTANCE_INFO = "&";
	public void query();
	public void printResult();
	public String getResponse();
}
