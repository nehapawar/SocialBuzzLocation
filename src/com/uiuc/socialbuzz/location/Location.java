package com.uiuc.socialbuzz.location;

import java.util.ArrayList;

public class Location 
{ 
	ArrayList<NGram> ngram = new ArrayList<NGram>();
	int id; 
	String name; 
	String official_name; 
	String short_name; 
	String amenity; 
	String place; 
	String cuisine; 
	String shop; 
	String housenumber; 
	String street; 
	String city; 
	String phone; 
	String opening_hours; 
	String website; 
	Double[] coord; 
	
	public int editDistance=Integer.MAX_VALUE;
	double score = 0;
	int hits = 1;
}