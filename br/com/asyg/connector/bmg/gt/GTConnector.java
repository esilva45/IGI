package br.com.asyg.connector.bmg.gt;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;

import br.com.asyg.connector.bmg.gt.model.Account;
import br.com.asyg.connector.exceptions.ConnectorException;

public class GTConnector extends Connector implements ConnectorInterface {
	private static String VERSION_INFO = null;
	private List<Object> userList = new ArrayList<>();
	private String user, password, url, jdbcurl, jdbcpassword, jdbcuser, ctrl;
	private int current;
	Connection conn = null;
	
	public GTConnector() {
		setName("GT Connector");
	    setModes(new String[] { "Iterator", "Lookup" });
	}
	
	public void terminate() throws Exception {
		this.url = null;
		this.user = null;
		this.password = null;
		this.jdbcurl = null;
		this.jdbcpassword = null;
		this.jdbcuser = null;
		this.ctrl = null;
		
		if (conn != null)
			conn.close();
		
	    super.terminate();
	}
	
	public void initialize(Object o) throws ClassNotFoundException, Exception {
		loginfo("Initialize GTConnector");
		this.url = getParam("url");
		this.user = getParam("user");
		this.password = getParam("password");
		this.jdbcurl = getParam("jdbcurl");
		this.jdbcpassword = getParam("jdbcpassword");
		this.jdbcuser = getParam("jdbcuser");
		this.ctrl = UUID.randomUUID().toString().replace("-", "");
	}
	
	public synchronized void selectEntries() throws ConnectorException {
		Document doc = null;
		String field = "0", ercpf = null;
		this.current = 0;
		int totalHits = 0, cpf_null = 0, j = 1;
		
		try {
			switch (field) {
				case "0":
					doc = sendPost("/getbycriteria", searchField(0,0));
					break;
				case "1":
					doc = sendPost("/getbycriteria", searchDate("1",0,0));
					break;
				case "2":
					doc = sendGet("1153");
					break;
			}
			
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("totalHits");
			
			for (int temp = 0; temp < nList.getLength(); temp++) {
		        Node nNode = nList.item(temp);
		        totalHits = Integer.valueOf(nNode.getTextContent());
		        loginfo("Total: " + nNode.getTextContent());
			}
			
			if (totalHits > 0 ) {			
				for (int i = 0; i < totalHits; i+=1000) {
					conn = Util.getConnection("com.ibm.db2.jcc.DB2Driver", jdbcurl, jdbcuser, jdbcpassword);
					
					loginfo("Processando lote: " + j++);
					
					if (field.equals("0")) {
						doc = sendPost("/getbycriteria", searchField(1000,i));
					} else {
						doc = sendPost("/getbycriteria", searchDate("1",1000,i));
					}
					
					doc.getDocumentElement().normalize();
					
					NodeList nodeList = doc.getElementsByTagName("document");
					NodeList var1 = null;
					Node var2 = null;
					
					for (int itr = 0; itr < nodeList.getLength(); itr++) {
						Node node = nodeList.item(itr);
						Account usr = new Account();
			            
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							Element eElement = (Element) node;

							var1 = eElement.getElementsByTagName("id");
							var2 = var1.item(0);
							usr.setErnodeid(var2 != null ? var2.getTextContent() : "");
							
							var1 = eElement.getElementsByTagName("name");
							var2 = var1.item(0);
							usr.setErstatus(var2 != null ? var2.getTextContent() : "");
		
							var1 = eElement.getElementsByTagName("name");
							var2 = var1.item(1);
							usr.setErname(var2 != null ? var2.getTextContent() : "");
		
							var1 = eElement.getElementsByTagName("namelista");
							var2 = var1.item(0);
							usr.setErcentroLucro(var2 != null ? var2.getTextContent() : "");
							
							var1 = eElement.getElementsByTagName("birthDate");
							var2 = var1.item(1);
							usr.setErbirthDate(var2 != null ? var2.getTextContent() : "");
		
							var1 = eElement.getElementsByTagName("cpf");
							var2 = var1.item(0);
							usr.setErcpf(var2 != null ? var2.getTextContent() : "");
							ercpf = var2 != null ? var2.getTextContent() : "";
							
							var1 = eElement.getElementsByTagName("passport");
							var2 = var1.item(0);
							usr.setErpassport(var2 != null ? var2.getTextContent() : "");
		
							var1 = eElement.getElementsByTagName("mailcolaborador");
							var2 = var1.item(0);
							usr.setErmailcolaborador(var2 != null ? var2.getTextContent() : "");
		
							var1 = eElement.getElementsByTagName("manangerEmail");
							var2 = var1.item(0);
							usr.setErmanangerEmail(var2 != null ? var2.getTextContent() : "");
		
							var1 = eElement.getElementsByTagName("cellPhone");
							var2 = var1.item(0);
							usr.setErcellPhone(var2 != null ? var2.getTextContent() : "");
		
							var1 = eElement.getElementsByTagName("cnpj");
							var2 = var1.item(0);
							usr.setErcnpj(var2 != null ? var2.getTextContent() : "");
		
							var1 = eElement.getElementsByTagName("companyName");
							var2 = var1.item(0);
							usr.setErcompanyName(var2 != null ? var2.getTextContent() : "");
		
							var1 = eElement.getElementsByTagName("dataFimContrato");
							var2 = var1.item(0);
							usr.setErdataFimContrato(var2 != null ? var2.getTextContent() : "");
		
							if (!isNullOrEmpty(ercpf)) {
								preFeed(usr);
							} else {
								cpf_null++;
							}
						}
					}
					conn.close();
				}
				
				loginfo("CPF Nulo: " + cpf_null);
		        this.userList.addAll(retFeed());
			}
		}
		catch (Exception e) {
			logerror("Exception selectEntries: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException("Exception selectEntries: " + e.getMessage());
	    }
	}
	
