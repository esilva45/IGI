package br.com.asyg.connector.brk.hrfeed;

import java.util.HashMap;
import java.util.Properties;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataEventListener;
import com.sap.conn.jco.ext.DestinationDataProvider;

public class ITResourceDataProvider implements DestinationDataProvider {
	private DestinationDataEventListener eL;
    private HashMap<String, Properties> secureDBStorage = new HashMap<String, Properties>();

	@Override
	public Properties getDestinationProperties(String destinationName) {
		System.out.println("getDestinationProperties ");
		
		try {
            Properties p = secureDBStorage.get(destinationName);
            
            if (p != null && p.isEmpty())
                throw new DataProviderException(DataProviderException.Reason.INVALID_CONFIGURATION,"destination configuration is incorrect", null);

            return p;
        }
        catch (RuntimeException re) {
        	System.out.println("getDestinationProperties "+ re);
            throw new DataProviderException(DataProviderException.Reason.INTERNAL_ERROR, re);
        }
	}
	
	void changeProperties(String destName, Properties properties) {
		System.out.println("changeProperties ");
		
        synchronized (secureDBStorage) {
            if (properties == null) {
                if (secureDBStorage.remove(destName) != null)
                    eL.deleted(destName);
            } else {
                secureDBStorage.put(destName, properties);
                eL.updated(destName);
            }
        }
    }

	@Override
	public void setDestinationDataEventListener(DestinationDataEventListener eventListener) {
		this.eL = eventListener;
	}

	@Override
	public boolean supportsEvents() {
		return true;
	}
}
