package br.com.asyg.connector.bmg.kibana;

import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import br.com.asyg.connector.bmg.kibana.model.Roles;

public class KibanaConnector extends Connector implements ConnectorInterface {
	private static final String COPYRIGHT = "Licensed Materials - Property of Asyg";
	private static final String myName = "Kibana Connector";
	private static String VERSION_INFO = null;
	private List<Object> userList = new ArrayList<>();
	private List<Object> roleList = new ArrayList<>();
	private int current;
	private String token, url, type, operation, jdbcurl, jdbcpassword, jdbcuser, uid;
	
	public KibanaConnector() {
		setName(myName);
		setModes(new String[] { "Iterator", "AddOnly", "Update", "Lookup", "Delete" });
	}
	
	public void terminate() throws Exception {
		this.token = null;
		this.url = null;
		this.type = null;
		this.operation = null;
		this.jdbcurl = null;
		this.jdbcpassword = null;
		this.jdbcuser = null;
		super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		logmsg("Initialize Kibana Connector");
		this.url = getParam("url");
		this.token = getParam("token");
		this.type = getParam("type");
		this.operation = getParam("operation");
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
	
	public synchronized void selectEntries() throws Exception {
		logmsg("selectEntries");
		String context = "";
		int ret = 200;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		this.current = 0;
		
		try {
			conn = Util.getConnection("com.mysql.jdbc.Driver", jdbcurl, jdbcuser, jdbcpassword);

			switch (type) {
			case "User":
				//context = "/_security/user";
				//ret = sendGet(context, "");
				logmsg("selectEntries User");
				
				if (!type.equals("User"))
		            break;
				
				stmt = conn.prepareStatement("select login,nome,matricula from usr");
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					Account usr = new Account();
					usr.setFullName(rs.getString("nome"));
					usr.setEmail(rs.getString("matricula"));
					usr.setUsername(rs.getString("login"));
					userList.add(usr);
				}
				
				rs.close();
				return;
			case "Group":
				//context = "/_security/role";
				//ret = sendGet(context, "");
				logmsg("selectEntries Group");
				
				if (!type.equals("Group"))
		            break;
				
				stmt = conn.prepareStatement("select code,decode from grp");
				rs = stmt.executeQuery();
				
				while (rs.next()) {
					Roles rls = new Roles();
					rls.setRoleid(rs.getString("code"));
					rls.setRolename(rs.getString("decode"));
					roleList.add(rls);
				}
				
				rs.close();
				return;
			default:
				throw new Exception("Invalid entry type");
			}
			
			if (ret < 200 || ret > 299) {
				throw new Exception("Exception Send(User) ");
			}
		}
		catch (Exception e) {
			logmsg("Exception selectEntries: " + e);
			throw new Exception("Exception selectEntries: " + e.getMessage());
	    }
		finally {
			try {
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				logmsg("Exception " + e);
				throw new Exception("Exception selectEntries: " + e.getMessage());
			}
		}
	}
	
	public synchronized Entry getNextEntry() throws Exception {
		logmsg("getNextEntry " + type);
		Entry entry = null;
		
		try {
			switch (type) {
			case "User":
				if (!type.equals("User"))
		            break;
				
				if (userList != null && current < userList.size()) {
					Account var1 = (Account)userList.get(current);
					entry = retUser(var1);
			        current++;
				}
				
				return entry;
			case "Group":
				if (!type.equals("Group"))
		            break;
				
				if (roleList != null && current < roleList.size()) {
					Roles var1 = (Roles)roleList.get(current);
					entry = retRole(var1);
			        current++;
				}
				
				return entry;
			}
			
		    throw new Exception("Invalid entry type: " + new String(getParam("entrytype")));
		}
		catch (Exception e) {
			logmsg("Exception getNextEntry: " + e);
			throw new Exception("Exception getNextEntry: " + e.getMessage());
	    }
	}
	
	private Entry retUser(Account var2) throws Exception {
	    Entry var1 = new Entry();
	    var1.setAttribute("erusername", var2.getUsername());
	    var1.setAttribute("erfullname", var2.getFullName());
	    var1.setAttribute("eremail", var2.getEmail());
	    return var1;
	}
	
	private Entry retRole(Roles var3) throws Exception {
	    Entry var4 = new Entry();
	    var4.setAttribute("erroleid", var3.getRoleid());
	    var4.setAttribute("errolename", var3.getRolename());
	    return var4;
	}
	
	public void querySchema() throws Exception {
		logmsg("querySchema");
	}

	public Entry findEntry(SearchCriteria paramSearchCriteria) throws Exception {
		logmsg("findEntry " + paramSearchCriteria.toString());
		Entry entry = null;
		
		if (operation.equals("Delete") || operation.equals("Search")) {
			entry = new Entry();
			entry.setAttribute("eruid", paramSearchCriteria.getFirstCriteriaValue());
		}
		
		uid = paramSearchCriteria.getFirstCriteriaValue();

		return entry;
	}
	
	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception {
		logmsg("deleteEntry");
		int ret = 501;
		
		try {
			String context = "/_security/user/" + entry.getString("eruid");
			ret = sendDelete(context, "");
			
			if (ret < 200 || ret > 299) {
				//throw new Exception("Exception Send(User) ");
			}
		}
		catch (Exception e) {
			logmsg("Exception deleteEntry: " + e);
			throw new Exception("Exception deleteEntry: " + e.getMessage());
	    }
	}

