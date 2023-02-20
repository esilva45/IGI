package br.com.asyg.connector.bmg.docusign;

import org.json.JSONObject;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

public class DocuSignConnector extends Connector implements ConnectorInterface {
	private static final String COPYRIGHT = "Licensed Materials - Property of Asyg";
	public static final String[] CONNECTOR_MODES = new String[] { "AddOnly", "Iterator", "Lookup", "CallReply", "Delete" };
	private static final String myName = "DocuSign Connector";
	private String url;
	private String user;
	private String password;
	private String method;
	private String organization;
	private String entrytype;
	
	public DocuSignConnector() {
		setName(myName);
	    setModes(CONNECTOR_MODES);
	}
	
	public void terminate() throws Exception {
		this.url = null;
	    this.user = null;
	    this.password = null;
	    this.method = null;
	    this.organization = null;
	    this.entrytype = null;
	    super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		this.url = getParam("url");
	    this.user = getParam("user");
	    this.password = getParam("password");
	    this.method = getParam("method");
	    this.organization = getParam("organization");
	    this.entrytype = getParam("entrytype");
	    getToken();
	}
	
	public void selectEntries() throws Exception {
		logmsg(">>> selectEntries");
	}
	
	public Entry getNextEntry() throws Exception {
		logmsg(">>> getNextEntry");
		Entry var1 = null;
		return var1;
	}
	
	public void putEntry(Entry entry) throws Exception {

		JSONObject json = new JSONObject();
		json.put("UID", entry.getString("eruid"));
		json.put("FirstName", entry.getString("erfirstname"));
		json.put("LastName", entry.getString("erlastname"));
		json.put("Email", entry.getString("eremail"));
		
		logmsg(">>> Json " + json.toString());
	}
	
	public Entry queryReply(Entry entry) throws Exception {
		logmsg(">>> queryReply");
		return entry;
	}
	
	public Entry findEntry(SearchCriteria search) throws Exception {
		logmsg(">>> findEntry " + search);
		return null;
	}
	
	@SuppressWarnings("unused")
	private void deleteUser(Entry entry) throws Exception {
		logmsg(">>> deleteUser");
	}
	
	@SuppressWarnings("unused")
	private Entry sendRequestReadResponse(String url, Entry entry) throws Exception {
	    sendRequest(url, entry);
	    return entry;
	  }
	
	private void sendRequest(String strURL, Entry entry) throws Exception {
		logmsg(">>> sendRequest " + strURL + " " + entry);
	}
	
	private void getToken() throws Exception {
		logmsg(">>> url " + url);
		logmsg(">>> user " + user);
		logmsg(">>> password " + password);
		logmsg(">>> method " + method);
		logmsg(">>> organization " + organization);
		logmsg(">>> entrytype " + entrytype);
	}
	
	public String getVersion() {
		logmsg(COPYRIGHT);
		return "2.1-di7.1.1 %I% 20%E%";
	}
}
