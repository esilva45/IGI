package br.com.asyg.connector.bmg.azure;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

import br.com.asyg.connector.bmg.azure.model.Account;
import br.com.asyg.connector.bmg.azure.model.Roles;

public class MFAConnector extends Connector implements ConnectorInterface {
	private static String VERSION_INFO = null;
	private List<Object> userList = new ArrayList<>();
	private List<Object> perList = new ArrayList<>();
	private String grantType, clientId, token, urlService, clientSecret, tenant, urlAuthentication, uid, operation, type;
	private int current;
	
	public MFAConnector() {
		setName("MFA Connector");
		setModes(new String[] { "Iterator", "AddOnly", "Update", "Lookup", "Delete" });
	}
	
	public void terminate() throws Exception {
		loginfo("Terminate MFA Connector");
		
		this.grantType = null;
		this.clientId = null;
		this.clientSecret = null;
		this.token = null;
		this.urlService = null;
		this.urlAuthentication = null;
		this.tenant = null;
		super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		loginfo("Initialize MFA Connector");
		
		this.type = getParam("type");
		this.urlService = getParam("urlService");
		this.urlAuthentication = getParam("urlAuthentication");
		this.clientSecret = getParam("clientSecret");
		this.grantType = getParam("grantType");
		this.clientId = getParam("clientId");
		this.tenant = getParam("tenant");
		this.operation = getParam("operation");
		this.token = getToken();
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
	
	public void selectEntries() throws Exception {
		loginfo("Entering selectEntries() method");
		JSONObject jsonResult = new JSONObject();
		JSONArray tmp = new JSONArray();
		this.current = 0;
		
		try {
			switch (type) {
				case "User":
				
					if (!type.equals("User"))
						break;
					
					String[] ids = sendGetId();
					
					for (int i = 0; i < ids.length; i++) {
						Account usr = new Account();
						jsonResult = sendGet("/" + ids[i] + "/authentication/methods");
						JSONArray jarr = jsonResult.getJSONArray("value");
						
						tmp = new JSONArray();
						
						usr.setMail(ids[i]);
						
						for (int j = 0; j < jarr.length(); j++) {
							JSONObject arr = jarr.getJSONObject(j);
							
							if (arr.getString("@odata.type").equalsIgnoreCase("#microsoft.graph.phoneAuthenticationMethod")) {
								usr.setPhoneNumber(arr.isNull("phoneNumber") ? "" : arr.get("phoneNumber").toString());
								usr.setSmsSignInState(arr.isNull("smsSignInState") ? "" : arr.getString("smsSignInState"));
							}
							
							if (!arr.getString("@odata.type").equalsIgnoreCase("#microsoft.graph.phoneAuthenticationMethod") 
									|| !arr.getString("@odata.type").equalsIgnoreCase("#microsoft.graph.passwordAuthenticationMethod")) {
								tmp.put(arr.getString("@odata.type"));
							}
							
							usr.setRoleid(tmp);
						}
						
						if (usr != null) {
							userList.add(usr);
						}
					}
					
					return;
				case "Methods":
					String[] values = {
							"#microsoft.graph.emailAuthenticationMethod",
							"#microsoft.graph.fido2AuthenticationMethod",
							"#microsoft.graph.microsoftAuthenticatorAuthenticationMethod",
							"#microsoft.graph.phoneAuthenticationMethod",
							"#microsoft.graph.softwareOathAuthenticationMethod",
							"#microsoft.graph.temporaryAccessPassAuthenticationMethod",
							"#microsoft.graph.windowsHelloForBusinessAuthenticationMethod"
					};
					
					if (!type.equals("Methods"))
			            break;
					
					for (int i = 0; i < values.length; i++) {
						Roles rls = new Roles();
						rls.setRoleid(values[i]);
						rls.setRolename(values[i]);
						
						perList.add(rls);
					}
					
					return;
				default:
					logerror("Invalid entry type: " + type);
					throw new Exception("Invalid entry type: " + type);
			}
		}
		catch (Exception e) {
			logerror("Exception selectEntries: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	public synchronized Entry getNextEntry() throws Exception {
		loginfo("Entering getNextEntry() method");
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
					
				case "Methods":
					if (!type.equals("Methods"))
			            break;
					
					if (perList != null && current < perList.size()) {
						Roles var1 = (Roles)perList.get(current);
						entry = retMethods(var1);
				        current++;
					}
					
					return entry;
					
				default:
					logerror("Invalid entry type: " + type);
					throw new Exception("Invalid entry type: " + type);
			}
			
			logerror(new String(getParam("entrytype")));
		    throw new Exception(new String(getParam("entrytype")));
		}
		catch (Exception e) {
			logerror("Exception getNextEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	private Entry retUser(Account var2) throws Exception {
		loginfo("Entering retUser() method");
	    Entry var1 = new Entry();
	    var1.setAttribute("eruid", var2.getMail());
	    var1.setAttribute("eremail", var2.getMail());
	    var1.setAttribute("erphonenumber", var2.getPhoneNumber());
	    var1.setAttribute("ersmsstate", var2.getSmsSignInState());
	    
	    JSONArray jsRoles = new JSONArray(var2.getRoleid());
		
        for (int i = 0; i < jsRoles.length(); i++) {
        	var1.addAttributeValue("erroleid", jsRoles.getString(i));
        }
        
	    return var1;
	}
	
	private Entry retMethods(Roles var2) throws Exception {
		loginfo("Entering retMethods() method");
	    Entry var1 = new Entry();
	    var1.setAttribute("erroleid", var2.getRoleid());
	    var1.setAttribute("errolename", var2.getRolename());
	    return var1;
	}
	
	public void querySchema() throws Exception {
		loginfo("querySchema");
	}
	
	public Entry findEntry(SearchCriteria paramSearchCriteria) throws Exception {
		loginfo("Entering findEntry() method");
		loginfo("paramSearchCriteria " + paramSearchCriteria.toString());
		Entry entry = new Entry();
		
		if (type.equals("Test")) {
			String ret = testService();
			
			if (ret.equals("site_down")) {
				logerror("return " + ret);
				throw new Exception(String.valueOf(ret));
			}
			
			entry = new Entry();
		} else {
			entry = new Entry();
			entry.setAttribute("eruid", paramSearchCriteria.getFirstCriteriaValue());
			uid = paramSearchCriteria.getFirstCriteriaValue();
		}
		
		return entry;
	}
	
	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception {
		loginfo("Entering deleteEntry() method");
		JSONObject j1 = new JSONObject();
		
		try {
			JSONObject jsonResult = sendGet("/" + entry.getString("eruid") + "/authentication/phoneMethods");

			if (!jsonResult.isEmpty()) {
				JSONArray jarr = jsonResult.getJSONArray("value");
				j1 = sendDelete("/" + entry.getString("eruid") + "/authentication/phoneMethods/" + jarr.getJSONObject(0).getString("id"));
				
				if (j1.has("error")) {
					throw new Exception(String.valueOf(j1.getString("error")));
				}
			}
		}
		catch (Exception e) {
			logerror("Exception deleteEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	public void putEntry(Entry entry) throws Exception {
		loginfo("Entering putEntry() method");
		JSONObject j1 = new JSONObject();
		String ret = "";
		
		try {
			j1.put("phoneType", "mobile");
			j1.put("phoneNumber", "+55" + entry.getString("erphoneNumber").replaceAll("\\D+",""));
			JSONObject jsonResult = sendPost("/" + entry.getString("eremail") + "/authentication/phoneMethods", j1.toString());
			
			if (jsonResult.has("error")) {
				throw new Exception(String.valueOf(ret));
			}
		}
		catch (Exception e) {
			logerror("Exception putEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	public void modEntry(Entry entry, SearchCriteria search, Entry old) throws Exception {
		loginfo("modEntry " + operation + ", entry " + entry + ", old " + old);
		JSONObject j1 = new JSONObject(), jsonResult = new JSONObject();
		
		try {
			switch (operation) {
				case "Suspend":
					loginfo("modEntry " + operation + " not implemented.");
					
					break;
				case "Restore":
					loginfo("modEntry " + operation + " not implemented.");
					
					break;
				case "Modify":
					jsonResult = sendGet("/" + uid + "/authentication/phoneMethods");
					
					JSONArray jarr = jsonResult.getJSONArray("value");
					j1 = sendDelete("/" + entry.getString("eruid") + "/authentication/phoneMethods/" + jarr.getJSONObject(0).getString("id"));
					
					if (j1.has("Error")) {
						throw new Exception(String.valueOf(j1.getString("Error")));
					} else {
						j1 = new JSONObject();
						jsonResult = new JSONObject();
						
						j1.put("phoneType", "mobile");
						j1.put("phoneNumber", "+55" + entry.getString("phoneNumber").replaceAll("\\D+",""));
						jsonResult = sendPost("/" + uid + "/authentication/phoneMethods", j1.toString());
						
						if (jsonResult.has("error")) {
							throw new Exception(String.valueOf(jsonResult.has("error")));
						}
					}
					
					break;
				default:
					logerror("Invalid entry type");
					throw new Exception("Invalid entry type");
			}
		}
		catch (Exception e) {
			logerror("Exception modifyUser: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.getMessage());
		}
	}
	
	public Entry queryReply(Entry entry) {
		return null;
	}
	
	private JSONObject sendPost(String context, String content) throws Exception {
		JSONObject jsonResult = new JSONObject();
		
		try {
			HttpPost var = new HttpPost(urlService + context);
			var.addHeader("Content-Type", "application/json");
			var.addHeader("Authorization", "Bearer " + token);
			var.addHeader("Accept", "application/json");
			var.setEntity(new StringEntity(content));
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
				int responseCode = response.getStatusLine().getStatusCode();
				
				loginfo("URL: " + var.getURI());
				loginfo("Method: " + var.getMethod());
				loginfo("Status line: " + response.getStatusLine().toString());
				
				if (responseCode == 201) {
					jsonResult.put("Success", EntityUtils.toString(response.getEntity()));
				} else {
					JSONObject tmp = new JSONObject(EntityUtils.toString(response.getEntity()));
					jsonResult.put("error", tmp.getJSONObject("error").getString("message").toString());
				}
	        }
		}
		catch (Exception e) {
			logerror("Exception sendPost: " + ExceptionUtils.getStackTrace(e));
			jsonResult.put("error", e.getMessage());
	    }
		
		return jsonResult;
	}
	
	private JSONObject sendGet(String context) throws Exception {
		JSONObject jsonResult = new JSONObject();
		
		try {
			HttpGet var = new HttpGet(urlService + context);
			var.addHeader("Authorization", "Bearer " + token);
			var.addHeader("Content-Type", "application/json");
			var.addHeader("Accept", "application/json");
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
	        	loginfo("URL: " + var.getURI());
	        	loginfo("Method: " + var.getMethod());
		    	loginfo("Status line: " + response.getStatusLine().toString());
				
	        	int responseCode = response.getStatusLine().getStatusCode();
	        	
	        	if (responseCode == 200) {
	        		jsonResult = new JSONObject(EntityUtils.toString(response.getEntity()));
				}
			}
		}
		catch (Exception e) {
			JSONObject tmp = new JSONObject();
			tmp.put("error", e.getMessage());
	    }
		
		return jsonResult;
	}
	
	private JSONObject sendDelete(String context) {
		JSONObject jsonResult = new JSONObject();
		
		try {
			HttpDelete var = new HttpDelete(urlService + context);
			var.addHeader("Authorization", "Bearer " + token);
			var.addHeader("Content-Type", "application/json");
			var.addHeader("Accept", "application/json");
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
				int responseCode = response.getStatusLine().getStatusCode();
				
				loginfo("URL: " + var.getURI());
				loginfo("Method: " + var.getMethod());
		    	loginfo("Status line: " + response.getStatusLine().toString());
		    	
		        if (responseCode == 204) {
		        	jsonResult.put("success", "OK");
				} else {
					jsonResult.put("error", EntityUtils.toString(response.getEntity()));
				}
	        }
		}
		catch (Exception e) {
			JSONObject tmp = new JSONObject();
			tmp.put("error", e.getMessage());
	    }
		
		return jsonResult;
	}
	
	private String getToken() {
		String token = "";
		
		try {	    	
			HttpPost httpPost = new HttpPost(urlAuthentication + "/" + tenant + "/oauth2/v2.0/token");
		    List<NameValuePair> params = new ArrayList<NameValuePair>();
		    params.add(new BasicNameValuePair("grant_type", grantType));
		    params.add(new BasicNameValuePair("client_id", clientId));
		    params.add(new BasicNameValuePair("client_secret", clientSecret));
		    params.add(new BasicNameValuePair("scope", "https://graph.microsoft.com/.default"));
		    httpPost.setEntity(new UrlEncodedFormEntity(params));
		    
		    try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(httpPost)) {
		    	
		    	int responseCode = response.getStatusLine().getStatusCode();
		    	
		    	loginfo("URL: " + httpPost.getURI());
		    	loginfo("Method: " + httpPost.getMethod());
		    	loginfo("Status line: " + response.getStatusLine().toString());
				
		    	if (responseCode == 200) {
			    	JSONObject jsonResult = new JSONObject(EntityUtils.toString(response.getEntity()));
			    	token = jsonResult.getString("access_token").replaceAll("token: ", "");
		    	}
	        }
		}
		catch (Exception e) {
			logerror("Exception getToken: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return token;
	}
	
	private String[] sendGetId() throws Exception {
		String[] array = null;
		
		try {
			HttpGet var = new HttpGet(urlService);
			var.addHeader("Content-Type", "application/json");
			var.addHeader("Authorization", "Bearer " + token);
			var.addHeader("Accept", "application/json");
	        
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
		        	CloseableHttpResponse response = httpClient.execute(var)) {
	        	
	        	loginfo("URL: " + var.getURI());
	        	loginfo("Method: " + var.getMethod());
		    	loginfo("Status line: " + response.getStatusLine().toString());
				
	        	int responseCode = response.getStatusLine().getStatusCode();
	        	List<String> listOfStrings = new ArrayList<String>();
	        	
	        	if (responseCode == 200) {
	        		JSONObject jsonResult = new JSONObject(EntityUtils.toString(response.getEntity()));
	        		JSONArray results = jsonResult.getJSONArray("value");
	        		
	        		for (int i = 0; i < results.length(); ++i) {
	        			JSONObject rec = results.getJSONObject(i);
	        			listOfStrings.add(rec.getString("userPrincipalName"));
	        		}
	        		
	        		array = listOfStrings.toArray(new String[0]);
				}
			}
		}
		catch (Exception e) {
			array = null;
			logerror("Exception sendGetId: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return array;
	}
	
	private String testService() throws Exception {
		String ret  = "site_up";
		
		try {
			loginfo("Test Service");
			loginfo("type: " + type);
			loginfo("urlService: " + urlService);
			loginfo("urlAuthentication: " + urlAuthentication);
			loginfo("clientSecret: *************");
			loginfo("grantType: " + grantType);
			loginfo("clientId: " + clientId);
			loginfo("tenant: " + tenant);
			loginfo("operation: " + operation);
			
			URL path = new URL(urlService);
			HttpURLConnection conn = (HttpURLConnection) path.openConnection();
			conn.setRequestMethod("GET");
			
			loginfo(String.format("Fetching %s ...", urlService));
			
			int responseCode = conn.getResponseCode();
			
			if (responseCode == 200) {
				loginfo(String.format("Site is up, content length = %s", conn.getHeaderField("content-length")));
			} else {
				loginfo(String.format("Site is up, but returns non-ok status = %d", responseCode));
			}
		}
		catch (java.net.UnknownHostException e) {
			ret = "site_down";
			logerror("Site is down");
		}
		
		return ret;
	}
	
	private void loginfo(String paramString) {
		this.myLog.info(paramString);
	}
	
	private void logerror(String paramString) {
		this.myLog.error(paramString);
	}
	
	@SuppressWarnings("unused")
	private void logdebug(String paramString) {
		this.myLog.debug(paramString);
	}
}
