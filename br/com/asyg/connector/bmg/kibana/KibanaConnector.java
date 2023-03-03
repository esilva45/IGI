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
import org.apache.http.util.EntityUtils;
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
	private String token, url, type, operation, uid;
	
	public KibanaConnector() {
		setName(myName);
		setModes(new String[] { "Iterator", "AddOnly", "Update", "Lookup", "Delete" });
	}
	
	public void terminate() throws Exception {
		this.token = null;
		this.url = null;
		super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		logmsg("Initialize Kibana Connector");
		
		this.url = getParam("url");
		this.token = getParam("token");
		this.type = getParam("type");
		this.operation = getParam("operation");
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
		String context = "";
		int ret = 501;
		this.current = 0;
		JSONObject jsonObject = null;
		
		try {
			switch (type) {
			case "User":
				context = "/_security/user";
				jsonObject = sendGet(context, uid);
				
				if (!type.equals("User"))
		            break;
				
				for (String key: jsonObject.keySet()) {
					Account usr = new Account();
					JSONObject d = jsonObject.getJSONObject(key);
					
					usr.setFullName(d.get("full_name").toString());
					usr.setEmail(d.get("username").toString());
					usr.setUsername(d.get("email").toString());
					usr.setStatus(d.getBoolean("enabled") ? 0 : 1);

		            JSONArray jsArray = new JSONArray(d.get("roles").toString());
					String[] stringArray = new String[jsArray.length()];
					
			        for (int i = 0; i < jsArray.length(); i++) {
			            stringArray[i] = jsArray.getString(i);
			        }
			        
			        usr.setGroupId(stringArray);
			        userList.add(usr);
				}
				
				return;
			case "Group":
				context = "/_security/role";
				jsonObject = sendGet(context, "");
				
				if (!type.equals("Group"))
		            break;
				
				for (String key: jsonObject.keySet()) {
					Roles rls = new Roles();
					rls.setRoleid(key);
					rls.setRolename(key);
					roleList.add(rls);
				}
				
				return;
			default:
				throw new Exception("Invalid entry type");
			}
			
			if (ret < 200 || ret > 299) {
				throw new Exception("return " + ret);
			}
		}
		catch (Exception e) {
			logmsg("Exception selectEntries: " + e);
			throw new Exception(e.toString());
	    }
	}
	
	public synchronized Entry getNextEntry() throws Exception {
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
			
		    throw new Exception(new String(getParam("entrytype")));
		}
		catch (Exception e) {
			logmsg("Exception getNextEntry: " + e);
			throw new Exception(e.toString());
	    }
	}
	
	private Entry retUser(Account var2) throws Exception {
	    Entry var1 = new Entry();
	    var1.setAttribute("erusername", var2.getUsername());
	    var1.setAttribute("erfullname", var2.getFullName());
	    var1.setAttribute("eremail", var2.getEmail());
	    var1.setAttribute("eraccountstatus", var2.getStatus());
	    var1.setAttribute("ergroupid", var2.getGroupId());
	    return var1;
	}
	
	private Entry retRole(Roles var2) throws Exception {
	    Entry var1 = new Entry();
	    var1.setAttribute("erroleid", var2.getRoleid());
	    var1.setAttribute("errolename", var2.getRolename());
	    return var1;
	}
	
	public void querySchema() throws Exception {
		logmsg("querySchema");
	}

	public Entry findEntry(SearchCriteria paramSearchCriteria) throws Exception {
		Entry entry = null;

		if (operation.equals("Delete") || operation.equals("Search")) {
			entry = new Entry();
			entry.setAttribute("eruid", paramSearchCriteria.getFirstCriteriaValue());
		}
		
		uid = paramSearchCriteria.getFirstCriteriaValue();

		return entry;
	}
	
	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception {
		int ret = 501;
		
		try {
			String context = "/_security/user/" + entry.getString("eruid");
			ret = sendDelete(context, "");
			
			if (ret < 200 || ret > 299) {
				throw new Exception("return " + ret);
			}
		}
		catch (Exception e) {
			logmsg("Exception deleteEntry: " + e);
			throw new Exception(e.toString());
	    }
	}

	public void putEntry(Entry entry) throws Exception {
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
				throw new Exception("return " + ret);
			}
		}
		catch (Exception e) {
			logmsg("Exception putEntry: " + e);
			throw new Exception(e.toString());
	    }
	}

	public int addAccount(Entry entry) {
		JSONObject j1 = new JSONObject();
		JSONArray j2 = new JSONArray();
		int ret = 501;
		
		try {
			String username = entry.getString("erusername");
			
			if (type.equals("User")) {
				j1.put("password", entry.getString("erpassword"));
				j1.put("full_name", entry.getString("erfullname"));
				j1.put("email", entry.getString("eremail"));
				
				j2.put(entry.getString("erprimarygroup"));
				j1.put("roles", j2);
				
				String context = "/_security/user/" + username;
				
				ret = sendPost(context, j1.toString());
			}
		}
		catch (Exception e) {
			logmsg("Exception addAccount: " + e);
	    }
		
		return ret;
	}
	
	public int modifyUser(Entry entry) throws Exception {
		logmsg("modifyUser " + entry);
		int ret = 501;
		
		try {
			JSONObject j1 = new JSONObject();
			
			if (!isNullOrEmpty(entry.getString("ergroupid"))) {
				String context = "/_security/user/" + uid;
				j1.put("roles", getGroups(context, entry.getString("ergroupid")));
			}
			
			if (!isNullOrEmpty(entry.getString("erfullname"))) {
				j1.put("full_name", entry.getString("erfullname"));
			}
			
			if (!isNullOrEmpty(entry.getString("eremail"))) {
				j1.put("email", entry.getString("eremail"));
			}

			String context = "/_security/user/" + uid;
			ret = sendPut(context, j1.toString());

			if (ret < 200 || ret > 299) {
				throw new Exception("return " + ret);
			}
		}
		catch (Exception e) {
			logmsg("Exception modifyUser: " + e);
		}
		
		return ret;
	}

	public Entry queryReply(Entry entry) {
		return null;
	}
	
	private JSONArray getGroups(String context, String role) throws Exception {
		JSONArray j2 = null;
		
		try {
			String auth = "ApiKey " + token;
			int idx = -1;
			
			HttpGet var = new HttpGet(url.toString() + context);
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {

				logmsg("URL: " + var.getURI());
				logmsg("Method: " + var.getMethod());
				logmsg("Status line: " + response.getStatusLine().toString());
	        	
				if (response.getEntity() != null) {
					JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
			        JSONObject d = jsonObject.getJSONObject(uid);
			        j2 = new JSONArray(d.get("roles").toString());
			        
			        for (int i = 0; i < j2.length(); i++) {
				    	if (role.equals(j2.getString(i))) {
							idx = i;
				    	}
				    }
			        
			        if (idx >= 0) {
				    	j2.remove(idx);
				    } else {
				    	j2.put(role);
				    }
				}
			}
		}
		catch (Exception e) {
			logmsg("Exception getGroups: " + e);
			throw new Exception(e.toString());
	    }
		
		return j2;
	}
	
	private int sendPost(String context, String content) throws Exception {
		int responseCode = 200;
		String jsonResult = null;
		
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
					jsonResult = EntityUtils.toString(response.getEntity());
					logmsg("Result: " + jsonResult);
				}
	        }
		}
		catch (Exception e) {
			logmsg("Exception sendPost: " + e);
			throw new Exception(e.toString());
	    }
		
		return responseCode;
	}
	
	private int sendPut(String context, String content) throws Exception {
		int responseCode = 200;
		String jsonResult = null;
		
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
					jsonResult = EntityUtils.toString(response.getEntity());
					logmsg("Result: " + jsonResult);
				}
	        }
		}
		catch (Exception e) {
			logmsg("Exception sendPut: " + e);
			throw new Exception(e.toString());
	    }
		
		return responseCode;
	}
	
	private int sendDelete(String context, String content) throws Exception {
		int responseCode = 200;
		String jsonResult = null;
		
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
				jsonResult = EntityUtils.toString(response.getEntity());
				logmsg("Result: " + jsonResult);
			}
		}
		catch (Exception e) {
			logmsg("Exception sendDelete: " + e);
			throw new Exception(e.toString());
	    }
		
		return responseCode;
	}
	
	private JSONObject sendGet(String context, String content) throws Exception {
		JSONObject j1 = null;
		
		try {
			String auth = "ApiKey " + token;
			logmsg("content: " + content);
			
			HttpGet var = new HttpGet(url.toString() + context);
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {

				logmsg("URL: " + var.getURI());
				logmsg("Method: " + var.getMethod());
				logmsg("Status line: " + response.getStatusLine().toString());
	        	
				if (response.getEntity() != null) {
					JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
			        j1 = jsonObject.getJSONObject(uid);
				}
			}
		}
		catch (Exception e) {
			logmsg("Exception sendGet: " + e);
			throw new Exception(e.toString());
	    }
		
		return j1;
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
