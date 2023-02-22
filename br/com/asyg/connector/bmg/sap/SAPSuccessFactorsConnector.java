package br.com.asyg.connector.bmg.sap;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import javax.net.ssl.SSLContext;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

public class SAPSuccessFactorsConnector extends Connector implements ConnectorInterface {
	private static final String COPYRIGHT = "Licensed Materials - Property of Asyg";
	private static final String[] CONNECTOR_MODES = new String[] { "AddOnly", "Iterator", "Lookup", "CallReply" };
	private static final String myName = "SAPSuccessFactors Connector";
	private static String VERSION_INFO = null;
	private String url;
	private String user;
	private String password;
	private String companyID;
	private String privatekey;
	private String status_line;
	private String optionID;
	
	public SAPSuccessFactorsConnector() {
		setName(myName);
	    setModes(CONNECTOR_MODES);
	}
	
	public void terminate() throws Exception {
		this.url = null;
	    this.user = null;
	    this.password = null;
	    this.companyID = null;
	    this.privatekey = null;
	    this.optionID = null;
	    super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		logmsg("Initialize SAPSuccessFactorsConnector");
		this.companyID = getParam("company");
		this.optionID = getParam("optionid");
		this.password = getParam("password");
		this.url = getParam("url");
	    this.user = getParam("user");
	    this.privatekey = readPrivateKey(getParam("privatekey"));
	}
	
	public void selectEntries() throws Exception {	}
	
	public void querySchema() throws Exception { }

	public void modifyUser(Entry entry) throws Exception { }
	
	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception { }
	
	public Entry queryReply (Entry entry) {
		return null;
	}
	
	public Entry getNextEntry() throws Exception {
		return null;
	}
	
	public void putEntry(Entry entry) throws Exception {
		try {
			String token = getToken();
			boolean primary = !isPrimary(token, entry.getString("erpersonidexternal"));
			int ret = 501;
			
			logmsg("eremail: " + entry.getString("eremail"));
			logmsg("eruserid: " + entry.getString("eruserid"));
			logmsg("erpersonnelnumber: " + entry.getString("erpersonnelnumber"));
			logmsg("erpersonidexternal: " + entry.getString("erpersonidexternal"));
			
			// PerEmail
			Map<String, String> map1 = new HashMap<>();
			map1.put("uri", "PerEmail(emailType='" + optionID + "',personIdExternal='" + entry.getString("erpersonidexternal") + "')");
			map1.put("type", "SFOData.PerEmail");
			
			JSONObject j1 = new JSONObject();
			j1.put("emailAddress",entry.getString("eremail"));
			j1.put("isPrimary", primary);
			j1.put("__metadata",map1);
	
			logmsg("Json: " + j1.toString());
			ret = send("POST", j1.toString(), token);
			
			if (ret < 200 || ret > 299) {
				throw new Exception("Exception Send(PerEmail) " + status_line);
			} else {
				// User
				Map<String, String> map2 = new HashMap<>();
				map2.put("uri", "User('" + entry.getString("eruserid") + "')");
				map2.put("type", "SFOData.User");
				
				JSONObject j2 = new JSONObject();
				j2.put("username",entry.getString("erpersonnelnumber"));
				j2.put("__metadata",map2);
				
				logmsg("Json: " + j2.toString());
				ret = send("POST", j2.toString(), token);
				
				if (ret < 200 || ret > 299) {
					throw new Exception("Exception Send(User) " + status_line);
				}
			}
		}
		catch (Exception e) {
			logmsg("Exception putEntry: " + e);
			throw new Exception("Exception putEntry: " + e.getMessage());
	    }
	}

