package com.uiuc.socialbuzz.svm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HashMapReader {

	public static void main(String[] args) throws FileNotFoundException {
		Scanner in = new Scanner(new File("D:\\UIUC\\Spring 2014\\Prof Kevin Chang\\Tweet Location Classifier\\hashmap.txt"));
		
		while (in.hasNext())
		{
			String line = in.nextLine();
			
			System.out.println(line);
			/*int lastSpace = line.lastIndexOf(' ') - 1;
			StringBuffer sb = new StringBuffer();
			sb.insert(0, "");
			do
			{
				
				sb.append(line.charAt(lastSpace));
				lastSpace--;
				
			}while (line.charAt(lastSpace)!=' ');
			
			sb = sb.reverse();
			int negative = Integer.parseInt(line.substring(line.lastIndexOf(' ')+1));
			int positive = Integer.parseInt(sb.toString());
			
			String phrase = line.substring(0, lastSpace);
			
			if (positive!=0)
				System.out.println(positive+" "+negative+" "+phrase);*/
		}
	}
}
