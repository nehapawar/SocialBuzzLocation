package com.uiuc.socialbuzz.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class CityExtractor {
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new File("config\\city.txt"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("config\\cities.txt")));
		in.nextLine();
		
		while(in.hasNext())
		{
			String line = in.nextLine();
			String[] array = line.split(" ");
			String city = array[0].substring(3);
			bw.write(city+"\n");
			
		}
		bw.close();
	}

}