	private boolean isPrimary(String token, String personidexternal) throws Exception {
		boolean responseCode = false;
		String jsonResult = null;
		
		try {
			HttpGet post = new HttpGet(url + "/odata/v2/PerEmail?$top=10&$format=json&$filter=personIdExternal%20eq%20'" + personidexternal + "'%20and%20isPrimary%20eq%20'true'");
			post.addHeader("Content-Type", "text/plain; charset=utf-8");
			post.addHeader("Authorization", "Bearer " + token);
			post.addHeader("Accept", "application/json");
			
			logmsg("POST: " + post.toString());
			
			try (CloseableHttpClient client = HttpClientBuilder.create()
	        		.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[] { "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
            	    .build();
		        	CloseableHttpResponse response = client.execute(post)) {

				status_line = response.getStatusLine().getStatusCode() + " " + response.getStatusLine().toString();
				
				logmsg("URL: " + post.getURI());
				logmsg("Status line: " + status_line);

				if (response.getEntity() != null) {
			        jsonResult = EntityUtils.toString(response.getEntity());
			        JSONObject jsonObject = new JSONObject(jsonResult);
			        JSONObject d = jsonObject.getJSONObject("d");
			        JSONArray jsonarray = new JSONArray(d.get("results").toString());

			        if (!jsonarray.isEmpty()) {
					    for (int i = 0; i < jsonarray.length(); i++) {
				            JSONObject jsonobject = jsonarray.getJSONObject(i);
				            
				            if (jsonobject.getBoolean("isPrimary")) {
				            	responseCode = true;
				            } else {
				            	responseCode = false;
				            }
					    }
			        } else {
			        	responseCode = false;
			        }
				} else {
					throw new Exception("Exception Send: " + status_line);
				}
	        }
	        catch (Exception e) {
	        	logmsg("Exception: " + e);
	        	throw new Exception("Exception: " + e.getMessage());
		    }

			logmsg("ret: " + responseCode);
		}
		catch (Exception e) {
			logmsg("Exception send: " + e);
			throw new Exception("Exception send: " + e.getMessage());
	    }
		
		return responseCode;
	}
	
	private int send(String method, String content, String token) throws Exception {
		int responseCode = 501;
		String jsonResult = null;
		String message = null;
		
		try {
			status_line = "";
			
			HttpPost post = new HttpPost(url + "/odata/v2/upsert");
			post.addHeader("Content-Type", "application/json; charset=utf-8");
			post.addHeader("Authorization", "Bearer " + token);
			post.addHeader("Accept", "application/json");
			post.setEntity(new StringEntity(content));
	        
	        logmsg("POST: " + post.toString());
	        
	        try (CloseableHttpClient client = HttpClientBuilder.create()
	        		.setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[] { "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
            	    .build();
		        	CloseableHttpResponse response = client.execute(post)) {
	        	
				responseCode = response.getStatusLine().getStatusCode();
				status_line = responseCode + " " + response.getStatusLine().toString();
				logmsg("URL: " + post.getURI());
				logmsg("Status line: " + status_line);

				if (response.getEntity() != null) {
			        jsonResult = EntityUtils.toString(response.getEntity());
			        logmsg("Result: " + jsonResult);
			        
			        JSONObject jsonObject = new JSONObject(jsonResult);
			        JSONArray jsonarray = new JSONArray(jsonObject.get("d").toString());
				    
				    for (int i = 0; i < jsonarray.length(); i++) {
			            JSONObject jsonobject = jsonarray.getJSONObject(i);
			            responseCode = jsonobject.getInt("httpCode");
			            message = jsonobject.isNull("message") ? "OK" : jsonobject.getString("message");
				    }
				    
				    if (responseCode < 200 || responseCode > 299) {
				    	status_line = responseCode + " " + message;
				    }
				    
				} else {
					throw new Exception("Exception Send: " + status_line);
				}
	        }
	        catch (Exception e) {
	        	logmsg("Exception: " + e);
	        	throw new Exception("Exception: " + e.getMessage());
		    }

	        logmsg("ret: " + responseCode);
		}
		catch (Exception e) {
			logmsg("Exception send: " + e);
			throw new Exception("Exception send: " + e.getMessage());
	    }
		
		return responseCode;
	}
	
	private String getToken() throws Exception {
		String token = "";
		String resultado = "";
		JSONObject jsonObj = null;
		
		try {
			status_line = "";
			
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("client_id", password));
            nvps.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:saml2-bearer"));
            nvps.add(new BasicNameValuePair("company_id", companyID));
            nvps.add(new BasicNameValuePair("assertion", getAssertion()));
			
            HttpClient client = HttpClientBuilder.create()
            	    .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[] { "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
            	    .build();
            
			HttpPost post = new HttpPost(url + "/oauth/token");
			post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse response = client.execute(post);

			status_line = response.getStatusLine().getStatusCode() + " " + response.getStatusLine().toString();
			
			logmsg("URL: " + post.getURI());
			logmsg("Status line: " + status_line);

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String line = "";

            while ((line = rd.readLine()) != null) {
                resultado = resultado + line;
            }
            
            jsonObj = new JSONObject(resultado);

            if (!jsonObj.isNull("access_token")) {
                token = String.valueOf(jsonObj.get("access_token"));
            } else {
                token = "erro";
                throw new Exception(String.valueOf(jsonObj.get("errorMessage")));
            }
		}
		catch (Exception e) {
			logmsg("Fail getToken: " + e);
			throw new Exception("Exception send: " + e.getMessage());
		}
		
		return token;
	}
	
	private String getAssertion() throws Exception {
		String assertion = "";
		
		try {
			status_line = "";
			
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("client_id", password));
            nvps.add(new BasicNameValuePair("user_id", user));
            nvps.add(new BasicNameValuePair("token_url", url + "/oauth/token"));
            nvps.add(new BasicNameValuePair("private_key", privatekey));

            HttpClient client = HttpClientBuilder.create()
            	    .setSSLSocketFactory(new SSLConnectionSocketFactory(SSLContext.getDefault(), new String[] { "TLSv1.1", "TLSv1.2" }, null, SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
            	    .build();
            
			HttpPost post = new HttpPost(url + "/oauth/idp");
			post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			HttpResponse response = client.execute(post);

			status_line = response.getStatusLine().getStatusCode() + " " + response.getStatusLine().toString();
			
			logmsg("URL: " + post.getURI());
			logmsg("Status line: " + response.getStatusLine().toString());

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String line = "";

            while ((line = rd.readLine()) != null) {
                assertion = assertion + line;
            }
            
            if (assertion.equals("")) {
            	throw new Exception("Fail Assertion key " + status_line); 
            }
		}
		catch (Exception e) {
			logmsg("Fail getAssertion: " + e);
			throw new Exception("Fail getAssertion: " + e.getMessage());
		}
		
		return assertion;
	}
	
	private String readPrivateKey(String path) throws Exception {
		String line = null;
		
		try {
			if (path.equals("")) {
				throw new Exception("Fail read private key, path is null"); 
            }
			
			File file = new File(path);
			line = FileUtils.readFileToString(file, "UTF-8");
			
			if (line == null) {
				throw new Exception("Fail read private key, line is null"); 
            }
		} 
		catch (Exception e) {
			logmsg("Fail readPrivateKey: " + e);
			throw new Exception("Fail readPrivateKey: " + e.getMessage());
		}
		
		return line;
	}
	
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
				logmsg("Fail to determine the version information: " + e.getMessage());
			}
		return VERSION_INFO;
	}
}
