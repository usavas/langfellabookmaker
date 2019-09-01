package com.savas.maven_projects;

import java.util.EnumMap;

public enum Level {

	starter,
	elementary,
	preintermediate,
	intermediate,
	intermediateplus,
	upperintermediate,
	advanced,
	unabridged;
	
	public static String GetLevelString(Level level) {
		
		EnumMap<Level, String> map = new EnumMap<Level, String>(Level.class);
		
		map.put(starter, "starter");
		map.put(elementary, "elementary");
		map.put(preintermediate, "pre-intermediate");
		map.put(intermediate, "intermediate");
		map.put(intermediateplus, "intermediate-plus");
		map.put(upperintermediate, "upper-intermediate");
		map.put(advanced, "advanced");
		map.put(unabridged, "unabridged");
	
		return map.get(level);	
	}
}
