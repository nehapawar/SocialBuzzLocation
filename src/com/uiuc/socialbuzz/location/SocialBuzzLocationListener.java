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
		String[] tweets = 
		{/*"game of thrones night at siebel", 
			"Picture Perfect night in Champaign...",
			"I guess you can consider last night semi-successful ???? #dzdoessemi #hesthebomb #mardigras @ Soma… http://t.co/spLF2bcgjT",
			"JAGABOMBS!!! (@ It's Brothers Bar & Grill) http://t.co/rXYGVhAqe1",
			"Great start to the evening. (with Nathan at Boltini Lounge) [pic] — https://t.co/2F83i4P9hK",
			"Moet on a Wednesday night! Probably can't beat that....... @ The Highdive http://t.co/u5iYhatWWE",
			"Also, we'll be on Green St ALL DAY tomorrow for #unofficial shenanigans",
			"Come on the quad and support black student voices being heard through a silent statement. Today 11am-2pm http://t.co/fnPgYz7chQ",
			"Trying out the new Computer Lab and Study Space. Solid (@ Illini Union) [pic]: http://t.co/wJDFrydmlr",
			"Lunch date at Merry Ann's @zfranz90 @j_norfleet84",
			"This #BeingBlackAtIllinois movement going on on the Quad today is really interesting...",
			"Excited to be back in Urbana at The Canopy Club tonight!",
			"Basketball (@ Activities & Recreation Center (ARC) w/ 5 others) http://t.co/1aAU2nmhqF",
			"We are outside evergreen tobacco, across from 309 E. Green st. Open 8am-3am",
			"Buffalo Wild Wings is selling boneless wings, wedges and mozzarella sticks in the parking lot of Evergreen Tobacco http://t.co/xjpkC2tcbY",
			"Police are riding in motorcycles down Healey St. http://t.co/RECvT9ov4a",
			"Reporting live from Green St...it's green! #Unofficial http://t.co/T3MajmufVK",
			"HAPPY HOUR: Everything will be only $1 from 2-3! Come find us at Sixth and Green! #unofficial #bakesale",
			"Evergreen Tobacco has a waving Sasquatch (?) outside for photos! #Unofficial http://t.co/70rjRjNEBb",
			"Champaign police were called to Red Lion -- wouldn't be specific but said it wasn't a big deal\" #Unofficial http://t.co/JukdzUOH15\"",
			"Orpheus in the Underworld (@ Krannert Center for the Performing Arts) http://t.co/ui5ond4bza",
			"Today's #dpdough #foodporn is a sausage and cheese pizza with garlic parmacrust around the edges. This… http://t.co/IWm60H5pKb",
			"Do not buy the Panera lobster bisque soup unless if you want to eat a cup of cream. #disappointed #sogross",
			"Iced vanilla macchiato is the best thing that ever happened to Starbucks ??",
			"7 pm group meetings ain't right.",
			"@yaya_bmasta and who wants to go to Applebee's by themselves? Couples retreat, homieeee",*/
				"give me the cup"};
			
		int counter = 1;
		for (String tweet : tweets)
		{
			
			System.out.println(counter);
			System.out.println(tweet);
			counter++;
			
		ArrayList<String> ngrams = pt.generateNGrams(tweet);
		if (ngrams == null)
		{
			System.out.println("Not found");
			//return;
			continue;
		}
		ArrayList<Location> locations = dm.detectLocations(ngrams);
		if (locations.size()==0)
		{
			System.out.println("No location found");
			//return;
			continue;
		}
		
		//once you have list of possible matches and their coordinates, 
		// find the most probable one for yourself
		
		/*for (Location loc : locations)
		{
			System.out.println(loc.id+"\t"+loc.name+"\t"+loc.hits+"\t"+loc.ngram);
		}	*/
		
		Location mostProbableLoc = dm.getMostProbaleLocation(locations);
		if (mostProbableLoc!=null)
			System.out.println("Most probable location : "+mostProbableLoc.name);
		else
			System.out.println("Nothing");
		
		System.out.println();
		
		//send this tweet to classifier to validate the result  ??
		
		}
		
	}
}
