package br.com.asyg.connector.bmg.kibana;

import java.sql.Connection;
import java.sql.DriverManager;

public class Util {
	public final static Connection getConnection(String driver, String url, String username, String password) throws Exception, ClassNotFoundException {

		try {
    		Class.forName(driver);
    		return DriverManager.getConnection(url, username, password);            
        } 
		catch (Exception e) {
			System.out.println("Exception Connection: " + e);
			throw new Exception("Exception Connection: " + e);
		}
	}
}
