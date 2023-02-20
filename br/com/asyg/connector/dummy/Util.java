package br.com.asyg.connector.dummy;

import java.sql.Connection;
import java.sql.DriverManager;

import br.com.asyg.connector.exceptions.ConnectorException;

public class Util {
	public final static Connection getConnection(String driver, String url, String username, String password) throws Exception, ClassNotFoundException {

		try {
    		Class.forName(driver);
    		return DriverManager.getConnection(url, username, password);            
        } 
		catch (Exception e) {
			System.out.println("Exception Connection: " + e);
			throw new ConnectorException("Exception Connection: " + e);
		}
	}
}
