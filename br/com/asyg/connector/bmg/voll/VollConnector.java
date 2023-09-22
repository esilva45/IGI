package br.com.asyg.connector.bmg.voll;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

import br.com.asyg.connector.bmg.voll.model.Account;
import br.com.asyg.connector.bmg.voll.model.Groups;

public class VollConnector extends Connector implements ConnectorInterface {
	private static String VERSION_INFO = null;
	private String clientId, clientSecret, token, url, operation, type, uid;
	private static Util util = new Util();
	private List<Object> userList = new ArrayList<>();
	private List<Object> grpList = new ArrayList<>();
	private int current;
	
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
	
	public void initialize(Object o) throws Exception {
		loginfo("Initialize Voll Connector");
		
		this.type = getParam("type");
		this.url = getParam("url");
		this.clientId = getParam("clientId");
		this.clientSecret = getParam("clientSecret");
		this.operation = getParam("operation");
		
		if (!type.equals("Test"))
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
		int i = 0;
		boolean next = true;
		JSONArray arr = new JSONArray();
		
		try {
			switch (type) {
				case "User":
					i = 1;
					next = true;
					
					if (!type.equals("User"))
			            break;
					
					while (next) {
						arr = getUsers(i);
						
			    		if (arr != null && arr.length() > 0) {
			    			for (int x = 0; x < arr.length(); x++) {
			                    JSONObject jsonobject = arr.getJSONObject(x);
			                    Account account = new Account();
			                    account.setId(jsonobject.isNull("id") ? "" : jsonobject.get("id").toString());
			                    account.setName(jsonobject.isNull("name") ? "" : jsonobject.getString("name"));
			                    account.setEmail(jsonobject.isNull("email") ? "" : jsonobject.getString("email"));
			                    account.setGroupid(jsonobject.isNull("groupId") ? "" : jsonobject.getString("groupId"));
			                    account.setProfile(jsonobject.isNull("profile") ? "" : jsonobject.getString("profile"));
			                    account.setPhone(jsonobject.isNull("phone") ? "" : jsonobject.getString("phone"));
			                    account.setWorspaceId(jsonobject.isNull("workspaceId") ? "" : jsonobject.getString("workspaceId"));
			                    account.setTags(jsonobject.isNull("tags") ? "" : jsonobject.getString("tags"));
			                    account.setPreferredName(jsonobject.isNull("preferredName") ? "" : jsonobject.getString("preferredName"));
			                    userList.add(account);
			    			}
			    			
							i++;
						} else {
							next = false;
						}
					}
					
					return;
				case "Groups":
					i = 1;
					next = true;
					
					if (!type.equals("Groups"))
			            break;
					
					while (next) {
						arr = getGroups(i);
						
			    		if (arr != null && arr.length() > 0) {
			    			for (int x = 0; x < arr.length(); x++) {
			                    JSONObject jsonobject = arr.getJSONObject(x);
			                    Groups grp = new Groups();
			                    grp.setId(jsonobject.get("id").toString());
			                    grp.setName(jsonobject.get("name").toString());
			                    grp.setWorkspaceId(jsonobject.get("workspaceId").toString());
			                    grpList.add(grp);
			    			}
			    			
							i++;
						} else {
							next = false;
						}
					}
					
					return;
				default:
					logerror("Invalid entry type");
					throw new Exception("Invalid entry type");
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
				case "Groups":
					if (!type.equals("Groups"))
			            break;
					
					if (grpList != null && current < grpList.size()) {
						Groups var1 = (Groups)grpList.get(current);
						entry = retGroups(var1);
				        current++;
					}
					
					return entry;
				default:
					logerror("Invalid entry type");
					throw new Exception("Invalid entry type");
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
	    var1.setAttribute("eruid", var2.getId());
	    var1.setAttribute("ername", var2.getName());
	    var1.setAttribute("erprofile", var2.getProfile());
	    var1.setAttribute("erphone", var2.getPhone());
	    var1.setAttribute("erworspaceId", var2.getWorspaceId());
        var1.setAttribute("ergroupId", var2.getGroupid());
        var1.setAttribute("ertags", var2.getTags());
        var1.setAttribute("erpreferredName", var2.getPreferredName());
	    return var1;
	}
	
	private Entry retGroups(Groups var2) throws Exception {
		loginfo("Entering retGroups() method");
	    Entry var1 = new Entry();
	    var1.setAttribute("ergroupId", var2.getId());
	    var1.setAttribute("ergroupName", var2.getName());
	    return var1;
	}
	
	public void querySchema() throws Exception {
		loginfo("querySchema");
	}
	
	public Entry findEntry(SearchCriteria paramSearchCriteria) throws Exception {
		loginfo("Entering findEntry() method");
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
	
	@SuppressWarnings("resource")
	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception {
		try {
			String id = getUserId(entry.getString("eruid"));
			
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Authorization", "Bearer " + token);
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"query\":\"mutation ($input: RemoveUserInput!) {removeUser(input:$input)}\", "
					+ "\"variables\": {\"input\": {\"id\": \"" + id + "\"}}}");
			writer.flush();
			writer.close();
			
			logdebug("Body: " + httpConn.getOutputStream().toString());
			
			httpConn.getOutputStream().close();
			
			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			int responseCode = httpConn.getResponseCode();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			
			if (responseCode > 200 && responseCode < 299) {
				String error = s.hasNext() ? s.next() : "";
				logerror(error);
				throw new Exception(httpConn.getResponseCode() + ": " + util.removeHtml(error));
			}
		}
		catch (Exception e) {
			logerror("Exception deleteEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	@SuppressWarnings("resource")
	public void putEntry(Entry entry) throws Exception {
		String phone = "";
		
		try {
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Authorization", "Bearer " + token);
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setDoOutput(true);
			
			if (!util.isNullOrEmpty(entry.getString("erphone"))) {
				PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
				PhoneNumber swissNumberProto = phoneUtil.parse(entry.getString("erphone").toString(), "BR");
				phone = String.valueOf(swissNumberProto.getCountryCode()) + swissNumberProto.getNationalNumber();
			}
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"query\":\"mutation ($input: UserInput!) {createUser(input:$input)}\", \"variables\": {\"input\": {"
			+ "\"id\":\"" + entry.getString("eruid") + "\""
			+ ",\"groupId\":\"" + entry.getString("ergroupid") + "\""
			+ ",\"name\":\"" + entry.getString("ername") + "\""
			+ ",\"preferredName\":\"" + entry.getString("erpreferredname") + "\""
			+ ",\"email\":\"" + entry.getString("eremail") + "\""
			+ ",\"profile\":\"" + entry.getString("erprofile") + "\""
			+ ",\"tags\":[\"" + util.returnNullOrEmpty(entry.getString("ertags")) + "\"]"
			+ ",\"workspaceId\":\"" + util.returnNullOrEmpty(entry.getString("erworspaceId")) + "\""
			+ ",\"phone\":\"" + phone + "\""
			+ "}}}");
			
			writer.flush();
			writer.close();
			
			logdebug("Body: " + httpConn.getOutputStream().toString());
			
			httpConn.getOutputStream().close();
			
			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			int responseCode = httpConn.getResponseCode();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			
			if (responseCode < 200 && responseCode > 299) {
				String error = s.hasNext() ? s.next() : "";
				logerror(error);
				throw new Exception(httpConn.getResponseCode() + ": " + util.removeHtml(error));
			}
		}
		catch (Exception e) {
			logerror("Exception putEntry: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
	    }
	}
	
	@SuppressWarnings("resource")
	public void modEntry(Entry entry, SearchCriteria search, Entry old) throws Exception {
		loginfo("modEntry " + operation + ", entry " + entry + ", old " + old);
		int responseCode = 0;
		String phone = "";
		
		try {
			InputStream responseStream = null;
			Scanner s = null;
			
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Authorization", "Bearer " + token);
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			
			String id = getUserId(uid);
			
			switch (operation.toLowerCase()) {
				case "suspend":
					writer.write("{\"query\":\"mutation ChangeStatusUserInput($input: ChangeStatusUserInput!) {changeStatusUser(input:$input)}\","
							+ "\"operationName\":\"ChangeStatusUserInput\",\"variables\":{\"input\":"
							+ "{\"id\":\"" + id + "\",\"active\":\"INACTIVE\"}}}");
					writer.flush();
					writer.close();
					
					logdebug("Body: " + httpConn.getOutputStream().toString());
					
					httpConn.getOutputStream().close();
					
					responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
					responseCode = httpConn.getResponseCode();
					s = new Scanner(responseStream).useDelimiter("\\A");
					
					if (responseCode > 200 && responseCode < 299) {
						String error = s.hasNext() ? s.next() : "";
						logerror(error);
						throw new Exception(httpConn.getResponseCode() + ": " + util.removeHtml(error));
					}
					
					break;
				case "restore":
					writer.write("{\"query\":\"mutation ChangeStatusUserInput($input: ChangeStatusUserInput!) {changeStatusUser(input:$input)}\","
							+ "\"operationName\":\"ChangeStatusUserInput\",\"variables\":{\"input\":"
							+ "{\"id\":\"" + id + "\",\"active\":\"ACTIVE\"}}}");
					writer.flush();
					writer.close();
					
					logdebug("Body: " + httpConn.getOutputStream().toString());
					
					httpConn.getOutputStream().close();
					
					responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
					responseCode = httpConn.getResponseCode();
					s = new Scanner(responseStream).useDelimiter("\\A");
					
					if (responseCode > 200 && responseCode < 299) {
						String error = s.hasNext() ? s.next() : "";
						logerror(error);
						throw new Exception(httpConn.getResponseCode() + ": " + util.removeHtml(error));
					}
					
					break;
				case "modify":
					if (!util.isNullOrEmpty(entry.getString("erphone"))) {
						PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
						PhoneNumber swissNumberProto = phoneUtil.parse(entry.getString("erphone").toString(), "BR");
						phone = String.valueOf(swissNumberProto.getCountryCode()) + swissNumberProto.getNationalNumber();
					}
					
					writer.write("{\"query\":\"mutation ($input: UserInput!) {createUser(input:$input)}\", \"variables\": {\"input\": {\"id\":\"" + uid + "\""
							+ ",\"groupId\":\"" + entry.getString("ergroupid") + "\""
							+ ",\"name\":\"" + entry.getString("ername") + "\""
							+ ",\"preferredName\":\"" + entry.getString("erpreferredname") + "\""
							+ ",\"email\":\"" + entry.getString("eremail") + "\""
							+ ",\"profile\":\"" + entry.getString("erprofile") + "\""
							+ ",\"tags\":[\"" + entry.getString("ertags") + "\"]"
							+ ",\"workspaceId\":\"" + entry.getString("erworspaceId") + "\""
							+ ",\"phone\":\"" + phone + "\""
							+ "}}}");
					
					writer.flush();
					writer.close();
					
					logdebug("Body: " + httpConn.getOutputStream().toString());
					
					httpConn.getOutputStream().close();
					
					responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
					responseCode = httpConn.getResponseCode();
					
					s = new Scanner(responseStream).useDelimiter("\\A");
					
					if (responseCode < 200 && responseCode > 299) {
						String error = s.hasNext() ? s.next() : "";
						logerror(error);
						throw new Exception(httpConn.getResponseCode() + ": " + util.removeHtml(error));
					}
					
					break;
				default:
					logerror("Invalid entry type");
					throw new Exception("Invalid entry type");
			}
		}
		catch (Exception e) {
			logerror("Exception modifyUser: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(e.toString());
		}
	}
	
	@SuppressWarnings("resource")
	private String getToken() throws Exception {
		String ret = "";
		
		try {
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"query\":\"mutation ($input: TokenInputPublic!) {createTokenPublic(input:$input){accessToken}}\","
					+ "\"variables\":{\"input\":{\"clientId\":\"" + clientId
					+ "\",\"clientSecret\":\"" + clientSecret + "\"}}}");
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();
			
			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			int responseCode = httpConn.getResponseCode();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			ret = s.hasNext() ? s.next() : "";
			
			if (responseCode >= 200 && responseCode <= 299) {
				JSONObject obj = new JSONObject(ret);
				ret = obj.getJSONObject("data").getJSONObject("createTokenPublic").getString("accessToken");
			} else {
				String error = s.hasNext() ? s.next() : "";
				logerror(error);
				ret = "";
			}
		}
		catch (Exception e) {
			logerror("Exception modifyUser: " + ExceptionUtils.getStackTrace(e));
			throw new Exception(ExceptionUtils.getStackTrace(e));
	    }
		
		return ret;
	}
	
	@SuppressWarnings("resource")
	private String getUserId(String value) {
		String ret = "";
		
		try {
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Authorization", "Bearer " + token);
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"operationName\": \"Query\", \"query\": \"query Query($filter: ListUserFilter, $pagination: PageInfoInput) "
					+ "{integrationListUser(filter: $filter, pagination: $pagination) "
					+ "{ items { id groupId name preferredName email profile phone tags workspaceId } "
					+ "pageInfo { hasPrevious hasNext size current }}}\",\"variables\": "
					+ "{\"filter\": { \"email\": \"" + value + "\"}}}");
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();
			
			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			int responseCode = httpConn.getResponseCode();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			ret = s.hasNext() ? s.next() : "";
			
			if (responseCode >= 200 && responseCode <= 299) {
				JSONObject obj = new JSONObject(ret);
				JSONArray arr = obj.getJSONObject("data").getJSONObject("integrationListUser").getJSONArray("items");
				ret = arr.getJSONObject(0).getString("id");
			} else {
				String error = s.hasNext() ? s.next() : "";
				logerror(error);
				ret = "0";
			}
		}
		catch (Exception e) {
			logerror("Exception getUserId: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return ret;
	}
	
	@SuppressWarnings("resource")
	private JSONArray getUsers(int nummber) {
		String ret = "";
		JSONArray arr = new JSONArray();
		
		try {
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Authorization", "Bearer " + token);
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"operationName\": \"Query\",\"query\": \"query Query($filter: ListUserFilter, $pagination: PageInfoInput) "
					+ "{integrationListUser(filter: $filter, pagination: $pagination) "
					+ "{ items { id groupId name preferredName email profile phone tags workspaceId } "
					+ "pageInfo { hasPrevious hasNext size current }}}\","
					+ "\"variables\": {\"pagination\": {\"limit\": 100,\"number\": " + nummber + "}}}");
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			int responseCode = httpConn.getResponseCode();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			ret = s.hasNext() ? s.next() : "";
			
			if (responseCode >= 200 && responseCode <= 299) {
				JSONObject obj = new JSONObject(ret);
				arr = obj.getJSONObject("data").getJSONObject("integrationListUser").getJSONArray("items");
			} else {
				String error = s.hasNext() ? s.next() : "";
				logerror(error);
			}
		}
		catch (Exception e) {
			logerror("Exception getUsers: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return arr;
	}
	
	@SuppressWarnings("resource")
	private JSONArray getGroups(int nummber) {
		JSONArray arr = new JSONArray();
		String ret = "";
		
		try {
			URL path = new URL(url + "/graphql");
			HttpURLConnection httpConn = (HttpURLConnection) path.openConnection();
			httpConn.setRequestMethod("POST");
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("Authorization", "Bearer " + token);
			httpConn.setDoOutput(true);
			
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"operationName\": \"Query\",\"query\": \"query Query($filter: ListGroupFilter, $pagination: PageInfoInput){ "
					+ "integrationListGroup(filter: $filter, pagination: $pagination) { "
					+ "items {workspaceId name id } pageInfo { hasPrevious hasNext size current }}}\","
					+ "\"variables\": {\"pagination\": {\"limit\": 50,\"number\": " + nummber + "}}}");
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();
			
			InputStream responseStream = httpConn.getResponseCode() / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();
			int responseCode = httpConn.getResponseCode();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			ret = s.hasNext() ? s.next() : "";
			
			if (responseCode >= 200 && responseCode <= 299) {
				JSONObject obj = new JSONObject(ret);
				arr = obj.getJSONObject("data").getJSONObject("integrationListGroup").getJSONArray("items");
			} else {
				String error = s.hasNext() ? s.next() : "";
				logerror(error);
			}
		}
		catch (Exception e) {
			logerror("Exception getGroups: " + ExceptionUtils.getStackTrace(e));
	    }
		
		return arr;
	}
	
	private String testService() throws Exception {
		String ret  = "site_up";
		
		try {
			URL path = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) path.openConnection();
			conn.setRequestMethod("GET");
			
			loginfo(String.format("Fetching %s ...", url));
			
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