	public synchronized Entry getNextEntry() throws ConnectorException {
		Entry entry = null;
		
		try {
			if (userList != null && current < userList.size()) {
				Account var2 = (Account)userList.get(current);
				entry = retUser(var2);
				entry.setOperation("modify");
		        current++;
			}
		}
		catch (Exception e) {
			logerror("Exception getNextEntry: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException("Exception getNextEntry: " + e.getMessage());
	    }
		
		return entry;
	}
	
	private Entry retUser(Account var2) throws Exception {
	    Entry var1 = new Entry();
	    var1.setAttribute("erstatus", var2.getErstatus());
	    var1.setAttribute("ername", var2.getErname());
	    var1.setAttribute("erbirthDate", var2.getErbirthDate());
	    var1.setAttribute("erpassport", var2.getErpassport());
	    var1.setAttribute("ermailcolaborador", var2.getErmailcolaborador());
	    var1.setAttribute("ercellPhone", var2.getErcellPhone());
	    var1.setAttribute("ermanangerEmail", var2.getErmanangerEmail());
	    var1.setAttribute("ercnpj", var2.getErcnpj());
	    var1.setAttribute("ercompanyName", var2.getErcompanyName());
	    var1.setAttribute("erdataFimContrato", var2.getErdataFimContrato());
	    var1.setAttribute("ercpf", var2.getErcpf());
	    var1.setAttribute("eruid", var2.getErcpf());
	    var1.setAttribute("ercentroLucro", var2.getErcentroLucro());
	    return var1;
	}
	
	private List<Account> retFeed() throws ConnectorException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Account> list = new ArrayList<>();
		
		try {
			if (conn != null)
				conn = Util.getConnection("com.ibm.db2.jcc.DB2Driver", jdbcurl, jdbcuser, jdbcpassword);
			
			stmt = conn.prepareStatement("SELECT NAME,VARCHAR_FORMAT(BIRTH_DATE,'DD/MM/YYYY') AS BIRTH_DATE,PASSPORT,MAIL_COLABORADOR,CELLPHONE,MANAGER_MAIL,CNPJ,"
					+ "COMPANY_NAME,VARCHAR_FORMAT(DATA_FIM_CONTRATO,'DD/MM/YYYY') AS DATA_FIM_CONTRATO,CPF,CENTRO_LUCRO,STATUS "
					+ "FROM IGITOOLS.PRE_FEED_GT");
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				Account usr = new Account();
				usr.setErstatus(rs.getString("STATUS") != null ? rs.getString("STATUS") : "");
				usr.setErname(rs.getString("NAME") != null ? rs.getString("NAME") : "");
				usr.setErcentroLucro(rs.getString("CENTRO_LUCRO") != null ? rs.getString("CENTRO_LUCRO") : "");
				usr.setErbirthDate(rs.getString("BIRTH_DATE") != null ? rs.getString("BIRTH_DATE") : "");
				usr.setErcpf(rs.getString("CPF"));
				usr.setErpassport(rs.getString("PASSPORT") != null ? rs.getString("PASSPORT") : "");
				usr.setErmailcolaborador(rs.getString("MAIL_COLABORADOR") != null ? rs.getString("MAIL_COLABORADOR") : "");
				usr.setErmanangerEmail(rs.getString("MANAGER_MAIL") != null ? rs.getString("MANAGER_MAIL") : "");
				usr.setErcellPhone(rs.getString("CELLPHONE") != null ? rs.getString("CELLPHONE") : "");
				usr.setErcnpj(rs.getString("CNPJ") != null ? rs.getString("CNPJ") : "");
				usr.setErcompanyName(rs.getString("COMPANY_NAME") != null ? rs.getString("COMPANY_NAME") : "");
				//usr.setErdataFimContrato(rs.getString("DATA_FIM_CONTRATO") != null ? rs.getString("DATA_FIM_CONTRATO") : "");
				usr.setErdataFimContrato("");
				list.add(usr);
			}
			
			rs.close();
			stmt.close();
		}
		catch (Exception e) {
			logerror("Exception retFeed: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
		
		return list;
	}
	
	private void preFeed(Account var) throws ConnectorException {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String var_ctrl = null, ercpf = null, sql = null;
		int var_status = -1, status = -1, exec = -1, var_id = 0;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date dfc = null, var_dfc = null;
		
		try {
			ercpf = StringUtils.leftPad(var.getErcpf().replaceAll("[^0-9]", ""), 11, "0");
			
			if (var.getErstatus().trim().equals("Colaborador Ativo")) {
				status = 0;
			} else if (var.getErstatus().trim().equals("Aguardando Assinaturas")) {
				status = 1;
			} else if (var.getErstatus().trim().equals("Inclusão de Documentos")) {
				status = 1;
			} else if (var.getErstatus().trim().equals("Colaborador Bloqueado")) {
				status = 2;
			} else if (var.getErstatus().trim().equals("Em processo de Bloqueio")) {
				status = 3;
			} else if (var.getErstatus().trim().equals("Colaborador Encerrado")) {
				status = 4;
			} else {
				status = 5;
			}
			
			stmt = conn.prepareStatement("SELECT ID, CTRL, ID_STATUS, DATA_FIM_CONTRATO FROM IGITOOLS.PRE_FEED_GT WHERE CPF = ?");
			stmt.setString(1, ercpf);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
				var_ctrl = rs.getString("CTRL");
				var_status = rs.getInt("ID_STATUS");
				var_id = rs.getInt("ID");
				dfc = rs.getDate("DATA_FIM_CONTRATO");
				
				if (!isNullOrEmpty(var.getErdataFimContrato().trim())) {
					var_dfc = formatter.parse(var.getErdataFimContrato().trim());
				}
				
				if (var_ctrl.equals(ctrl) && status < var_status) {
					exec = 0;
					sql = "UPDATE IGITOOLS.PRE_FEED_GT SET NAME=?, BIRTH_DATE=?, PASSPORT=?, MAIL_COLABORADOR=?, CELLPHONE=?, "
							+ "MANAGER_MAIL=?, CNPJ=?, COMPANY_NAME=?, DATA_FIM_CONTRATO=?, CPF=?, CENTRO_LUCRO=?, STATUS=?, ID_STATUS=?, CTRL=?, UPDATE=CURRENT TIMESTAMP WHERE ID = ?";
				} else if ((status < var_status) || (status == var_status)) {
					exec = 0;
					sql = "UPDATE IGITOOLS.PRE_FEED_GT SET NAME=?, BIRTH_DATE=?, PASSPORT=?, MAIL_COLABORADOR=?, CELLPHONE=?, "
							+ "MANAGER_MAIL=?, CNPJ=?, COMPANY_NAME=?, DATA_FIM_CONTRATO=?, CPF=?, CENTRO_LUCRO=?, STATUS=?, ID_STATUS=?, CTRL=?, UPDATE=CURRENT TIMESTAMP WHERE ID = ?";
				} else if (!var_ctrl.equals(ctrl) && status > var_status) {
					exec = 0;
					sql = "UPDATE IGITOOLS.PRE_FEED_GT SET NAME=?, BIRTH_DATE=?, PASSPORT=?, MAIL_COLABORADOR=?, CELLPHONE=?, "
							+ "MANAGER_MAIL=?, CNPJ=?, COMPANY_NAME=?, DATA_FIM_CONTRATO=?, CPF=?, CENTRO_LUCRO=?, STATUS=?, ID_STATUS=?, CTRL=?, UPDATE=CURRENT TIMESTAMP WHERE ID = ?";
				} else if ((status >= 2 || status <= 4) && var_dfc.after(dfc)) {
					exec = 0;
					sql = "UPDATE IGITOOLS.PRE_FEED_GT SET NAME=?, BIRTH_DATE=?, PASSPORT=?, MAIL_COLABORADOR=?, CELLPHONE=?, "
							+ "MANAGER_MAIL=?, CNPJ=?, COMPANY_NAME=?, DATA_FIM_CONTRATO=?, CPF=?, CENTRO_LUCRO=?, STATUS=?, ID_STATUS=?, CTRL=?, UPDATE=CURRENT TIMESTAMP WHERE ID = ?";
				}
			} else {
				exec = 0;
				var_id = Integer.parseInt(var.getErnodeid());
				sql = "INSERT INTO IGITOOLS.PRE_FEED_GT (NAME,BIRTH_DATE,PASSPORT,MAIL_COLABORADOR,CELLPHONE,MANAGER_MAIL,CNPJ,COMPANY_NAME,DATA_FIM_CONTRATO,CPF,CENTRO_LUCRO,STATUS,ID_STATUS,CTRL,IDGT,CREATE) "
					+ "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT TIMESTAMP)";
			}
			
			rs.close();
			
			if (exec == 0) {
				stmt = conn.prepareStatement(sql);
				stmt.setString(1, var.getErname().trim().toUpperCase());
				
				if (isNullOrEmpty(var.getErbirthDate().trim())) {
					stmt.setNull(2, java.sql.Types.DATE);
				} else {
					Date fd = formatter.parse(var.getErbirthDate().trim());
					java.sql.Date sqlDate = new java.sql.Date(fd.getTime());
					stmt.setDate(2, sqlDate);
				}
				
				stmt.setString(3, var.getErpassport().trim());
				stmt.setString(4, var.getErmailcolaborador().trim().toLowerCase());
				stmt.setString(5, var.getErcellPhone().trim());
				stmt.setString(6, var.getErmanangerEmail().trim().toLowerCase());
				stmt.setString(7, var.getErcnpj().replaceAll("[^0-9]", ""));
				stmt.setString(8, var.getErcompanyName().trim());
				
				if (isNullOrEmpty(var.getErdataFimContrato().trim())) {
					stmt.setNull(9, java.sql.Types.DATE);
				} else {
					Date fd = formatter.parse(var.getErdataFimContrato().trim());
					java.sql.Date sqlDate = new java.sql.Date(fd.getTime());
					stmt.setDate(9, sqlDate);
				}
				
				stmt.setString(10, ercpf);
				stmt.setString(11, var.getErcentroLucro().trim());
				stmt.setString(12, var.getErstatus().trim());
				stmt.setInt(13, status);
				stmt.setString(14, ctrl);
				stmt.setInt(15, var_id);
				stmt.executeUpdate();
				stmt.close();
				conn.commit();
			}
		}
		catch (Exception e) {
			logerror("Exception preFeed1: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
	}
	
	private Document sendPost(String context, JSONObject content) throws ConnectorException {
        Document doc = null;
        
		try {
			logdebug("content: " + content);
			
			String authStr = user + ":" + password;
		    String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());
			
		    URL obj = new URL(url.toString() + context);
			
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("POST");
	        connection.setRequestProperty("Accept", "application/xml");
	        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
	        connection.setRequestProperty("Authorization", "Basic " + base64Creds);
	        connection.setDoOutput(true);
	        
	        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	        wr.writeBytes(content.toString());
	        wr.flush();
	        wr.close();
	        
	        if (connection.getResponseCode() == 200) {
		        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    		String inputLine;
	    		StringBuffer response = new StringBuffer();
	    		
	    		while ((inputLine = in.readLine()) != null) {
	    			response.append(inputLine);
	    		}
	    		
	    		in.close();
	    		
	    		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));
    		}
		}
		catch (Exception e) {
			doc = null;
			logerror("Exception sendPost: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
		
		return doc;
	}
	
	private Document sendGet(String context) throws ConnectorException {
        Document doc = null;
        
		try {
			String authStr = user + ":" + password;
		    String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

			URL obj = new URL(url.toString() + "/" + context);
			
			HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
			connection.setRequestMethod("GET");
	        connection.setRequestProperty("Accept", "application/xml");
	        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
	        connection.setRequestProperty("Authorization", "Basic " + base64Creds);

	        if (connection.getResponseCode() == 200) {
		        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    		String inputLine;
	    		StringBuffer response = new StringBuffer();
	    		
	    		while ((inputLine = in.readLine()) != null) {
	    			response.append(inputLine);
	    		}
	    		
	    		in.close();
	
	    		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(response.toString())));
	        }
		}
		catch (Exception e) {
			doc = null;
			logerror("Exception sendGet: " + ExceptionUtils.getStackTrace(e));
			throw new ConnectorException(e.toString());
	    }
		
