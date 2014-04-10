package com.uiuc.socialbuzz.helper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TermExtractor {
	
	public static void main(String[] args) throws IOException {
		Scanner in = new Scanner(new File("D:\\UIUC\\Spring 2014\\Prof Kevin Chang\\Empirical Study\\term_df"));
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("tf.txt")));
		boolean start = false;
		while (in.hasNext())
		{
			String line = in.nextLine();
			if (!start && line.startsWith("#a")) start = true;
			if (start && line.startsWith("#"))
			{
				bw.write(line+"\n");
			}
		}
		bw.close();
	}

}
