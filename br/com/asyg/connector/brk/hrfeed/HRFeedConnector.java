package br.com.asyg.connector.brk.hrfeed;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.Environment;

import br.com.asyg.connector.brk.hrfeed.model.User;

public class HRFeedConnector extends Connector implements ConnectorInterface {
	public static final String[] CONNECTOR_MODES = new String[] { "Iterator", "Lookup" };
	private static final String COPYRIGHT = "Licensed Materials - Property of Asyg";
	private static final String myName = "HRFeed Connector";
	private static String VERSION_INFO = null;
	protected String startDate, endDate;
	private int current;
	private List<User> users = new ArrayList<User>();
	private String user, passwd, ashost, sysnr, client, lang, days, jdbcurl, jdbcpassword, jdbcuser;
			
	public HRFeedConnector() {
		setName(myName);
	    setModes(CONNECTOR_MODES);
	}
	
	public void terminate() throws Exception {
		this.client = null;
		this.user = null;
		this.passwd = null;
		this.ashost = null;
		this.sysnr = null;
		this.lang = null;
		this.days = null;
		this.jdbcurl = null;
		this.jdbcpassword = null;
		this.jdbcuser = null;
	    super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		logmsg("Initialize HRFeedConnector");
		this.client = getParam("client");
		this.user = getParam("user");
		this.passwd = getParam("passwd");
		this.ashost = getParam("ashost");
		this.sysnr = getParam("sysnr");
		this.lang = getParam("lang");
		this.days = getParam("days");
		this.jdbcurl = getParam("jdbcurl");
		this.jdbcpassword = getParam("jdbcpassword");
		this.jdbcuser = getParam("jdbcuser");
	}
	
	public synchronized void setResultEntry(Entry paramEntry) {
		logmsg("setResultEntry: " + paramEntry);
	}
	
	public synchronized void selectEntries() throws Exception {
		ITResourceDataProvider memoryProvider = new ITResourceDataProvider();
		SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy");
		java.util.Date now = new java.util.Date();
		Connection conn = null;
		this.current = 0;
		
		try {
			Calendar calendarData = Calendar.getInstance();
			calendarData.setTime(now);
			calendarData.add(Calendar.DAY_OF_MONTH, Integer.parseInt(days) * -1);
			startDate = SDF.format(calendarData.getTime());
			endDate = SDF.format(now);
			
			conn = Util.getConnection("com.ibm.db2.jcc.DB2Driver", jdbcurl, jdbcuser, jdbcpassword);
			
			try {
				Integer.parseInt(days);
			}
			catch (NumberFormatException e) {
				days = "1";
			}
			
			if (!Environment.isDestinationDataProviderRegistered()) {
				Environment.registerDestinationDataProvider(memoryProvider);
			}

		    memoryProvider.changeProperties("ABAP_AS",getDestinationPropertiesFromUI());
		    
		    JCoDestination destination = JCoDestinationManager.getDestination("ABAP_AS");
			JCoFunction function = destination.getRepository().getFunction("ZRHF003_API_CSTMZ_IDM");
			
			if (function == null) {
	            throw new Exception("ZRHF003_API_CSTMZ_IDM" + " not found in SAP.");
	        }
			//logmsg("function: " + function);
			function.getImportParameterList().setValue("IV_BEGDA", SDF.parse(startDate));
			function.getImportParameterList().setValue("IV_ENDDA", SDF.parse(endDate));
			function.execute(destination);
			
			JCoTable codes = function.getExportParameterList().getTable("ET_OUTPUT");
			int qtd = codes.getNumRows();

			PreparedStatement stmt = conn.prepareStatement("select IDENTIFICATION_NUMBER, C_CPF from igacore.user_erc where C_CPF=?");
			
			for (int i = 0; i < qtd; i++) {
				codes.setRow(i);
				logmsg("PERNR: [" + codes.getString("PERNR") + "] CPF: [" + codes.getString("CPF").replaceAll("[^0-9]", "") + "] AEDTM: [" + codes.getString("AEDTM") + "]");
        		
        		stmt.setString(1, codes.getString("CPF").replaceAll("[^0-9]", ""));
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                	User usr = new User();
                	usr.setPernr(codes.getString("PERNR"));
    				usr.setCpf(codes.getString("CPF").replaceAll("[^0-9]", ""));
    				usr.setUid(codes.getString("CPF").replaceAll("[^0-9]", ""));
    				users.add(usr);
                }

                rs.close();
			}
			
			stmt.close();

			logmsg("Size: " + this.users.size());
		}
		catch (Exception e) {
			logmsg("Exception selectEntries: " + e);
			throw new Exception("Exception selectEntries: " + e.getMessage());
	    }
		finally {
			try {
				Environment.unregisterDestinationDataProvider(memoryProvider);
				
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
		Entry entry = null;
		
		try {
			if (users != null && current < users.size()) {
				User var2 = (User)users.get(current);
				entry = retUser(var2);
		        current++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			logmsg("Exception getNextEntry: " + e);
			throw new Exception("Exception getNextEntry: " + e.getMessage());
	    }
		
		return entry;
	}
	
	private Entry retUser(User var2) throws Exception {
	    Entry var1 = new Entry();
	    var1.setAttribute("erpernr", var2.getPernr());
	    var1.setAttribute("ercpf", var2.getCpf());
	    var1.setAttribute("eruid", var2.getUid());
	    return var1;
	}
	
	public void querySchema() throws Exception { }

	public void modifyUser(Entry entry) throws Exception { }
	
	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception { }
	
	public void putEntry(Entry entry) throws Exception { }
	
	public Entry queryReply (Entry entry) {
		return null;
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
				logmsg("Fail to determine the version information: " + e.getMessage());
			}
		return VERSION_INFO;
	}
	
	private Properties getDestinationPropertiesFromUI() {
		logmsg("Initialize getDestinationPropertiesFromUI");
		
        Properties connectProperties=new Properties();
        connectProperties.setProperty(DestinationDataProvider.JCO_ASHOST, ashost);
        connectProperties.setProperty(DestinationDataProvider.JCO_SYSNR, sysnr);
        connectProperties.setProperty(DestinationDataProvider.JCO_CLIENT, client);
        connectProperties.setProperty(DestinationDataProvider.JCO_USER, user);
        connectProperties.setProperty(DestinationDataProvider.JCO_PASSWD, passwd);
        connectProperties.setProperty(DestinationDataProvider.JCO_LANG, lang);
        return connectProperties;
    }
}
