package br.com.asyg.connector.bmg.voll;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

import br.com.asyg.connector.exceptions.ConnectorException;

public class VollConnector extends Connector implements ConnectorInterface {
	private static String VERSION_INFO = null;
	private String clientId, clientSecret, token, url;
	
	public VollConnector() {
		setName("Voll Connector");
		setModes(new String[] { "Iterator", "AddOnly", "Update", "Lookup", "Delete" });
	}
	
	public void terminate() throws Exception {
		this.clientId = null;
		this.clientSecret = null;
		this.token = null;
		this.url = null;
		super.terminate();
	}
	
	public void initialize(Object o) throws ConnectorException {
		loginfo("Initialize Voll Connector");
		
		this.url = getParam("url");
		this.clientId = getParam("clientId");
		this.clientSecret = getParam("clientSecret");
		this.token = getToken(url, clientId, clientSecret);
	}
	
	@Override
	public String getVersion() {
		loginfo("Licensed Materials - Property of Asyg");

		if (VERSION_INFO == null)
			try {
				String str = getClass().getName().replace(".", "/") + ".class";
				URL uRL = getClass().getClassLoader().getResource(str);
				String[] arrayOfString = uRL.toExternalForm().split("!");
				uRL = new URL(arrayOfString[0].replaceFirst("jar:", ""));
				JarInputStream jarInputStream = new JarInputStream(uRL.openStream(), false);
				Attributes attributes = jarInputStream.getManifest().getMainAttributes();
				VERSION_INFO = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
			}
			catch (Exception e) {
				logerror("Fail to determine the version information: " + ExceptionUtils.getStackTrace(e));
			}
		return VERSION_INFO;
	}
	
	public void selectEntries() throws ConnectorException { }
	
	public synchronized Entry getNextEntry() throws ConnectorException {
		Entry entry = null;
		return entry;
	}
	
	public void querySchema() throws Exception {
		loginfo("querySchema");
	}
	
	@SuppressWarnings("unused")
	public Entry findEntry(SearchCriteria paramSearchCriteria) throws ConnectorException {
		Entry entry = new Entry();
		entry.setAttribute("eruid", paramSearchCriteria.getFirstCriteriaValue());
		String uid = paramSearchCriteria.getFirstCriteriaValue();
		
		return entry;
	}
	
	@SuppressWarnings("resource")
	public void deleteEntry(Entry entry, SearchCriteria search) throws ConnectorException {

		try {
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Authorization", "Bearer " + token);
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"query\":\"mutation {removeUser(input: {id: \"" + entry.getString("eruid") + "\"})}\"}");
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			logmsg(s.hasNext() ? s.next() : "");
		}
		catch (Exception e) {
			logerror("Exception deleteEntry: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
	}
	
	@SuppressWarnings("resource")
	public void putEntry(Entry entry) throws ConnectorException {

		try {
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Authorization", "Bearer " + token);
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"query\":\"mutation {createUser(input: {id: \"" + entry.getString("eruid") 
					+ "\",groupId:\"" + entry.getString("ergroupid")
					+ "\",name: \"" + entry.getString("ername")
					+ "\",preferredName: \"" + entry.getString("erpreferredname")
					+ "\",email: \"" + entry.getString("eremail")
					+ "\",profile:" + entry.getString("erprofile")
					+ ",tags:[\"" + entry.getString("ertags") + "\"]"
					+ ",workspaceId: \"" + entry.getString("erworspaceId")
					+ "\",phone: \"" + entry.getString("erphone")
					+ "\"})}\"}");
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			logmsg(s.hasNext() ? s.next() : "");
		}
		catch (Exception e) {
			logerror("Exception putEntry: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
	}
	
	@SuppressWarnings("resource")
	public void modEntry(Entry entry, SearchCriteria search, Entry old) throws ConnectorException {
		logdebug("modEntry " + entry);
		
		try {
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Authorization", "Bearer " + token);
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"query\":\"mutation {createUser(input: {id: \"" + entry.getString("eruid") + "\"");
			
			if (!isNullOrEmpty(entry.getString("ergroupid")))
				writer.append(",groupId:\"" + entry.getString("ergroupid") + "\"");

			if (!isNullOrEmpty(entry.getString("ername")))
				writer.append(",name: \"" + entry.getString("ername") + "\"");

			if (!isNullOrEmpty(entry.getString("erpreferredname")))
				writer.append(",preferredName: \"" + entry.getString("erpreferredname") + "\"");

			if (!isNullOrEmpty(entry.getString("eremail")))
				writer.append(",email: \"" + entry.getString("eremail") + "\"");

			if (!isNullOrEmpty(entry.getString("erprofile")))
				writer.append(",profile:" + entry.getString("erprofile") + "\"");

			if (!isNullOrEmpty(entry.getString("ertags")))
				writer.append(",tags:[\"" + entry.getString("ertags") + "\"]");

			if (!isNullOrEmpty(entry.getString("erworspaceId")))
				writer.append(",workspaceId: \"" + entry.getString("erworspaceId") + "\"");
			
			if (!isNullOrEmpty(entry.getString("erphone")))
				writer.append(",phone: \"" + entry.getString("erphone") + "\"");

			writer.append("})}\"}");
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			logmsg(s.hasNext() ? s.next() : "");
		}
		catch (Exception e) {
			logerror("Exception modifyUser: " + ExceptionUtils.getStackTrace(e));
		}
	}
	
	@SuppressWarnings("resource")
	private static String getToken(String url, String client_id, String client_secret) throws ConnectorException {
		String ret = "";
		
		try {
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"query\":\"mutation {createTokenPublic(input: {clientId: \"" + client_id + "\",clientSecret: \"" + client_secret + "\"}) {accessToken}}\"}");
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			ret = s.hasNext() ? s.next() : "";
		}
		catch (Exception e) {
			throw new ConnectorException(ExceptionUtils.getStackTrace(e));
	    }
		
		return ret;
	}
	
	private static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}
	
	public Entry queryReply(Entry entry) {
		return null;
	}
	
	private void loginfo(String paramString) {
		this.myLog.info(paramString);
	}
	
	private void logerror(String paramString) {
		this.myLog.error(paramString);
	}
	
	private void logdebug(String paramString) {
		this.myLog.debug(paramString);
	}
}