	public void putEntry(Entry entry) throws Exception {
		logmsg("putEntry " + entry);
		String context = "";
		int ret = 501;
		
		try {
			switch (operation) {
			case "Add":
				ret = addAccount(entry);
				break;
			case "Suspend":
				context = "/_security/user/" + uid + "/_disable";
				ret = sendPut(context, "{}");
				break;
			case "Restore":
				context = "/_security/user/" + uid + "/_enable";
				ret = sendPut(context, "{}");
				break;
			case "Modify":
				ret = modifyUser(entry);
				break;
			case "ChangePassword":
				context = "/_security/user/" + entry.getString("eruid") + "/_password";
				
				JSONObject j1 = new JSONObject();
				j1.put("password", entry.getString("erpassword"));
				
				ret = sendPut(context, j1.toString());
				break;
			default:
				throw new Exception("Invalid entry type");
			}
			
			if (ret < 200 || ret > 299) {
				//throw new Exception("Exception Send(User)");
			}
		}
		catch (Exception e) {
			logmsg("Exception putEntry: " + e);
			throw new Exception("Exception putEntry: " + e.getMessage());
	    }
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public int addAccount(Entry entry) {
		Connection conn = null;
		List rowValues = new ArrayList();
		JSONObject j1 = null;
		JSONArray j2 = null;
		String context = "";
		int ret = 501;
		
		try {
			// Get all roles from user
			conn = Util.getConnection("com.mysql.jdbc.Driver", jdbcurl, jdbcuser, jdbcpassword);
			PreparedStatement stmt = conn.prepareStatement("select cpf, matricula from usr");
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
			    rowValues.add(rs.getString(1));
			}
			
			rs.close();
			String username = entry.getString("erusername");
			
			if (type.equals("User")) {
				String password = entry.getString("erpassword");
				String fullname = entry.getString("erfullname");
				String email = entry.getString("eremail");
				
				j1 = new JSONObject();
				j2 = new JSONArray(rowValues);
				
				j1.put("password", password);
				j1.put("full_name", fullname);
				j1.put("email", email);
				j1.put("roles", j2);
				
				context = "/_security/user/" + username;
				
				ret = sendPost(context, j1.toString());
			} else if (type.equals("Group")) {
				j1 = new JSONObject();
				j2 = new JSONArray(rowValues);
				j1.put("roles", j2);
				
				context = "/_security/user/" + username + "?pretty";
				
				ret = sendPost(context, j1.toString());
			}
		}
		catch (Exception e) {
			logmsg("Exception addAccount: " + e);
	    }
		finally {
			try {
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				logmsg("Exception " + e);
			}
		}
		
		return ret;
	}
	
	public int modifyUser(Entry entry) throws Exception {
		logmsg("modifyUser " + entry);
		int ret = 501;
		
		try {		
			JSONObject j1 = new JSONObject();
			
			if (!isNullOrEmpty(entry.getString("erfullname"))) {
				j1.put("full_name", entry.getString("erfullname"));
			}
			
			if (!isNullOrEmpty(entry.getString("eremail"))) {
				j1.put("email", entry.getString("eremail"));
			}

			String context = "/_security/user/" + uid;
			ret = sendPut(context, j1.toString());

			if (ret < 200 || ret > 299) {
				//throw new Exception("Exception Send(User) ");
			}
		}
		catch (Exception e) {
			logmsg("Exception modifyUser: " + e);
		}
		
		return ret;
	}

	public Entry queryReply(Entry entry) {
		logmsg("queryReply " + entry);
		return null;
	}
	
	private int sendPost(String context, String content) throws Exception {
		int responseCode = 501;
		
		try {
			String auth = "ApiKey " + token;
			logmsg("content: " + content);
			
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
			logmsg("Exception sendPost: " + e);
			throw new Exception("Exception sendPost: " + e.getMessage());
	    }
		
		return responseCode;
	}
	
	private int sendPut(String context, String content) throws Exception {
		int responseCode = 501;
		
		try {
			String auth = "ApiKey " + token;
			logmsg("content: " + content);
			
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
			logmsg("Exception sendPut: " + e);
			throw new Exception("Exception sendPut: " + e.getMessage());
	    }
		
		return responseCode;
	}
	
	private int sendDelete(String context, String content) throws Exception {
		int responseCode = 501;
		
		try {
			String auth = "ApiKey " + token;
			logmsg("content: " + content);
			
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
			logmsg("Exception sendDelete: " + e);
			throw new Exception("Exception sendDelete: " + e.getMessage());
	    }
		
		return responseCode;
	}
	
	private int sendGet(String context, String content) throws Exception {
		int responseCode = 501;
		
		try {
			String auth = "ApiKey " + token;
			logmsg("content: " + content);
			
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
			logmsg("Exception sendGet: " + e);
			throw new Exception("Exception sendGet: " + e.getMessage());
	    }
		
		return responseCode;
	}
	
	private static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
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
