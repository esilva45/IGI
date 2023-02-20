package br.com.asyg.connector.bmg.kibana;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

import br.com.asyg.connector.bmg.kibana.model.Account;

public class KibanaConnector extends Connector implements ConnectorInterface {
	private static final String COPYRIGHT = "Licensed Materials - Property of Asyg";
	private static final String myName = "Kibana Connector";
	private static String VERSION_INFO = null;
	private List<Account> accounts = new ArrayList<Account>();
	private int current;
	private String token, url, type, jdbcurl, jdbcpassword, jdbcuser;
	
	public KibanaConnector() {
		setName(myName);
		setModes(new String[] { "Iterator", "AddOnly", "Update", "Lookup", "Delete" });
	}
	
	public void terminate() throws Exception {
		this.token = null;
		this.url = null;
		this.type = null;
		this.jdbcurl = null;
		this.jdbcpassword = null;
		this.jdbcuser = null;
		super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		logmsg("Initialize KibanaConnector");
		this.url = getParam("url");
		this.token = getParam("token");
		this.type = getParam("type");
		this.jdbcurl = getParam("jdbcurl");
		this.jdbcpassword = getParam("jdbcpassword");
		this.jdbcuser = getParam("jdbcuser");
	}
	
	@Override
	public String getVersion() {
		logmsg(COPYRIGHT);

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
				logmsg("Fail to determine the version information: " + e.toString());
			}
		return VERSION_INFO;
	}
	
	public void selectEntries() throws Exception {
		logmsg("selectEntries");
		int ret = 501;
		
		try {
			String context = "/_security/user/";
			ret = sendGet(context, "");
			
			if (ret < 200 || ret > 299) {
				throw new Exception("Exception Send(User) ");
			}
		}
		catch (Exception e) {
			logmsg("Exception putEntry: " + e);
			throw new Exception("Exception putEntry: " + e.getMessage());
	    }
	}
	
	public Entry getNextEntry() throws Exception {
		logmsg("getNextEntry");
		Entry entry = null;
		
		try {
			if (accounts != null && current < accounts.size()) {
				Account var2 = (Account)accounts.get(current);
				entry = retUser(var2);
		        current++;
			}
		}
		catch (Exception e) {
			logmsg("Exception getNextEntry: " + e);
			throw new Exception("Exception getNextEntry: " + e.getMessage());
	    }
		
		return entry;
	}
	
	private Entry retUser(Account var2) throws Exception {
	    Entry var1 = new Entry();
	    var1.setAttribute("username", var2.getUsername());
	    var1.setAttribute("full_name", var2.getFull_name());
	    var1.setAttribute("email", var2.getEmail());
	    return var1;
	}
	
	public void querySchema() throws Exception { 
		
	}

	public void modifyUser(Entry entry) throws Exception {
		logmsg("modifyUser");
		int ret = 501;
		String context = "";
		
		try {
			String username = entry.getString("username");
			String password =  entry.getString("password");
			String roles =  entry.getString("roles");
			String full_name = entry.getString("full_name");
			String email =  entry.getString("email");
			String eraccountstatus =  entry.getString("eraccountstatus");
			
			JSONObject j1 = new JSONObject();
			
			ArrayList<String> list = new ArrayList<String>();
			list.add(roles);
			JSONArray j2 = new JSONArray(list);
			
			j1.put("password", password);
			j1.put("full_name", full_name);
			j1.put("email", email);
			j1.put("roles", j2);
			
			if (ret == 1) {
				context = "/_security/user/" + entry.getString("username");
				ret = sendPut(context, j1.toString());
			} else if (eraccountstatus.equals("active")) {
				context = "/_security/user/" + username + "/_enable";
				ret = sendPut(context, null);
			} else if (eraccountstatus.equals("inactive")) {
				context = "/_security/user/" + username + "/_disable";
				ret = sendPut(context, null);
			}

			if (ret < 200 || ret > 299) {
				throw new Exception("Exception Send(User) ");
			}
		}
		catch (Exception e) {
			logmsg("Exception putEntry: " + e);
			throw new Exception("Exception putEntry: " + e.getMessage());
		}
	}

	public Entry findEntry(SearchCriteria paramSearchCriteria) throws Exception {
		logmsg("findEntry");
		return null;
	}
	
	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception {
		logmsg("deleteEntry");
		int ret = 501;
		
		try {
			String context = "/_security/user/" + entry.getString("username");
			ret = sendDelete(context, "");
			
			if (ret < 200 || ret > 299) {
				throw new Exception("Exception Send(User) ");
			}
		}
		catch (Exception e) {
			logmsg("Exception putEntry: " + e);
			throw new Exception("Exception putEntry: " + e.getMessage());
	    }
	}

	public void putEntry(Entry entry) throws Exception {
		logmsg("putEntry");
		int ret = 501;
		
		try {
			String username = entry.getString("username");
			String password =  entry.getString("password");
			String roles =  entry.getString("roles");
			String full_name = entry.getString("full_name");
			String email =  entry.getString("email");
			
			JSONObject j1 = new JSONObject();
			
			ArrayList<String> list = new ArrayList<String>();
			list.add(roles);
			JSONArray j2 = new JSONArray(list);
			
			j1.put("password", password);
			j1.put("full_name", full_name);
			j1.put("email", email);
			j1.put("roles", j2);
			
			String context = "/_security/user/" + username;
			
			ret = sendPost(context, j1.toString());
			
			if (ret < 200 || ret > 299) {
				throw new Exception("Exception Send(User)");
			}
		}
		catch (Exception e) {
			logmsg("Exception putEntry: " + e);
			throw new Exception("Exception putEntry: " + e.getMessage());
	    }
	}

	public Entry queryReply(Entry entry) {
		logmsg("queryReply");
		return null;
	}
	
	private int sendPost(String context, String content) throws Exception {
		int responseCode = 501;
		
		try {
			String auth = "ApiKey " + token;

			HttpPost var = new HttpPost(url.toString() + context);
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
			var.setEntity(new StringEntity(content));
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
				responseCode = response.getStatusLine().getStatusCode();

				logmsg("URL: " + var.getURI());
				logmsg("Method: " + var.getMethod());
				logmsg("Status line: " + response.getStatusLine().toString());

				if (response.getEntity() != null) {

				}
	        }
		}
		catch (Exception e) {
			logmsg("Exception send: " + e);
			throw new Exception("Exception send: " + e.getMessage());
	    }
		return responseCode;
	}
	
	private int sendPut(String context, String content) throws Exception {
		int responseCode = 501;
		
		try {
			String auth = "ApiKey " + token;
			
			HttpPut var = new HttpPut(url.toString() + context);
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
			var.setEntity(new StringEntity(content));
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
				responseCode = response.getStatusLine().getStatusCode();

				logmsg("URL: " + var.getURI());
				logmsg("Method: " + var.getMethod());
				logmsg("Status line: " + response.getStatusLine().toString());
	        	
				if (response.getEntity() != null) {
					
				}
	        }
		}
		catch (Exception e) {
			logmsg("Exception send: " + e);
			throw new Exception("Exception send: " + e.getMessage());
	    }
		return responseCode;
	}
	
	private int sendDelete(String context, String content) throws Exception {
		int responseCode = 501;
		
		try {
			String auth = "ApiKey " + token;

			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpDeleteWithBody var = new HttpDeleteWithBody(url.toString() + context);
			
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
			var.setEntity(new StringEntity(content));
	        
	        CloseableHttpResponse response = httpclient.execute(var);
	        responseCode = response.getStatusLine().getStatusCode();
	        
	        logmsg("URL: " + var.getURI());
	        logmsg("Method: " + var.getMethod());
	        logmsg("Status line: " + response.getStatusLine().toString());
        	
			if (response.getEntity() != null) {

			}
		}
		catch (Exception e) {
			logmsg("Exception send: " + e);
			throw new Exception("Exception send: " + e.getMessage());
	    }
		return responseCode;
	}
	
	private int sendGet(String context, String content) throws Exception {
		int responseCode = 501;
		
		try {
			String auth = "ApiKey " + token;
			
			HttpGet var = new HttpGet(url.toString() + context);
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
				responseCode = response.getStatusLine().getStatusCode();

				logmsg("URL: " + var.getURI());
				logmsg("Method: " + var.getMethod());
				logmsg("Status line: " + response.getStatusLine().toString());
	        	
				if (response.getEntity() != null) {
					
				}
			}
		}
		catch (Exception e) {
			logmsg("Exception send: " + e);
			throw new Exception("Exception send: " + e.getMessage());
	    }
		return responseCode;
	}
	
	class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {
	    public static final String METHOD_NAME = "DELETE";

	    public String getMethod() {
	        return METHOD_NAME;
	    }

	    public HttpDeleteWithBody(final String uri) {
	        super();
	        setURI(URI.create(uri));
	    }

	    public HttpDeleteWithBody(final URI uri) {
	        super();
	        setURI(uri);
	    }

	    public HttpDeleteWithBody() {
	        super();
	    }
	}
}
