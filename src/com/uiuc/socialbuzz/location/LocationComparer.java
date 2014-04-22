package com.uiuc.socialbuzz.location;

import java.util.Comparator;

public class LocationComparer implements Comparator<Location> {

	@Override
	public int compare(Location l1, Location l2) {
		
		return (l1.score > l2.score) ? -1 : 1;
	}

}
