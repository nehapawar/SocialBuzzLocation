package com.uiuc.socialbuzz.svm;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.uiuc.socialbuzz.location.DictionaryMatching;
import com.uiuc.socialbuzz.location.Location;
import com.uiuc.socialbuzz.location.ProcessTweet;

import org.apache.commons.lang.*;

public class ExtractFeatures {
	
	Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
	Matcher m = null;
	int MAX_GRAM_SIZE = 4;
	
	//Database credentials
	String user = "pawar2";
	String pw = "change_me#";
	String dbUrl = "jdbc:mysql://harrier02.cs.illinois.edu/test";
	String dbClass = "com.mysql.jdbc.Driver";
	Connection con = null;
	
	HashMap<String, Integer> corpusHash = new HashMap<String, Integer>();
	public ExtractFeatures() 
	{

		try {
			Class.forName(dbClass);
			con = DriverManager.getConnection(dbUrl, user, pw);
			con.setAutoCommit(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		createCorpus();
		readFileAndExtract();
	}
	
	private void createCorpus() 
	{
		try 
		{
			//Scanner in = new Scanner(new File("config\\corpus.txt"));
			Scanner in = new Scanner(new File("corpus.txt"));
			while (in.hasNext())
			{
				String line = in.nextLine();
				//System.out.println(line);
				String[] array = line.split("\t");
				corpusHash.put(array[0], Integer.parseInt(array[1]));
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void readFileAndExtract()  
	{
		
		int id=0;
		ProcessTweet pt = new ProcessTweet();
		ArrayList<Integer> problem = new ArrayList<Integer>();
		DictionaryMatching dm = new DictionaryMatching();
		try {
			//Scanner in = new Scanner(new File("test.txt"));
			String query = "select id, tweet, urls from features";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			while(rs.next())
			{
				try
				{
					id = rs.getInt("id");
					String line = rs.getString("tweet").toLowerCase();
					String urls = rs.getString("urls");
					
					ArrayList<String> grams = pt.generateNGrams(line);
					ArrayList<Location> locations = null;
					if (grams!=null)
					{
						locations = dm.detectLocations(grams);
					}
					
					//first feature : # of matching grams
					int f1 = locations ==null ? 0 : locations.size();
					
					//second feature : tf*idf of all unigrams
					double f2 = 0;
					ArrayList<String> grams2 = this.generateNGrams(line);
					double dumpSize = pt.tf.size();
					for (String gr : grams2)
					{
						int tf = org.apache.commons.lang.StringUtils.countMatches(line, gr);
						double dumpFrq = 1 + ((pt.tf.get(gr)==null) ? (0): (pt.tf.get(gr)));
						f2+= tf * dumpSize/dumpFrq;
					}
					
					//third feature : tf*idf with corpus pool
					double f3 = 0;
					double corpusSize = corpusHash.size();
					for (String gr : grams2)
					{
						int tf = org.apache.commons.lang.StringUtils.countMatches(line, gr);
						double corpusFrq = 1 + ((corpusHash.get(gr)==null)?0:corpusHash.get(gr)) ;
						f3+= tf * corpusSize / corpusFrq;
					}
					
					//fourth feature : edit distance of gram
					int f4=Integer.MAX_VALUE;
					if (f1!=0)
					{
						Location loc = dm.getMostProbaleLocation(locations);
						f4 = loc.editDistance;
					}
					
					//fifth feature : presence of 4sq urls
					int f5 = org.apache.commons.lang.StringUtils.countMatches(urls, "4sq");
					
					//System.out.println(line);
					System.out.println(id);
					//System.out.println("f1 : "+f1+"\t f2 : "+f2+"\t f3 : "+f3+"\t f4 : "+f4+"\t f5 : "+f5);
					
					String insert ="update features set gramsmatched=?, wordscore=?, corpusscore=?, editdistance=?, 4sq=? where id=?";
					PreparedStatement ps = con.prepareStatement(insert);
					ps.setInt(1, f1);
					ps.setDouble(2, f2);
					ps.setDouble(3, f3);
					ps.setInt(4, f4);
					ps.setInt(5, f5);
					ps.setInt(6, id);
					ps.executeUpdate();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					problem.add(id);
					continue;
				}
			}
			
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			
		}
		
		System.out.println("Problem records : ");
		for (int i : problem)
			System.out.println(i);
	}



	
	public ArrayList<String> generateNGrams(String tweet)
	{
		ArrayList<String> grams = new ArrayList<String>();

		//splits on spaces and on ....
		String[] allWords = tweet.split("\\s+|\\.\\.+");
		
		
		ArrayList<String> cleanWords = new ArrayList<String>();
		int n =0;
		
		//Process tweet to get clean words
		//remove URLs
		for (String word : allWords)
		{
			String addWord = word;
	
			//replace the ??? which appear a lot in the tweets
			addWord = addWord.replaceAll("\\?", "");
			
			if (addWord.trim().length()==0)
				continue;
			//System.out.println("3"+ word);
			//check if leading characters are something we dont want
			m = p.matcher(""+addWord.charAt(0));
			while (m.find())
			{
				addWord = addWord.substring(1);	
				if (addWord.trim().length()==0) break;
				m = p.matcher(""+addWord.charAt(0));
			}
			
			
			//System.out.println("4"+ word);
			if (addWord.trim().length()==0)
				continue;
			
			//check if trailing characters are something we dont want
			m = p.matcher(""+addWord.charAt(addWord.length()-1));
			while (m.find())
			{
				addWord = addWord.substring(0,addWord.length()-1);
				if (addWord.trim().length()==0) break;
				m = p.matcher(""+addWord.charAt(addWord.length()-1));
			} 
			
			//System.out.println("4"+ word);
			if (addWord.trim().length()==0)
				continue;
			
			//System.out.println("5"+ word);
			if (addWord.trim().length()>0)
			{
				cleanWords.add(addWord.toLowerCase());
				//System.out.print(addWord+" ");
			}
		
		}
		n = cleanWords.size();
		
		//Generate 1 grams
		for (String word : cleanWords)
		{
		
			{
				grams.add(word);
				//System.out.print(word+" ");
			}
		}
		
		int gramSize = 2;
		StringBuilder sb = null;
		//Generate n grams
		int gramLimit = ((n-1) > MAX_GRAM_SIZE)? (MAX_GRAM_SIZE) : (n-1);
		for (int i=0; i<gramLimit; i++)
		{
			for (int j=0; j<=n-gramSize; j++)
			{
				sb = new StringBuilder();
				sb.insert(0, "");
				int k=0;
				
				while (k<gramSize)
				{
					sb.append(cleanWords.get(j+k));
					
					sb.append(" ");
					k++;
				}
				grams.add(sb.toString().trim());
			}
			gramSize++;
		}

		return grams;
	}
}
