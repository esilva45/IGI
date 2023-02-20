package br.com.asyg.connector.prudential.adprh;

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

import br.com.asyg.connector.prudential.adprh.model.User;
import br.com.asyg.connector.exceptions.ConnectorException;

public class AdpRHConnector extends Connector implements ConnectorInterface {
	public static final String[] CONNECTOR_MODES = new String[] { "Iterator", "Lookup" };
	private static final String COPYRIGHT = "Licensed Materials - Property of Asyg";
	private static final String myName = "ADPRH Connector";
	private static String VERSION_INFO = null;
	private String password, url, user, vacation, exception;
	private List<User> users = new ArrayList<User>();
	private int current;
	
	public AdpRHConnector() {
		setName(myName);
		setModes(CONNECTOR_MODES);
	}
	
	public void terminate() throws Exception {
		this.url = null;
		this.user = null;
		this.password = null;
		this.vacation = null;
		this.exception = null;
		super.terminate();
	}
	
	public void initialize(Object o) throws Exception {
		logmsg("Initialize AdpRHConnector");
		this.url = getParam("url");
		this.user = getParam("user");
		this.password = getParam("password");
		this.vacation = getParam("vacation");
		this.exception = getParam("exception");
	}
	
	public void selectEntries() throws Exception {
		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		this.current = 0;
		
		try {
			User usr = new User();
			conn = Utils.getConnection("net.sourceforge.jtds.jdbc.Driver", url, user, password);
			
			stmt = conn.prepareStatement("SELECT Email,Inicio_Ferias,Fim_Ferias FROM " + vacation);
			rs = stmt.executeQuery();

			while (rs.next()) {
				usr.setUid(rs.getString("Email"));
				usr.setStartDate(rs.getString("Inicio_Ferias"));
				usr.setEndDate(rs.getString("Fim_Ferias"));
				usr.setStatus("1");
				users.add(usr);
			}

			stmt.close();
			rs.close();

			stmt = conn.prepareStatement("SELECT Email,Inicio_Excecao,Fim_Excecao FROM " + exception);
			rs = stmt.executeQuery();
			
			while (rs.next()) {
				usr.setUid(rs.getString("Email"));
				usr.setStartDate(rs.getString("Inicio_Excecao"));
				usr.setEndDate(rs.getString("Fim_Excecao"));
				usr.setStatus("0");
				users.add(usr);
			}
		}
		catch (Exception e) {
			logError("Exception selectEntries: " + e);
			throw new ConnectorException("Exception selectEntries: " + e.getMessage());
		}
		finally {
			try {
				if (rs != null) { rs.close();}
				if (stmt != null) { stmt.close();}
				if (conn != null) { conn.close();}
			}
			catch (Exception e) {
				logError("Exception " + e);
				throw new ConnectorException("Exception selectEntries: " + e.getMessage());
			}
		}
	}
	
	public Entry getNextEntry() throws Exception {
		try {
			logmsg("getNextEntry AdpRHConnector");
			Entry var1 = null;
			
			if (users.size() > 0) {
				User var2 = (User)this.users.get(this.current);
		        var1 = retUser(var2);
		        this.current++;
			}
	
			return var1;
		}
		catch (Exception e) {
			logError("Exception getNextEntry: " + e);
			throw new ConnectorException("Exception getNextEntry: " + e.getMessage());
		}
	}

	private Entry retUser(User var1) throws Exception {
	    Entry var2 = new Entry();
	    var2.setAttribute("eruid", var1.getUid());
	    var2.setAttribute("erstartdate", var1.getStartDate());
	    var2.setAttribute("erenddate", var1.getEndDate());
	    var2.setAttribute("erstatus", var1.getStatus());
	    return var2;
	}
	
	public void querySchema() throws Exception { }

	public void modifyUser(Entry entry) throws Exception { }

	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception { }

	public void putEntry(Entry entry) throws Exception { }

	public Entry queryReply(Entry entry) {
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
				logError("Fail to determine the version information: " + e.toString());
			}
		return VERSION_INFO;
	}
}
