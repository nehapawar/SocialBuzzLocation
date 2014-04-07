package com.uiuc.socialbuzz.location;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessTweet {
	public HashSet<String> states;
	public HashSet<String> cities;
	public HashSet<String> stopwords;
	String cityFile = "config\\cities.txt"; 
	String stateFile = "config\\states.txt"; 
	String stopWordsFile = "config\\stop_words.txt";
	Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
	Matcher m = null;
	int MAX_GRAM_SIZE = 4;
	
	ProcessTweet()
	{
		
		//Create city and state hash sets
		fillCities();
		fillStates();
		
		//Create stop words hash set
		fillStopWords();
		
	}
	
	ArrayList<String> generateNGrams(String tweet)
	{
		ArrayList<String> grams = new ArrayList<String>();

		String[] allWords = tweet.split("\\s+");
		ArrayList<String> cleanWords = new ArrayList<String>();
		int n =0;
		
		//Process tweet to get clean words
		//remove URLs
		for (String word : allWords)
		{
			String addWord = word;
			
			//remove urls
			if (addWord.startsWith("http:"))
			{
				continue;
			}
			
			//replace the ??? which appear a lot in the tweets
			addWord = addWord.replaceAll("\\?", "");
			
			if (addWord.trim().length()==0)
				continue;
			
			//check if first character is something we dont want
			m = p.matcher(""+addWord.charAt(0));
			boolean b = m.find();
			if (b)
			{
				addWord = addWord.substring(1);
			}
			
			if (addWord.trim().length()==0)
				continue;
			
			//check if last character is something we dont want
			m = p.matcher(""+addWord.charAt(addWord.length()-1));
			b = m.find();
			if (b)
			{
				addWord = addWord.substring(0,addWord.length()-1);
			} 
			
			
			if (addWord.trim().length()>0)
			{
				cleanWords.add(addWord.toLowerCase());
			}
		}
		n = cleanWords.size();
		
		//Generate 1 grams
		for (String word : cleanWords)
		{
			if (!existsInCitiesStates(word) && !existsInStopWords(word))
			{
				grams.add(word);
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
				boolean stopWordFlag = true;
				while (k<gramSize)
				{
					sb.append(cleanWords.get(j+k));
					if (stopWordFlag && !existsInStopWords(cleanWords.get(j+k)))
						stopWordFlag = false;
					sb.append(" ");
					k++;
				}
				if (!stopWordFlag)
					grams.add(sb.toString().trim());
			}
			gramSize++;
		}

		return grams;
	}
	
	/************************************************************************
	 * Method to fill HashSet of states from predefined states.txt file
	 * @param statefile
	 *************************************************************************/
	public void fillStates()
	{
		this.states = new HashSet<String>();
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(stateFile));
			String line;
			br.readLine();
			while((line = br.readLine()) != null)
			{
				line = line.toLowerCase();
				line = line.replaceAll("\\s","");
				states.add(line);
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/************************************************************************
	 * Method to fill HashSet of cities from predefined cities.txt file
	 * @param cityfile
	 *************************************************************************/
	public void fillCities()
	{
		this.cities = new HashSet<String>();
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(cityFile));
			String line;
			br.readLine();
			while((line = br.readLine()) != null)
			{
				line = line.toLowerCase();
				line = line.replaceAll("\\s","");
				cities.add(line);
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	/*******************************************************************************
	 * 
	 * Checks if the n-gram we have is a city or state or country name
	 * @param location
	 * @return
	 ********************************************************************************/
	boolean existsInCitiesStates(String gram)
	{
		boolean exists = false;
		String word = gram.toLowerCase();
		if (states.contains(word) || cities.contains(word))
		{
			exists = true;
		}
		return exists;		
	}
	
	/************************************************************************
	 * Method to fill HashSet of stop words from predefined stop_words.txt file
	 * @param stop_words_file
	 *************************************************************************/
	public void fillStopWords()
	{
		this.stopwords = new HashSet<String>();
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(stopWordsFile));
			String line;
			while((line = br.readLine()) != null)
			{
				line = line.toLowerCase();
				line = line.replaceAll("\\s","");
				stopwords.add(line);
			}
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	/*******************************************************************************
	 * 
	 * Checks if the location entity we have is a city or state or country name
	 * @param location
	 * @return
	 ********************************************************************************/
	boolean existsInStopWords(String gram)
	{
		boolean exists = false;
		String word = gram.toLowerCase();
		if (stopwords.contains(word))
		{
			exists = true;
		}
		return exists;		
	}

}
