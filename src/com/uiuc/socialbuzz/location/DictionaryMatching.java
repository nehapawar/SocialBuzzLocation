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
	/*String user = "pawar2";
	String pw = "change_me#";
	String dbUrl = "jdbc:mysql://harrier02.cs.illinois.edu/test";*/
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
	public ArrayList<Location> detectLocations(ArrayList<String> grams) throws SQLException
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
			
				if (checkVagueMatch(location, gram))
					continue;
				
				
				//check if already hit by a previous gram
				if (!alreadyHit(probableLocations, id, location, gram))
				{
					Location loc = new Location();
					loc.id = id;
					loc.name = location;
					loc.ngram.add(new NGram(gram));
					
					
					//System.out.println("addint to prob : "+id+ " "+location+" "+gram);
					probableLocations.add(loc);
				}
			}
		}
		

		return probableLocations;
	}
	
	
	private boolean checkVagueMatch(String location, String gram) 
	{
		//System.out.println(location+" "+gram);
		String locationLowerCase = location.toLowerCase();
		//System.out.println(locationLowerCase.indexOf(gram));
		//System.out.println(location.length()+" "+gram.length());
		
		//start of gram and characters left at the end
		int start, end;
		start = locationLowerCase.indexOf(gram);
		//System.out.println(location+" "+gram+" "+start);
		if ((start>0 && locationLowerCase.charAt(start-1)==' ') || (start==0))
		{
			if( (((start+gram.length())<locationLowerCase.length()) && locationLowerCase.charAt(start+gram.length())==' ') || ((start+gram.length())==location.length()))
			{
				//System.out.println("returning false");
				return false;
			}
				
		}
		end = location.length() - gram.length() - locationLowerCase.indexOf(gram);
		if (start > 0 && (end>2)) //how to choose this metric
		{
			//System.out.println("vague match..");
			return true;
		}
		
		
		String locationWithoutSpace = locationLowerCase.replaceFirst(" ", "");
		start = locationWithoutSpace.indexOf(gram);
		if ((start>0 && locationWithoutSpace.charAt(start-1)==' ') || (start==0))
		{
			if( (((start+gram.length())<locationWithoutSpace.length()) && locationWithoutSpace.charAt(start+gram.length())==' ') || ((start+gram.length())==locationWithoutSpace.length()))
			{
				return false;
			}
				
		}
		end = locationWithoutSpace.length() - gram.length() - locationWithoutSpace.indexOf(gram);
		if (start > 0 && (end>2)) //how to choose this metric
		{
			//System.out.println("vague match..");
			return true;
		}
		
		
		return false;
	}

	

	/***********************************************************************
	 * Method which checks if the location we found is already matched due 
	 * to previous ngram
	 * @param locArr - location array populated uptil now
	 * @param lid - new location id to be put
	 * @return
	 **************************************************************************/
	boolean alreadyHit(ArrayList<Location> locArr, int lid, String location, String gram)
	{
		boolean alreadyHit = false;
		
		for (Location loc : locArr)
		{
			if (loc.id == lid 
					|| loc.name.equalsIgnoreCase(location)
					)
			{
				//System.out.println("increasinf hi of "+loc.id);
				loc.hits++;
				loc.ngram.add(new NGram(gram));
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
	public Location getMostProbaleLocation(ArrayList<Location> locations)
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
			if (loc.hits == maxHits || loc.hits== maxHits-1)
			{
				maxHitLocations.add(loc);
			//	System.out.println(loc.name);
				
			}
		}
		
		//for each entry in max locations, get edit dist with the ngrams
		//pick the one with min edit distance
		int minEditDist = Integer.MAX_VALUE;
		Location bestMatchedLocation = null;
		String bestMatchedGram = "";
		for (Location loc : maxHitLocations)
		{
			
			for (NGram ng : loc.ngram)
			{
				String ngram = ng.gram;
				int editDist = getEditDistance(ngram.toLowerCase(), loc.name.toLowerCase());
				//System.out.println("edit dist "+editDist+" "+ngram+" "+loc.name);
				if (editDist <= minEditDist)
				{
					if (editDist == minEditDist)
					{
						//if (loc.name.length() > bestMatchedLocation.name.length())
						if (ngram.replace("\\", "").length() > bestMatchedGram.length())
						{
							//System.out.println(ngram.length()+" "+bestMatchedGram.length());
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
		bestMatchedLocation.editDistance = minEditDist;
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
		//System.out.println(len1+" "+len2);
		int[][] distanceMatrix = new int[len1+1][len2+1];
		
		for (int i=0; i<=len1; i++)
		{
			distanceMatrix[i][0] = i;
		}
		for (int j=0; j<=len2; j++)
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
		/*for ( int i=0; i<=len1; i++)
		{
			for (int j=0; j<=len2; j++)
			{
				System.out.print(distanceMatrix[i][j]+" ");
			}
			System.out.println();
		}*/
		return distanceMatrix[len1][len2];
	}
}
