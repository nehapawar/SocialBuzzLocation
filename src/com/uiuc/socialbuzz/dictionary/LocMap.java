package com.uiuc.socialbuzz.dictionary;

//import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
//import java.io.FileWriter;
//import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.String;

public class LocMap {
	HashMap<Long,Double[]> nodeMap;
	HashMap<Long,Double[]> wayMap;
	//HashMap<Long,Double[]> relMap;
	ArrayList<Location> loc_list;
	public LocMap(){
		nodeMap = new HashMap<Long, Double[]>();
		wayMap = new HashMap<Long, Double[]>();
		//relMap = new HashMap<Long, Double[]>();
		loc_list = new ArrayList<Location>();
	}
	public enum WayType {
		HIGHWAY, BUILDING
	}
	/* The getSubstr function gives the substring in the line between the prefix  
	 * and the suffix (prefix and suffix are not included) */
	public static String getSubstr(String line, String prefix,String suffix){
		int pos = line.indexOf(prefix);
		if (pos==-1)
			return null;
		pos += prefix.length();
		int end = line.indexOf(suffix);
		String result = new String(line.substring(pos, end));
		return result;
	}
	public static void main(String[]args) throws FileNotFoundException        
    { 
		LocMap locmap = new LocMap();       
	
	    File file = new File("xapi_meta");
	    Scanner in = new Scanner(file);
	    String line;
	    /*** Prefix strings of the values we want to extract from the file ***/
	    String node_id_prefix = "<node id=\"";
    	String way_id_prefix = "<way id=\"";
    	String rel_id_prefix = "<relation id=\"";
    	String name_prefix = "<tag k=\"name\" v=\"";
    	String official_name_prefix = "<tag k=\"official_name\" v=\"";
    	String short_name_prefix = "<tag k=\"short_name\" v=\"";
    	String amenity_prefix = "<tag k=\"amenity\" v=\"";
    	String place_prefix = "<tag k=\"place\" v=\"";
    	String cuisine_prefix = "<tag k=\"cuisine\" v=\"";
    	String shop_prefix = "<tag k=\"shop\" v=\"";
    	String housenumber_prefix = "<tag k=\"addr:housenumber\" v=\"";
    	String street_prefix = "<tag k=\"addr:street\" v=\"";
    	String city_prefix = "<tag k=\"addr:city\" v=\"";
    	String phone_prefix = "<tag k=\"phone\" v=\"";
    	String opening_hours_prefix = "<tag k=\"opening_hours\" v=\"";
    	String website_prefix = "<tag k=\"website\" v=\"";
    	String highway_prefix = "<tag k=\"highway\"";
    	String building_prefix = "<tag k=\"building\""; 
    	String value_suffix = "\"/>";
    	DBWriter dbw = new DBWriter();
	    while(in.hasNextLine())
        {	    	
	    	line = in.nextLine();	    	
	    	int node_id_pos;
	    	int way_id_pos;
	    	/* check if the current line is about a node */
	    	if ((node_id_pos = line.indexOf(node_id_prefix))>-1){
	    		node_id_pos += node_id_prefix.length();
	    		int id_end = line.indexOf("\" lat");
	    		/* Get id, latitude and longitude from the file*/
	    		String id_str = line.substring(node_id_pos, id_end);
		    	String lat_str = getSubstr(line,"lat=\"","\" lon");
		    	String lon_str = getSubstr(line,"lon=\"","\" version");
		    	//System.out.println("id="+id_str+" lat="+lat_str+" lon="+lon_str);
		    	Long id = Long.valueOf(id_str);
		    	Double lat = Double.valueOf(lat_str);
		    	Double lon = Double.valueOf(lon_str);
		    	Double [] coord = new Double[]{lat,lon};		    	
		    	locmap.nodeMap.put(id,coord);
		    	/* check if the current line ends with "/>", if not there are more
		    	 * information about the current node in the lines below the current line */
		    	if (line.indexOf("/>")==-1){		    		
		    		Location loc = new Location();
		    		loc.coord = coord;
		    		loc.id = id;		    
		    		line = in.nextLine();
		    		/* get all the information about the current node until "</node>"
		    		 * is encountered
		    		 */
		    		while(line.indexOf("</node>")==-1){
		    		/* check if the current line contains certain type of value we want to
		    		 * extract, such as name, official_name and etc.
		    		 */
		    			if (line.indexOf(name_prefix)>-1)
		    				loc.name = getSubstr(line,name_prefix,value_suffix);
		    			if (line.indexOf(official_name_prefix)>-1)
		    				loc.official_name = getSubstr(line,official_name_prefix,value_suffix);
		    			if (line.indexOf(short_name_prefix)>-1)
		    				loc.short_name = getSubstr(line,short_name_prefix,value_suffix);
		    			if (line.indexOf(amenity_prefix)>-1)
		    				loc.amenity = getSubstr(line,amenity_prefix,value_suffix);
		    			if (line.indexOf(place_prefix)>-1)
		    				loc.place = getSubstr(line,place_prefix,value_suffix);
		    			if (line.indexOf(cuisine_prefix)>-1)
		    				loc.cuisine = getSubstr(line,cuisine_prefix,value_suffix);
		    			if (line.indexOf(shop_prefix)>-1)
		    				loc.shop = getSubstr(line,shop_prefix,value_suffix);
		    			if (line.indexOf(housenumber_prefix)>-1)
		    				loc.housenumber = getSubstr(line,housenumber_prefix,value_suffix);
		    			if (line.indexOf(street_prefix)>-1)
		    				loc.street = getSubstr(line,street_prefix,value_suffix);
		    			if (line.indexOf(city_prefix)>-1)
		    				loc.city = getSubstr(line,city_prefix,value_suffix);
		    			if (line.indexOf(phone_prefix)>-1)
		    				loc.phone = getSubstr(line,phone_prefix,value_suffix);
		    			if (line.indexOf(opening_hours_prefix)>-1)
		    				loc.opening_hours = getSubstr(line,opening_hours_prefix,value_suffix);
		    			if (line.indexOf(website_prefix)>-1)
		    				loc.website = getSubstr(line,website_prefix,value_suffix);
		    			line = in.nextLine();
		    		}
		    		if (loc.name != null){
		    			locmap.loc_list.add(loc);
		    			dbw.addLoc(loc);
		    		}
		    		//System.out.println(loc.amenity);
		    	}
	    	}
	    	/* check if the current line is about a way */
	    	else if ((way_id_pos = line.indexOf(way_id_prefix))>-1){
	    		way_id_pos += way_id_prefix.length();
	    		int id_end = line.indexOf("\" version");	    		
	    		String id_str = line.substring(way_id_pos, id_end);
		    	Long id = Long.valueOf(id_str);
		    	Location loc = new Location();
		    	Double lat = 0.0;
		    	Double lon = 0.0;
		    	Double [] coord = null;
		    	WayType way_type = WayType.HIGHWAY; 
		    	/* the ArrayList stores all the coordinates of nodes contained
		    	 * in the way
		    	 */
		    	ArrayList<Double[]> coord_list = new ArrayList<Double[]>();
		    	String nid_str;
		    	int num_node = 0;
		    	line = in.nextLine();
		    	/* get all the information about the current node until "</way>"
	    		 * is encountered
	    		 */
		    	while(line.indexOf("</way>")==-1){
		    		/* get the nodes' id referred in the lines in the way */ 
		    			nid_str = getSubstr(line,"<nd ref=\"",value_suffix);
		    			if (nid_str!=null){
		    				/* get the node's coordinates by looking up the nodemap
		    				 * by node's id
		    				 */
		    				coord = locmap.nodeMap.get(Long.valueOf(nid_str));		    				
		    				/* if there exists such node in the map, get the 
		    				 * coordinates of this node
		    				 */
		    				if (coord!=null){
			    				lat += coord[0];
			    				lon += coord[1];
			    				num_node++;
			    				coord_list.add(coord);
		    				}
		    				
		    			}   			
		    			/* check if the current line contains certain type of value we want to
			    		 * extract, such as name, official_name and etc.
			    		 */
		    			if (line.indexOf(name_prefix)>-1)
		    				loc.name = getSubstr(line,name_prefix,value_suffix);
		    			if (line.indexOf(official_name_prefix)>-1)
		    				loc.official_name = getSubstr(line,official_name_prefix,value_suffix);
		    			if (line.indexOf(short_name_prefix)>-1)
		    				loc.short_name = getSubstr(line,short_name_prefix,value_suffix);
		    			if (line.indexOf(amenity_prefix)>-1)
		    				loc.amenity = getSubstr(line,amenity_prefix,value_suffix);
		    			if (line.indexOf(place_prefix)>-1)
		    				loc.place = getSubstr(line,place_prefix,value_suffix);
		    			if (line.indexOf(cuisine_prefix)>-1)
		    				loc.cuisine = getSubstr(line,cuisine_prefix,value_suffix);
		    			if (line.indexOf(shop_prefix)>-1)
		    				loc.shop = getSubstr(line,shop_prefix,value_suffix);
		    			if (line.indexOf(housenumber_prefix)>-1)
		    				loc.housenumber = getSubstr(line,housenumber_prefix,value_suffix);
		    			if (line.indexOf(street_prefix)>-1)
		    				loc.street = getSubstr(line,street_prefix,value_suffix);
		    			if (line.indexOf(city_prefix)>-1)
		    				loc.city = getSubstr(line,city_prefix,value_suffix);
		    			if (line.indexOf(phone_prefix)>-1)
		    				loc.phone = getSubstr(line,phone_prefix,value_suffix);
		    			if (line.indexOf(opening_hours_prefix)>-1)
		    				loc.opening_hours = getSubstr(line,opening_hours_prefix,value_suffix);
		    			if (line.indexOf(website_prefix)>-1)
		    				loc.website = getSubstr(line,website_prefix,value_suffix);
		    			/* check if the current way's type is HIGHWAY */
		    			if (line.indexOf(highway_prefix)>-1)
		    				way_type = WayType.HIGHWAY;
		    			/* check if the current way's type is BUILDING */
		    			if (line.indexOf(building_prefix)>-1)
		    				way_type = WayType.BUILDING;
		    			line = in.nextLine();
		    		}
		    		switch (way_type){
		    		case HIGHWAY:
		    			/* if the current way's type is HIGHWAY, use the coordinates of the 
		    			 * node in the middle of the way as the way's coordinates*/
		    			coord = coord_list.get(num_node/2);
		    			loc.coord = coord;
		    			break;
		    		case BUILDING:
		    			/* if the current way's type is BUILDING, use the average coordinates 
		    			 *  of all the nodes in the way as the way's coordinates*/
		    			coord = new Double[]{lat/num_node,lon/num_node};
		    			loc.coord = coord;
		    			break;
		    		}
		    	locmap.loc_list.add(loc);
		    	dbw.addLoc(loc);
		    	locmap.wayMap.put(id,coord);
	    	}else{
		    	if ((line.indexOf(rel_id_prefix))>-1){
			    		Location loc = new Location();
			    		Double lat = 0.0;
			    		Double lon = 0.0;
			    		Double [] coord;
			    		String id_str;
			    		int num_coord = 0;
			    		line = in.nextLine();
			    		while(line.indexOf("</relation>")==-1){
			    			/* get the coordinates of way referred in the lines in the relation */ 
			    			if (line.indexOf("<member type=\"way\" ref=\"")>-1){
			    				id_str = getSubstr(line,"<member type=\"way\" ref=\"","\" role=");
			    				coord = locmap.wayMap.get(Long.valueOf(id_str));
			    				if (coord!=null){
				    				lat += coord[0];
				    				lon += coord[1];
				    				num_coord++;
			    				}
			    			}
			    			/* get the coordinates of node referred in the lines in the relation */ 
			    			if (line.indexOf("<member type=\"node\" ref=\"")>-1){
			    				id_str = getSubstr(line,"<member type=\"node\" ref=\"","\" role=");
			    				coord = locmap.nodeMap.get(Long.valueOf(id_str));
			    				if (coord!=null){
				    				lat += coord[0];
				    				lon += coord[1];
				    				num_coord++;
			    				}
			    			}
			    			if (line.indexOf(name_prefix)>-1)
			    				loc.name = getSubstr(line,name_prefix,value_suffix);
			    			if (line.indexOf(official_name_prefix)>-1)
			    				loc.official_name = getSubstr(line,official_name_prefix,value_suffix);
			    			if (line.indexOf(short_name_prefix)>-1)
			    				loc.short_name = getSubstr(line,short_name_prefix,value_suffix);
			    			if (line.indexOf(amenity_prefix)>-1)
			    				loc.amenity = getSubstr(line,amenity_prefix,value_suffix);
			    			if (line.indexOf(place_prefix)>-1)
			    				loc.place = getSubstr(line,place_prefix,value_suffix);
			    			if (line.indexOf(cuisine_prefix)>-1)
			    				loc.cuisine = getSubstr(line,cuisine_prefix,value_suffix);
			    			if (line.indexOf(shop_prefix)>-1)
			    				loc.shop = getSubstr(line,shop_prefix,value_suffix);
			    			if (line.indexOf(housenumber_prefix)>-1)
			    				loc.housenumber = getSubstr(line,housenumber_prefix,value_suffix);
			    			if (line.indexOf(street_prefix)>-1)
			    				loc.street = getSubstr(line,street_prefix,value_suffix);
			    			if (line.indexOf(city_prefix)>-1)
			    				loc.city = getSubstr(line,city_prefix,value_suffix);
			    			if (line.indexOf(phone_prefix)>-1)
			    				loc.phone = getSubstr(line,phone_prefix,value_suffix);
			    			if (line.indexOf(opening_hours_prefix)>-1)
			    				loc.opening_hours = getSubstr(line,opening_hours_prefix,value_suffix);
			    			if (line.indexOf(website_prefix)>-1)
			    				loc.website = getSubstr(line,website_prefix,value_suffix);
			    			line = in.nextLine();
			    		}
			    		/* coordinates of the relation is the average coordinates 
			    		 * of all the way and nodes in it
			    		 */
			    		coord = new Double[]{lat/num_coord,lon/num_coord};
			    		loc.coord = coord;
			    		locmap.loc_list.add(loc);
			    		dbw.addLoc(loc);
		    	}
		    	}
	    	
        }
	    /*
		try {
			File out_file = new File("location_list.txt");		     
			// if file doesnt exists, then create it
			if (!out_file.exists()) {
				out_file.createNewFile();
			}
			FileWriter fw = new FileWriter(out_file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("name\t official_name\t short_name\t amenity\t place\t"
					+ " cuisine\t shop\t housenumber\t street\t city\t phone\t opening_hours\t website\n");
			int i;
			Location loc;
			for (i=0;i < locmap.loc_list.size(); i++)
	        {       loc = locmap.loc_list.get(i);
	        		bw.write(loc.name+'\t'+loc.official_name+'\t'+loc.short_name+'\t'+loc.amenity
	        				+'\t'+loc.place+'\t'+loc.cuisine+'\t'+loc.shop+'\t'+loc.housenumber
	        				+'\t'+loc.street+'\t'+loc.city+'\t'+loc.phone+'\t'+loc.opening_hours+'\t'+loc.website+'\t'
	        				+Double.toString(loc.coord[0])+','+Double.toString(loc.coord[1])+'\n');
	        }
        	//else bw.write("[++FOUND++]"+annotate_line+'\n');
			bw.close();} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	*/
	    
	}

}
