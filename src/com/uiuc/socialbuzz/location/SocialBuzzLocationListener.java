package com.uiuc.socialbuzz.location;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;

import jnisvmlight.FeatureVector;
import jnisvmlight.SVMLightModel;

public class SocialBuzzLocationListener {

	ProcessTweet pt;
	DictionaryMatching dm;
	HashMap<String, Integer> corpusHash = new HashMap<String, Integer>();
	File f = new File("socialbuzz.svm.model");
	SVMLightModel svm ;
	
	SocialBuzzLocationListener() throws SQLException
	{	
		setupFiles();
		createCorpus();
		loadClassifier();
		 
	}

	void setupFiles() throws SQLException
	{
		pt = new ProcessTweet();
		dm = new DictionaryMatching();
		//Call Listener
		listener();	
	}
	
	
	void loadClassifier()
	{
		try {
			svm = SVMLightModel.readSVMLightModelFromURL(f.toURI().toURL());
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*****************************************************************************
	 * 
	 * Tweet listener - this is the onstatus
	 * @throws SQLException 
	 **************************************************************************/
	void listener() throws SQLException
	{
		Status status = null;
		//get this from status
		String[] tweets = 
		{"game of thrones night at siebel", 
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
			"@yaya_bmasta and who wants to go to Applebee's by themselves? Couples retreat, homieeee",
				"get me a mac book"};
			
		int counter = 1;
		for (String tweet : tweets)
		{
			
			System.out.println(counter);
			System.out.println(tweet);
			counter++;
		
			//generate ngrams
		ArrayList<String> ngrams = pt.generateNGrams(tweet);
		
		if (ngrams == null)
		{
			System.out.println("Not found");
			//return;
			continue;
		}
		
		
		//detect locations
		ArrayList<Location> locations = dm.detectLocations(ngrams);
		if (locations.size()==0)
		{
			System.out.println("No location found");
			//return;
			continue;
		}
		
		
		
		/*for (Location loc : locations)
		{
			System.out.println(loc.id+"\t"+loc.name+"\t"+loc.hits+"\t"+loc.ngram);
		}	*/
		
		//once you have list of possible matches and their coordinates, 
		// find the most probable one 
		Location mostProbableLoc = dm.getMostProbaleLocation(locations);
		if (mostProbableLoc!=null)
			System.out.println("Most probable location : "+mostProbableLoc.name);
		else
			System.out.println("Nothing");
		
		
		
		//send this tweet to classifier to validate the result 
	/*	double f1 = new Double (ngrams==null ? 0 : ngrams.size());
		double f4= f1==0 ? -1 : mostProbableLoc.editDistance;
		double prediction = classifyTweet(status, f1, f4);
		
		if (prediction==1)
			System.out.println("Its a location tweet");*/
		
		}
		
	}
	
	
	double classifyTweet(Status s, double f1, double f4)
	{
		User u = s.getUser();
		String tweetText = s.getText().toLowerCase();
		String urls = handleURL(s.getURLEntities());
		
		int[] dimensions = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
		double[] values = new double[18];
		values[0] = s.getContributors() == null? 0 : s.getContributors().length;
		values[1] = s.getRetweetCount();
		values[2] = s.getUserMentionEntities()==null? 0 : s.getUserMentionEntities().length;
		values[3] = s.getHashtagEntities()==null? 0 :s.getHashtagEntities().length;
		values[4] = s.getURLEntities()==null? 0 : s.getURLEntities().length;
		values[5] = s.isFavorited()? 1 : 0;
		values[6] = s.getMediaEntities()==null? 0 : s.getMediaEntities().length;
		values[7] = u.getFavouritesCount();
		values[8] = u.getFollowersCount();
		values[9] = u.getFriendsCount();
		values[10] = u.getListedCount();
		values[11] = u.getStatusesCount();
		values[12] = u.isVerified()? 1:0;
		values[13]= f1;
		
		double f2 = 0;
		ArrayList<String> grams = pt.generateNGramsWithoutFiltering(tweetText);
		double dumpSize = pt.tf.size();
		for (String gr : grams)
		{
			int tf = org.apache.commons.lang.StringUtils.countMatches(tweetText, gr);
			double dumpFrq = 1 + ((pt.tf.get(gr)==null) ? (0): (pt.tf.get(gr)));
			f2+= tf * dumpSize/dumpFrq;
		}
		values[14]=f2;
		
		double f3 = 0;
		double corpusSize = corpusHash.size();
		for (String gr : grams)
		{
			int tf = org.apache.commons.lang.StringUtils.countMatches(tweetText, gr);
			double corpusFrq = 1 + ((corpusHash.get(gr)==null)?0:corpusHash.get(gr)) ;
			f3+= tf * corpusSize / corpusFrq;
		}
		values[15]=f3;
		values[16]=f4;
		
		double f5 = org.apache.commons.lang.StringUtils.countMatches(urls, "4sq");
		values[17]=f5;
		
		
		FeatureVector v = new FeatureVector(dimensions, values);
		double prediction = svm.classify(v);
		
		return prediction;
		
	    
	   
	}
	
	private void createCorpus() 
	{
		
		try 
		{
			Scanner in = new Scanner(new File("config\\corpus.txt"));
			
			while (in.hasNext())
			{
				String line = in.nextLine();
				
				String[] array = line.split("\t");
				corpusHash.put(array[0], Integer.parseInt(array[1]));
			
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**********************************************************************
	 * Method to create a single string from URLs in the tweet
	 * @param urls
	 * @return
	 **********************************************************************/
	public String handleURL(URLEntity[] urls){
		String urlStr = "";
		if (urls != null) {			
			for (URLEntity ue : urls) {
				urlStr = urlStr + " " + ue;
			}			
		}
		return urlStr;
	}
}
