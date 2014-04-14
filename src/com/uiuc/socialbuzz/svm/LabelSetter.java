package com.uiuc.socialbuzz.svm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class LabelSetter {
	
	public static void main(String[] args) throws SQLException, IOException {
		Scanner in = new Scanner(new File("PositiveTweetsIds.txt"));
		//Scanner in = new Scanner(new File("config\\test.txt"));
		int count = 1;
		
		//Database credentials
		String user = "pawar2";
		String pw = "change_me#";
		String dbUrl = "jdbc:mysql://harrier02.cs.illinois.edu/test";
		
		/*String user = "root";
		String pw = "root";
		String dbUrl = "jdbc:mysql://localhost/tedas"*/;
		String dbClass = "com.mysql.jdbc.Driver";
		Connection con = null;
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("tweetids.txt")));
		
		try {
			Class.forName(dbClass);
			con = DriverManager.getConnection(dbUrl, user, pw);
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while (in.hasNext())
		{
			String line = in.nextLine();
			int id = Integer.parseInt(line.trim());
			System.out.println(id);
			String query = "select tweetid from tweets where id=?";
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			
			/*if (rs.next())
			{
				long tweetid = rs.getLong("tweetid");
				bw.write(""+tweetid+"\n");
				System.out.println(tweetid);
			}*/
				
			
			long tweetid=0;
			if (rs.next())
			{
				tweetid = rs.getLong("tweetid");
				System.out.println(tweetid);
			}
			
			query = "update features set label=? where tweetid=?";
			ps = con.prepareStatement(query);
			ps.setString(1, "1");
			ps.setLong(2, tweetid);
			ps.executeUpdate();
		}
		bw.close();
		
		
		
	}

}
