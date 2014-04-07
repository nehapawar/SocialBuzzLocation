package com.uiuc.socialbuzz.helper;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class DictionaryDummy {
	
	public static void main(String[] args) throws FileNotFoundException, SQLException {
		
		
		//Database credentials
		String user = "root";
		String pw = "root";
		String dbUrl = "jdbc:mysql://localhost/tedas";
		String dbClass = "com.mysql.jdbc.Driver";
		String dictionaryInsert = "insert into dictionary (locationname) VALUES(?)";
		
		Connection con = null;
		try {
			Class.forName(dbClass);
			con = DriverManager.getConnection(dbUrl, user, pw);
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Scanner in = new Scanner(new File("D:\\UIUC\\Spring 2014\\Prof Kevin Chang\\Tweet Location Classifier\\xapi_meta"));
		
		while(in.hasNext())
		{
			String line = in.nextLine();
			
			if (line.contains("<tag k=\"name\""))
			{
				int vIndex = line.indexOf("v=");
				int vStart = vIndex+3;
				int vEnd = line.indexOf('"', vStart);
				//System.out.print(line.substring(vStart, vEnd)+"\t");
				
				PreparedStatement pstmt = con.prepareStatement(dictionaryInsert);
				pstmt.setString(1, line.substring(vStart, vEnd));
				pstmt.executeUpdate();
				pstmt.close();
				
			}
		}
		con.commit();
		con.close();
		
	}

}
