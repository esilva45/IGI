package br.com.asyg.connector.dummy;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

import br.com.asyg.connector.exceptions.ConnectorException;

public class DummyConnector extends Connector implements ConnectorInterface {
	public static final String[] CONNECTOR_MODES = new String[] { "Iterator", "Lookup" };
	private static final String COPYRIGHT = "Licensed Materials - Property of Asyg";
	private static final String myName = "Dummy Connector";
	private static String VERSION_INFO = null;
	private Entry resultEntry;
	private int current;
	private List<User> users = new ArrayList<User>();
	private String jdbcurl, jdbcpassword, jdbcuser;
	
	public DummyConnector() {
		setName(myName);
	    setModes(CONNECTOR_MODES);
	}
	
	public void terminate() throws Exception {
		this.jdbcurl = null;
		this.jdbcpassword = null;
		this.jdbcuser = null;
	    super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		logmsg("Initialize DummyConnector");
		this.jdbcurl = getParam("jdbcurl");
		this.jdbcpassword = getParam("jdbcpassword");
		this.jdbcuser = getParam("jdbcuser");
	}
	
	public synchronized void setResultEntry(Entry paramEntry) {
	    this.resultEntry = paramEntry;
	    logmsg("setResultEntry: " + this.resultEntry);
	}
	
	public void selectEntries() throws Exception {
		Connection conn = null;
		this.current = 0;
		
		try {
			conn = Util.getConnection("com.mysql.jdbc.Driver", jdbcurl, jdbcuser, jdbcpassword);
			
			PreparedStatement stmt = conn.prepareStatement("select cpf, matricula from usr");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				User usr = new User();
				usr.setPernr(rs.getString("matricula"));
				usr.setCpf(rs.getString("cpf"));
				users.add(usr);
			}
			
		}
		catch (Exception e) {
			logmsg("Exception selectEntries: " + e);
			throw new ConnectorException("Exception selectEntries: " + e.getMessage());
	    }
		finally {
			try {				
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				logmsg("Exception " + e);
				throw new ConnectorException("Exception selectEntries: " + e.getMessage());
			}
		}
	}
	
	public Entry getNextEntry() throws Exception {
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
			throw new ConnectorException("Exception getNextEntry: " + e.getMessage());
	    }
		
		return entry;
	}
	
	private Entry retUser(User var2) throws Exception {
	    Entry var1 = new Entry();
	    var1.setAttribute("erpernr", var2.getPernr());
	    var1.setAttribute("eruid", var2.getCpf());
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
}
