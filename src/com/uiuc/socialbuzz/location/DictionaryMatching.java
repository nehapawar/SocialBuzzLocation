package com.uiuc.socialbuzz.location;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class DictionaryMatching {

	String matchingString = null;
	
	//Database credentials
	String user = "root";
	String pw = "root";
	String dbUrl = "jdbc:mysql://localhost/tedas";
	String dbClass = "com.mysql.jdbc.Driver";
	Connection con = null;
	
	public DictionaryMatching() 
	{
		
		
		try {
			Class.forName(dbClass);
			con = DriverManager.getConnection(dbUrl, user, pw);
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/***********************************************************************
	 * Method which will try to match every ngram shortlisted, 
	 * with the database dictionary 
	 * @param grams
	 * @return
	 * @throws SQLException
	 ***********************************************************************/
	ArrayList<Location> detectLocations(ArrayList<String> grams) throws SQLException
	{
		ArrayList<Location> probableLocations = new ArrayList<Location>();
		
		for (String gram : grams)
		{
			/*matchingString = 
				"select * from dictionary where " +
				"locationname like '%Grainger%' or " +
				"replace(locationname,'.','') like '%dp dough%' or " + 
				"replace(locationname, ''', '') like '%steak n shake%' or " +
				"replace(locationname, ' ', '') like '%redlion%' ";*/
			
			gram = gram.replace("'", "\\\'");
			
			
			matchingString = 
				"select id,locationname from dictionary where " +
				//straightforward partial match
				"locationname like '%"+gram+"%' or officialname like '%"+gram+"%' or shortname like '%"+gram+"%' or " + 
				//missing '.' match
				"replace(locationname,'.','') like '%"+gram+"%' or replace(officialname,'.','') like '%"+gram+"%' or replace(shortname,'.','') like '%"+gram+"%' or " + 
				//missing ''' match
				"replace(locationname, '\\'', '') like '%"+gram+"%' or replace(officialname, '\\'', '') like '%"+gram+"%' or replace(shortname, '\\'', '') like '%"+gram+"%' or " +
				//missing ' ' match
				"replace(locationname, ' ', '') like '%"+gram+"%' or replace(officialname, ' ', '') like '%"+gram+"%' or replace(shortname, ' ', '') like '%"+gram+"%' or " +
				//missing '.' and ' '
				" replace(replace(locationname,'.',''), ' ', '') like '%"+gram+"%' or replace(replace(officialname,'.',''), ' ', '') like '%"+gram+"%' or replace(replace(shortname,'.',''), ' ', '') like '%"+gram+"%' or " +
				//missing ''' and ' '
				"replace(replace(locationname, '\\'', ''), ' ', '') like '%"+gram+"%' or replace(replace(officialname, '\\'', ''), ' ', '') like '%"+gram+"%' or replace(replace(shortname, '\\'', ''), ' ', '') like '%"+gram+"%'"
				;
			
			
			
			
			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(matchingString);
			
			while (rs.next())
			{
				int id = rs.getInt("id");
				String location = rs.getString("locationname");
				//check if the gram is very common and its matching vaguely to the
				
				if (!alreadyHit(probableLocations, id, gram))
				{
					Location loc = new Location();
					loc.id = id;
					loc.name = location;
					loc.ngram.add(gram);
					probableLocations.add(loc);
				}
			}
		}
		con.close();

		return probableLocations;
	}
	
	
	/***********************************************************************
	 * Method which checks if the location we found is already matched due 
	 * to previous ngram
	 * @param locArr - location array populated uptil now
	 * @param lid - new location id to be put
	 * @return
	 **************************************************************************/
	boolean alreadyHit(ArrayList<Location> locArr, int lid, String gram)
	{
		boolean alreadyHit = false;
		
		for (Location loc : locArr)
		{
			if (loc.id == lid)
			{
				loc.hits++;
				loc.ngram.add(gram);
				alreadyHit = true;
				break;
			}
		}
		return alreadyHit;
	}
	
	/*************************************************************************
	 * 
	 * Gets the most probable location from amongst the ones hit
	 * 
	 ************************************************************************/
	Location getMostProbaleLocation(ArrayList<Location> locations)
	{
		int maxHits = Integer.MIN_VALUE;
		for (Location loc : locations)
		{
			if (loc.hits > maxHits)
			{
				maxHits=loc.hits;
			}
		}
		
		ArrayList<Location> maxHitLocations = new ArrayList<Location>();
		for (Location loc : locations)
		{
			if (loc.hits == maxHits)
			{
				maxHitLocations.add(loc);
				System.out.println(loc.name);
				
			}
		}
		
		//for each entry in max locations, get edit dist with the ngrams
		//pick the one with min edit distance
		int minEditDist = Integer.MAX_VALUE;
		Location bestMatchedLocation = null;
		String bestMatchedGram = "";
		for (Location loc : maxHitLocations)
		{
			for (String ngram : loc.ngram)
			{
				int editDist = getEditDistance(ngram, loc.name);
				
				if (editDist <= minEditDist)
				{
					if (editDist == minEditDist)
					{
						//if (loc.name.length() > bestMatchedLocation.name.length())
						if (ngram.length() > bestMatchedGram.length())
						{
							bestMatchedLocation = loc;
							bestMatchedGram = ngram;
						}
					}
					else
					{
						minEditDist = editDist;
						bestMatchedLocation = loc;
						bestMatchedGram = ngram;
					}	
				}
			}
		}
		return bestMatchedLocation;
	}
	
	
	/*******************************************************************
	 * 
	 * function to return edit distance of two strings
	 * @param one
	 * @param two
	 * @return
	 *******************************************************************/
	int getEditDistance(String one, String two)
	{
		int len1 = one.length();
		int len2 = two.length();
		
		int[][] distanceMatrix = new int[len1+1][len2+1];
		
		for (int i=0; i<=len1; i++)
		{
			distanceMatrix[i][0] = i;
		}
		for (int j=0; j<len2; j++)
		{
			distanceMatrix[0][j] = j;
		}
		
		for (int i=0; i<len1; i++)
		{
			char c1 = one.charAt(i);
			
			for (int j=0; j<len2; j++)
			{
				char c2 = two.charAt(j);
				
				if (c1 == c2)
				{
					distanceMatrix[i+1][j+1] = distanceMatrix[i][j];
				}
				else
				{
					int replace = distanceMatrix[i][j] + 1;
					int insert = distanceMatrix[i][j+1] + 1;
					int delete = distanceMatrix[i+1][j] + 1;
					
					int min = replace>insert ? insert:replace;
					min = delete>min ? min:delete;
					
					distanceMatrix[i+1][j+1] = min;
				}
			}
		}
		return distanceMatrix[len1][len2];
	}
}