		return doc;
	}
	
	private JSONObject searchField(int var1, int var2) throws ConnectorException {
		JSONObject j1 = new JSONObject();
		j1.put("operator", "is");
		j1.put("value", "Colaborador Ativo");
		j1.put("fieldName", "collaboratorGT.status.name");
		
		JSONObject j2 = new JSONObject();
		j2.put("operator", "is");
		j2.put("value", "Colaborador Bloqueado");
		j2.put("fieldName", "collaboratorGT.status.name");

		JSONArray ja = new JSONArray();
		ja.put(j1);
		ja.put(j2);

		JSONObject j3 = new JSONObject();
		j3.put("criteriaOperatorGroup", "OR");
		j3.put("criteria", ja);
		
		JSONArray j4 = new JSONArray();
		j4.put(j3);
		
		JSONObject j5 = new JSONObject();
		//j5.put("criterionGroups", j4);
		j5.put("resultSize", var1);
		j5.put("from", var2);
		
		return j5;
	}
	
	private JSONObject searchDate(String value, int var1, int var2) throws ConnectorException {
		int days;
		
		try {
			days = Integer.parseInt(value);
		}
		catch (NumberFormatException e) {
			days = 1;
		}

		Date dt = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(dt);
		c.add(Calendar.DATE, -days);
		dt = c.getTime();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		String today = sdf.format(dt);
		
		JSONObject j1 = new JSONObject();
		j1.put("operator", "greaterThanEqual");
		j1.put("value", today);
		j1.put("fieldName", "collaboratorGT.created");
		
		JSONObject j2 = new JSONObject();
		j2.put("operator", "greaterThanEqual");
		j2.put("value", today);
		j2.put("fieldName", "collaboratorGT.updated");

		JSONArray ja = new JSONArray();
		ja.put(j1);
		ja.put(j2);

		JSONObject j3 = new JSONObject();
		j3.put("criteriaOperatorGroup", "OR");
		j3.put("criteria", ja);
		
		JSONArray j4 = new JSONArray();
		j4.put(j3);
		
		JSONObject j5 = new JSONObject();
		//j5.put("criterionGroups", j4);
		j5.put("resultSize", var1);
		j5.put("from", var2);
		
		return j5;
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
	
	private static boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
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
