package br.com.asyg.connector.bmg.as;

import java.sql.Connection;
import java.sql.DriverManager;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		if (str != null && !str.isEmpty()) {
			str = str.trim();
			if (str != null && !str.isEmpty())
				return false;
		}
		
		return true;
	}
	
	private String dateUnixFormat(String data) throws Exception {
		String dateString = "";
		
		try {
			Date currentTime = new Date(Long.parseLong(data));
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateString = formatter.format(currentTime);
		}
		catch (Exception e) {
			dateString = data;
		}
		
		return dateString;
	}
	
	public String dateFormat(String data) throws Exception {
		SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		String ret = null;
		
		try {
			String new_data = dateUnixFormat(data);
			
			SimpleDateFormat[] formats = {
					new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
				    new SimpleDateFormat("dd/MM/yyyy"),
				    new SimpleDateFormat("MM/dd/yyyy"),
				    new SimpleDateFormat("dd-MMM-yyyy E hh:mm a z"),
				    new SimpleDateFormat("yyyy-MM-dd HH:mm"),
				    new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy"),
				    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z"),
				    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			};
			
			for (SimpleDateFormat sdf : formats) {
			    try {
			    	date = sdf.parse(new_data);
			    	ret = dt1.format(date);
			    } catch (Exception e) {  }
			}
		}
		catch (Exception e) {
			System.out.println("Exception dateFormat: " + e);
		}
		
		return ret;
	}
	
	public String sanitizeString(String string) {
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[^\\p{ASCII}]", "");
        string = string.replaceAll("\\s+", " ");
        
        return string;
    }
}
