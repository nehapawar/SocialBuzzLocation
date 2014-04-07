package com.uiuc.socialbuzz.dictionary;


import java.sql.*;

public class DBWriter {
	private static String user = "pawar2";
	private static String pw = "change_me#";
	private static String dbUrl = "jdbc:mysql://harrier02.cs.illinois.edu/test";//://dssi-ei2.cs.illinois.edu/crimeSearch";
	//private static String user = "mao";
	//private static String pw = "";
	//private static String dbUrl = "jdbc:mysql://localhost/test";//://dssi-ei2.cs.illinois.edu/crimeSearch";
	private static String dbClass = "com.mysql.jdbc.Driver";
		
	private static String locdbInsert = "insert into dictionary (locationname,officialname,shortname,amenity,place,cuisine,shop,housenumber,street,city,phone,openinghours,website,lat,lon) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	int CommitCount;
	Connection con;
	
	DBWriter(){
		try {
			Class.forName(dbClass);
			con = DriverManager.getConnection(dbUrl, user, pw);
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addLoc(Location loc){
		
		try {
			CommitCount++;
			PreparedStatement pstmt = con.prepareStatement(locdbInsert);
			
			pstmt.setString(1, loc.name);
			pstmt.setString(2, loc.official_name);
			pstmt.setString(3, loc.short_name);
			pstmt.setString(4, loc.amenity);
			pstmt.setString(5, loc.place);
			pstmt.setString(6, loc.cuisine);
			pstmt.setString(7, loc.shop);
			pstmt.setString(8, loc.housenumber);
			pstmt.setString(9, loc.street);
			pstmt.setString(10, loc.city);
			pstmt.setString(11, loc.phone);
			pstmt.setString(12, loc.opening_hours);
			pstmt.setString(13, loc.website);
			//System.out.println(loc.coord[0]);
			pstmt.setDouble(14, loc.coord[0]);
			pstmt.setDouble(15, loc.coord[1]);
			
			pstmt.executeUpdate();
			pstmt.close();

			if(CommitCount > 10)
			{
				CommitCount = 0;
				con.commit();
			}
		} catch (SQLException e) {
			System.out.println("user database error");
			e.printStackTrace();
		}
	}
}