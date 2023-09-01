package br.com.asyg.connector.bmg.as;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.Normalizer;

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
	
	public boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}
	
	public String sanitizeString(String string) {
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[^\\p{ASCII}]", "");
        string = string.replaceAll("\\s+", " ");

        return string;
    }
}
