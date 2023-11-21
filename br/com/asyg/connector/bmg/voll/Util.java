package br.com.asyg.connector.bmg.voll;

import java.sql.Connection;
import java.sql.DriverManager;

import org.json.JSONArray;

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
	
	public String removeHtml(String html) {
		html = html.replaceAll("<(.*?)\\>","");
	    html = html.replaceAll("<(.*?)\\\n","");
	    html = html.replaceFirst("(.*?)\\>", "");
	    html = html.replaceAll("&amp;","&");
	    html = html.replaceAll("&.*?;", "");
	    html = html.replaceAll("&nbsp;"," ");
	    html = html.replaceAll("\n"," ");
	    html = html.replaceAll(" +", " ");
	    return html.trim();
	}
	
	public String returnArray(String str) {
		if (str != null && !str.isEmpty()) {
			str = "[" + str + "]";
			JSONArray jsonArray = new JSONArray(str);
			return jsonArray.toString();
		} else {
			return "\"\"";
		}
	}
	
	public String returnNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return str;
		return "";
	}
	
	public boolean isNullOrEmpty(String str) {
		if (str != null && !str.isEmpty())
			return false;
		return true;
	}
}
