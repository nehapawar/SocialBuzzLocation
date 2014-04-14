package com.uiuc.socialbuzz.classifier;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class NaiveBayes {
	
	HashMap<String, Double[]> prob = new HashMap<String, Double[]>();
	int totalSamples = 0;
	
	NaiveBayes()
	{
		try {
			createTrainingData();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	void createTrainingData() throws IOException
	{
		//read file
		readDatasetFile();
		
		//generate ngrams
		
		//for each gram, go to hashmap and increment appropriate counter, P or N
		
		//
	}
	
	void readDatasetFile() throws IOException
	{
		Scanner in = new Scanner(new File("D:\\UIUC\\Spring 2014\\Prof Kevin Chang\\Empirical Study\\local copies\\test1.csv"));
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data.txt")));
		char label ;
		String tweet = "";
		in.nextLine();
		while(in.hasNext())
		{
			String line = in.nextLine();
			
			System.out.println(line);
			
			/*int comma = line.indexOf(',');
			label = comma==0 ? 'N':'Y';
			
			if (line.charAt(comma+1)=='"')
			{
				int quote = line.indexOf('"', comma+2);
				while (line.charAt(quote+1)=='"')
				{
					quote = line.indexOf('"', quote+2);
				}
				tweet = line.substring(comma+2, quote-1);
			}
			else
			{	
				int nextComma = line.indexOf(',', comma+1);
				if (nextComma<comma+1)
					nextComma = line.length();
				tweet = line.substring(comma+1, nextComma-1);
			}
			System.out.println("writing");
			bw.write(label+"\t"+tweet+"\n");
		*/}
		bw.close();
	}
	
}
