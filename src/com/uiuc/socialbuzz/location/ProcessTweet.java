package com.uiuc.socialbuzz.location;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessTweet {
	public HashSet<String> states;
	public HashSet<String> cities;
	public HashSet<String> stopwords;
	public HashSet<String> filterwords;
	public HashMap<String, Integer> tf;
	String cityFile = "config\\cities.txt"; 
	String stateFile = "config\\states.txt"; 
	String stopWordsFile = "config\\stop_words.txt";
	String filterFile = "config\\filter.txt";
	String tfFile = "config\\tf.txt";
	Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
	Matcher m = null;
	int MAX_GRAM_SIZE = 4;
	
	ProcessTweet()
	{
		
		//Create city and state hash sets
		fillCities();
		fillStates();
		
		//fill filter words
		fillFilter();
		
		//fill term frequency words
		fillTFWords();
		
		
		//Create stop words hash set
		fillStopWords();
		
	}
	
	ArrayList<String> generateNGrams(String tweet)
	{
		ArrayList<String> grams = new ArrayList<String>();

		//splits on spaces and on ....
		String[] allWords = tweet.split("\\s+|\\.\\.+");
		
		// Impose filter
		if (existsInFilterWords(allWords))
			return null;
		
		ArrayList<String> cleanWords = new ArrayList<String>();
		int n =0;
		
		//Process tweet to get clean words
		//remove URLs
		for (String word : allWords)
		{
			String addWord = word;
			//System.out.println("1"+ word);
			//remove urls
			if (addWord.startsWith("http:"))
			{
				continue;
			}
			//System.out.println("2"+ word);
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
			if (!existsInCitiesStates(word) 
					&& !existsInStopWords(word) 
					&& !isNumeric(word)
					//&& existsInTF(word)<100000
					)
			{
				grams.add(word);
				System.out.print(word+" ");
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
		//System.out.println(word+" "+states.contains(word)+" "+cities.contains(word));
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

	
	/************************************************************************
	 * Method to fill HashSet of filter words from predefined filter.txt file
	 * @param filterFile
	 *************************************************************************/
	public void fillFilter()
	{
		this.filterwords = new HashSet<String>();
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(filterFile));
			String line;
			br.readLine();
			while((line = br.readLine()) != null)
			{
				line = line.toLowerCase();
				line = line.replaceAll("\\s","");
				
				filterwords.add(line);
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
	 * Checks if the tweet has a filter word
	 ********************************************************************************/
	boolean existsInFilterWords(String[] tweetWords)
	{
		boolean exists = false;
		for (String word : tweetWords)
		{
			if (filterwords.contains(word))
			{
				exists = true;
				break;
			}
		}
		return exists;		
	}
	
	/************************************************************************
	 * Method to fill HashMap of terms and frequencies
	 * 
	 *************************************************************************/
	public void fillTFWords()
	{
		this.tf = new HashMap<String, Integer>();
		try 
		{
			BufferedReader br = new BufferedReader(new FileReader(tfFile));
			String line;

			while((line = br.readLine()) != null)
			{
				String[] array = line.split("\\s+");
				String term = array[0].substring(1);
				int freq = Integer.parseInt(array[1].trim());
				tf.put(term, freq);
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
	 * returns term frequency of word
	 ********************************************************************************/
	int existsInTF(String tweet)
	{
		if (tf.get(tweet)!=null)
			return tf.get(tweet);
		return 0;
	}
	
	
	public boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}
	
}
