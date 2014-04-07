package com.uiuc.socialbuzz.location;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;

public class SocialBuzzLocationListener {

	ProcessTweet pt;
	DictionaryMatching dm;
	
	SocialBuzzLocationListener() throws SQLException
	{	
		setupFiles();
	}

	void setupFiles() throws SQLException
	{
		pt = new ProcessTweet();
		dm = new DictionaryMatching();
		//Call Listener
		listener();	
	}
	
	
	
	
	/*****************************************************************************
	 * 
	 * Tweet listener - this is the onstatus
	 * @throws SQLException 
	 **************************************************************************/
	void listener() throws SQLException
	{
		//get this from status
		String tweet = 
			"game of thrones night at siebel"; 
		ArrayList<String> ngrams = pt.generateNGrams(tweet);
		
		ArrayList<Location> locations = dm.detectLocations(ngrams);
		if (locations.size()==0)
		{
			System.out.println("No location found");
			return;
		}
		
		//once you have list of possible matches and their coordinates, 
		// find the most probable one for yourself
		
		for (Location loc : locations)
		{
			System.out.println(loc.id+"\t"+loc.name+"\t"+loc.hits+"\t"+loc.ngram);
		}	
		
		Location mostProbableLoc = dm.getMostProbaleLocation(locations);
		if (mostProbableLoc!=null)
			System.out.println("Most probable location : "+mostProbableLoc.name);
		else
			System.out.println("Nothing");
		
		
		//send this tweet to classifier to validate the result  ??
		
		
		
	}
}
