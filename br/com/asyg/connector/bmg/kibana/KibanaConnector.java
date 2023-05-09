package br.com.asyg.connector.bmg.kibana;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
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
import br.com.asyg.connector.exceptions.ConnectorException;

public class KibanaConnector extends Connector implements ConnectorInterface {
	private static String VERSION_INFO = null;
	private List<Object> userList = new ArrayList<>();
	private List<Object> roleList = new ArrayList<>();
	private int current;
	private String token, url, type, operation, uid, primary_group;
	
	public KibanaConnector() {
		setName("Kibana Connector");
		setModes(new String[] { "Iterator", "AddOnly", "Update", "Lookup", "Delete" });
	}
	
	public void terminate() throws Exception {
		this.token = null;
		this.url = null;
		super.terminate();
	}
	
	public void initialize(Object o) throws ConnectorException {
		loginfo("Initialize Kibana Connector");
		
		this.url = getParam("url");
		this.token = getParam("token");
		this.type = getParam("type");
		this.operation = getParam("operation");
		this.primary_group = getParam("primary_group");
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
	
	public void selectEntries() throws ConnectorException {
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
				
				if (jsonObject != null) {
					for (String key: jsonObject.keySet()) {
						Account usr = new Account();
						JSONObject d = jsonObject.getJSONObject(key);
						
						usr.setFullName(d.get("full_name").toString());
						usr.setEmail(d.get("email").toString());
						usr.setUsername(d.get("username").toString());
						usr.setStatus(d.getBoolean("enabled") ? 0 : 1);
			            usr.setGroupid(new JSONArray(d.get("roles").toString()));
				        userList.add(usr);
					}
				}
				
				return;
			case "Group":
				context = "/_security/role";
				jsonObject = sendGet(context, "");
				
				if (!type.equals("Group"))
		            break;
				
				if (jsonObject != null) {
					for (String key: jsonObject.keySet()) {
						Roles rls = new Roles();
						rls.setRoleid(key);
						rls.setRolename(key);
						roleList.add(rls);
					}
				}
				
				return;
			default:
				logerror("Invalid entry type");
				throw new ConnectorException("Invalid entry type");
			}
			
			if (ret < 200 || ret > 299) {
				logerror("return " + ret);
				throw new ConnectorException("return " + ret);
			}
		}
		catch (Exception e) {
			logerror("Exception selectEntries: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
	}
	
	public synchronized Entry getNextEntry() throws ConnectorException {
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
			
			logerror(new String(getParam("entrytype")));
		    throw new ConnectorException(new String(getParam("entrytype")));
		}
		catch (Exception e) {
			logerror("Exception getNextEntry: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
	}
	
	private Entry retUser(Account var2) throws ConnectorException {
	    Entry var1 = new Entry();
	    var1.setAttribute("eruid", var2.getUsername());
	    var1.setAttribute("erfullname", var2.getFullName());
	    var1.setAttribute("eremail", var2.getEmail());
	    var1.setAttribute("eraccountstatus", var2.getStatus());
	    
	    JSONArray jsArray = new JSONArray(var2.getGroupid());
		
        for (int i = 0; i < jsArray.length(); i++) {
        	var1.addAttributeValue("ergroupid", jsArray.getString(i));
        }
	    
	    return var1;
	}
	
	private Entry retRole(Roles var2) throws ConnectorException {
	    Entry var1 = new Entry();
	    var1.setAttribute("erroleid", var2.getRoleid());
	    var1.setAttribute("errolename", var2.getRolename());
	    return var1;
	}
	
	public void querySchema() throws Exception {
		loginfo("querySchema");
	}

	public Entry findEntry(SearchCriteria paramSearchCriteria) throws ConnectorException {
		Entry entry = null;
		
		if (type.equals("Test")) {
			int ret = testGet("/_cluster/health");
			
			if (ret < 200 || ret > 299) {
				logerror("return " + ret);
				throw new ConnectorException(String.valueOf(ret));
			}
			entry = new Entry();
		} else {
			entry = new Entry();
			entry.setAttribute("eruid", paramSearchCriteria.getFirstCriteriaValue());
			uid = paramSearchCriteria.getFirstCriteriaValue();
		}
		
		return entry;
	}
	
	public void deleteEntry(Entry entry, SearchCriteria search) throws ConnectorException {
		int ret = 501;
		
		try {
			String context = "/_security/user/" + entry.getString("eruid");
			ret = sendDelete(context, "");
			
			if (ret < 200 || ret > 299) {
				logerror("return " + ret);
				throw new ConnectorException(String.valueOf(ret));
			}
		}
		catch (Exception e) {
			logerror("Exception deleteEntry: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
	}

	public void putEntry(Entry entry) throws ConnectorException {
		int ret = 501;
		
		try {
			switch (operation) {
			case "Add":
				ret = addAccount(entry);
				break;
			default:
				logerror("Invalid entry type");
				throw new ConnectorException("Invalid entry type");
			}
			
			if (ret < 200 || ret > 299) {
				throw new ConnectorException(String.valueOf(ret));
			}
		}
		catch (Exception e) {
			logerror("Exception putEntry: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
	}
	
	public void modEntry(Entry entry, SearchCriteria search, Entry old) throws ConnectorException {
		logdebug("modEntry " + entry);
		int ret = 501;
		JSONArray j2 = null;
		JSONObject j1 = null, d = null;
		String context = "";
		int idx = -1;
		
		try {
			logmsg("erfullname " + entry.getAttribute("erfullname"));
			logmsg("eremail " + entry.getAttribute("eremail"));
			logmsg("op " + operation);
			logmsg("uid " + uid);
			
			switch (operation) {
				case "Suspend":
					context = "/_security/user/" + uid + "/_disable";
					ret = sendPut(context, null);
					break;
				case "Restore":
					context = "/_security/user/" + uid + "/_enable";
					ret = sendPut(context, null);
					break;
				case "ChangePassword":
					context = "/_security/user/" + uid + "/_password";
					
					j1 = new JSONObject();
					j1.put("password", entry.getString("erpassword"));
					
					ret = sendPut(context, j1.toString());
					break;
				case "Modify":
					context = "/_security/user/" + uid;
					j1 = new JSONObject();
					d = getUser(context);
					
					if (!isNullOrEmpty(entry.getString("ergroupid"))) {
						logmsg("Operation: " + entry.getString("grpoperation"));
						
						j2 = new JSONArray(d.get("roles").toString());
						
						if (entry.getString("grpoperation").equals("delete")) {
					        for (int i = 0; i < j2.length(); i++) {
						    	if (entry.getString("ergroupid").equals(j2.getString(i))) {
									idx = i;
						    	}
						    }
					        
					    	j2.remove(idx);
					    } else if (entry.getString("grpoperation").equals("add")) {
					    	j2.put(entry.getString("ergroupid"));
					    }
						
						j1.put("roles", j2);
						
					} else {
						j1.put("roles", new JSONArray(d.get("roles").toString()));
					}
					
					if (!isNullOrEmpty(entry.getString("erfullname"))) {
						j1.put("full_name", entry.getString("erfullname"));
					} else {
						j1.put("full_name", d.get("full_name").toString());
					}
					
					if (!isNullOrEmpty(entry.getString("eremail"))) {
						j1.put("email", entry.getString("eremail"));
					} else {
						j1.put("email", d.get("email").toString());
					}
					
					ret = sendPut(context, j1.toString());
					break;
				default:
					logerror("Invalid entry type");
					throw new ConnectorException("Invalid entry type");
			}

			if (ret < 200 || ret > 299) {
				throw new ConnectorException(String.valueOf(ret));
			}
		}
		catch (Exception e) {
			logerror("Exception modifyUser: " + ExceptionUtils.getStackTrace(e));
		}
	}

	public Entry queryReply(Entry entry) {
		return null;
	}
	
	public int addAccount(Entry entry) {
		JSONObject j1 = new JSONObject();
		JSONArray j2 = new JSONArray();
		int ret = 501;
		
		try {
			String username = entry.getString("eruid");
			
			if (type.equals("User")) {
				j1.put("password", entry.getString("erpassword"));
				j1.put("full_name", entry.getString("erfullname"));
				j1.put("email", entry.getString("eremail"));
				
				j2.put(primary_group);
				j1.put("roles", j2);
				
				String context = "/_security/user/" + username;
				
				ret = sendPost(context, j1.toString());
			}
		}
		catch (Exception e) {
			logerror("Exception addAccount: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return ret;
	}
	
	private JSONObject getUser(String context) throws ConnectorException {
		JSONObject j1 = null;
		
		try {
			String auth = "ApiKey " + token;

			HttpGet var = new HttpGet(url.toString() + context);
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {

	        	logdebug("URL: " + var.getURI());
	        	logdebug("Method: " + var.getMethod());
	        	logdebug("Status line: " + response.getStatusLine().toString());
	        	
				if (response.getEntity() != null) {
					JSONObject jsonObject = new JSONObject(EntityUtils.toString(response.getEntity()));
					j1 = jsonObject.getJSONObject(uid);
				}
			}
		}
		catch (Exception e) {
			logerror("Exception getGroups: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
		
		return j1;
	}
	
	
	private int sendPost(String context, String content) throws ConnectorException {
		int responseCode = 200;
		String jsonResult = null;
		
		try {
			String auth = "ApiKey " + token;
			
			JSONObject jsonContent = new JSONObject(content);
			if (jsonContent.has("password")) {
				jsonContent.put("password", "**********");
			}
			logmsg("content: " + jsonContent);
			
			HttpPost var = new HttpPost(url.toString() + context);
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
			var.setEntity(new StringEntity(content));
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
				responseCode = response.getStatusLine().getStatusCode();

				logdebug("URL: " + var.getURI());
				logdebug("Method: " + var.getMethod());
				logdebug("Status line: " + response.getStatusLine().toString());

				if (response.getEntity() != null) {
					jsonResult = EntityUtils.toString(response.getEntity());
					logdebug("Result: " + jsonResult);
				}
	        }
		}
		catch (Exception e) {
			logerror("Exception sendPost: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
		
		return responseCode;
	}
	
	private int sendPut(String context, String content) throws ConnectorException {
		int responseCode = 200;
		String jsonResult = null;
		JSONObject jsonContent = null;
		
		try {
			String auth = "ApiKey " + token;
			
			if (content != null) {
				jsonContent = new JSONObject(content);
				if (jsonContent.has("password")) {
					jsonContent.put("password", "**********");
				}
			}
			
			logmsg("content: " + jsonContent);
			
			HttpPut var = new HttpPut(url.toString() + context);
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
			
			if (content != null)
				var.setEntity(new StringEntity(content));
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
				responseCode = response.getStatusLine().getStatusCode();

				logmsg("URL: " + var.getURI());
				logmsg("Method: " + var.getMethod());
				logmsg("Status line: " + response.getStatusLine().toString());
	        	
				if (response.getEntity() != null) {
					jsonResult = EntityUtils.toString(response.getEntity());
					logdebug("Result: " + jsonResult);
				}
	        }
		}
		catch (Exception e) {
			logerror("Exception sendPut: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
		
		return responseCode;
	}
	
	private int sendDelete(String context, String content) throws ConnectorException {
		int responseCode = 200;
		String jsonResult = null;
		
		try {
			String auth = "ApiKey " + token;
			logdebug("content: " + content);
			
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpDeleteWithBody var = new HttpDeleteWithBody(url.toString() + context);
			
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
			var.setEntity(new StringEntity(content));
	        
	        CloseableHttpResponse response = httpclient.execute(var);
	        responseCode = response.getStatusLine().getStatusCode();

	        logdebug("URL: " + var.getURI());
	        logdebug("Method: " + var.getMethod());
	        logdebug("Status line: " + response.getStatusLine().toString());
        	
			if (response.getEntity() != null) {
				jsonResult = EntityUtils.toString(response.getEntity());
				logdebug("Result: " + jsonResult);
			}
		}
		catch (Exception e) {
			logerror("Exception sendDelete: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
		
		return responseCode;
	}
	
	private JSONObject sendGet(String context, String content) throws ConnectorException {
		JSONObject j1 = null;
		
		try {
			String auth = "ApiKey " + token;
			logdebug("content: " + content);
			
			HttpGet var = new HttpGet(url.toString() + context);
			var.addHeader("content-type", "application/json; charset=utf-8");
			var.addHeader("Authorization", auth);
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {

	        	logdebug("URL: " + var.getURI());
	        	logdebug("Method: " + var.getMethod());
	        	logdebug("Status line: " + response.getStatusLine().toString());
	        	logdebug("getEntity: " + response.getEntity());
				
				if (response.getEntity() != null) {
					String jsonResult = EntityUtils.toString(response.getEntity());
					j1 = new JSONObject(jsonResult);
				}
			}
		}
		catch (Exception e) {
			logerror("Exception sendGet: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
		
		return j1;
	}
	
	private int testGet(String context) throws ConnectorException {
		int responseCode = 200;
		
		try {
			String auth = "ApiKey " + token;
			
			HttpGet var = new HttpGet(url.toString() + context);
			var.addHeader("Authorization", auth);
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
	        	responseCode = response.getStatusLine().getStatusCode();
	        	
	        	logdebug("URL: " + var.getURI());
	        	logdebug("Method: " + var.getMethod());
	        	logdebug("Status line: " + response.getStatusLine().toString());
	        	logdebug("getEntity: " + response.getEntity());
			}
		}
		catch (Exception e) {
			logerror("Exception testGet: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
		
		return responseCode;
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
