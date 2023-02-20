package br.com.asyg.connector.prudential.wellcare;

import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;

import com.ibm.di.connector.Connector;
import com.ibm.di.connector.ConnectorInterface;
import com.ibm.di.entry.Entry;
import com.ibm.di.server.SearchCriteria;

import br.com.asyg.connector.exceptions.ConnectorException;

public class WellcareConnector extends Connector implements ConnectorInterface {
	public static final String[] CONNECTOR_MODES = new String[] { "Iterator", "Lookup" };
	private static final String COPYRIGHT = "Licensed Materials - Property of Asyg";
	private static final String myName = "Wellcare Connector";
	private static String VERSION_INFO = null;
	private String password, url, user, vacation, exception;
	private int current;
	
	public WellcareConnector() {
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
		logmsg("Initialize WellcareConnector");
		this.url = getParam("url");
		this.user = getParam("user");
		this.password = getParam("password");
		this.vacation = getParam("vacation");
		this.exception = getParam("exception");
	}
	
	public void selectEntries() throws Exception {
		this.current = 0;
		
		try {

		}
		catch (Exception e) {
			logmsg("Exception selectEntries: " + e);
			throw new ConnectorException("Exception selectEntries: " + e);
		}
	}
	
	public Entry getNextEntry() throws Exception {
		try {
			logmsg("getNextEntry WellcareConnector");
			Entry var1 = null;

			return var1;
		} catch (Exception e) {
			logmsg("Exception getNextEntry: " + e);
			throw new ConnectorException("Exception getNextEntry: " + e);
		}
	}

	public void querySchema() throws Exception { }

	public void modifyUser(Entry entry) throws Exception { }

	public void deleteEntry(Entry entry, SearchCriteria search) throws Exception { }

	public void putEntry(Entry entry) throws Exception { }

	public Entry queryReply(Entry entry) {
		return null;
	}

	private int send(String method, String content, String token) throws Exception {
		return 0;
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
}
