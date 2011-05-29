package de.xcraft.engelier.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class Configuration {
	private File configFile = null;
	private Map<String, Object> config = new HashMap<String, Object>();
	
	@SuppressWarnings("unchecked")
	public void load(String path, String filename) {
		String fullFileName = path + File.separator + filename;
		configFile = new File(fullFileName);
		
		try {
			if (!configFile.exists())
				configFile.createNewFile();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		Yaml yaml = new Yaml();
		try {
			config = (Map<String, Object>) yaml.load(new FileInputStream(configFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	public void save() {
		Yaml yaml = new Yaml();
		String dump = yaml.dump(config);
		try {
			FileOutputStream fh = new FileOutputStream(configFile);
			new PrintStream(fh).println(dump);
			fh.flush();
			fh.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Object getNode(String path) {
		Integer pathI = null;
		Object val = null;
		
		if (path.matches("^[0-9]+$"))
			pathI = new Integer(path);
		
        if (!path.contains("/")) {
        	if (pathI != null)
        		val = config.get(pathI);
        	else
        		val = config.get(path);
        	
        	if (val == null)
        		return null;

        	return val;
        }

        String[] parts = path.split("\\/");
        Map<String, Object> node = config;
        
        for (int i = 0; i < parts.length; i++) {
        	String thisPart = parts[i];
        	Integer thisPartI = null;
        	Object o = null;

        	if (thisPart.matches("^[0-9]+$")) {
    			thisPartI = new Integer(thisPart);
        		o = node.get(thisPartI);
        	} else {
        		o = node.get(thisPart);        		
        	}
       
        	if (o == null) {
        		return null;
        	}
        	
        	if (i == parts.length - 1) {
        		return o;
        	}
        	
        	try {
        		node = (Map<String, Object>)o;
        	} catch (ClassCastException e) {
        		return null;
        	}
        }
        
        return null;
	}
	
	@SuppressWarnings("unchecked")
	public void setNode(String path, Object value) {
	    if (!path.contains("/")) {
	    	config.put(path, value);
	    	return;
	    }

	    String[] parts = path.split("\\/");
	    
	    Map<String, Object> node = config;
	  
	    for (int i = 0; i < parts.length; i++) {
        	String thisPart = parts[i];
        	Integer thisPartI = null;
        	Object o = null;

        	if (thisPart.matches("^[0-9]+$")) {
    			thisPartI = new Integer(thisPart);
        		o = node.get(thisPartI);
        	} else {
        		o = node.get(thisPart);        		
        	}
       
            // Found our target!
	    	if (i == parts.length - 1) {
	    		node.put(thisPart, value);
	    		return;
	    	}
	    	
	    	if (o == null || !(o instanceof Map)) {
	    		// This will override existing configuration data!
	    		o = new HashMap<String, Object>();
	    		node.put(thisPart, o);
	    	}

	    	node = (Map<String, Object>)o;
	    }
	}

	@SuppressWarnings("unchecked")
	public List<String> getKeys(String path) {
		List<String> retVal = new ArrayList<String>();
		Map<String, Object> node = config;
		
		if (path != null)
			node = (Map<String, Object>) getNode(path);
		
		if (node == null)
			return null;
		
		for (Map.Entry<String, Object> entry: node.entrySet()) {
			Object key = entry.getKey();
						
			if (key instanceof String)
				retVal.add((String)key);
			
			if (key instanceof Integer)
				retVal.add(((Integer)key).toString());
		}
		
		return retVal;
	}
		
	public String getString(String path, String def) {
		Object check = getNode(path);
    	
		if (check == null) {
			setNode(path, def);
			return def;
		} else {
			return check.toString();
		}
	}
	
	public Integer getInt(String path, Integer def) {
		Integer check = castInt(getNode(path));
	
		if (check == null) {
			setNode(path, def);
			return def;
		} else {
			return check;
		}
	}
	
	public Boolean getBoolean(String path, Boolean def) {
		Boolean check = castBoolean(getNode(path));
		
		if (check == null) {
			setNode(path, def);
			return def;
		} else {
			return check;
		}
	}
	
	private static Integer castInt(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Byte) {
			return (int)(Byte)o;
		} else if (o instanceof Integer) {
			return (Integer)o;
		} else if (o instanceof Double) {
			return (int)(double)(Double)o;
		} else if (o instanceof Float) {
			return (int)(float)(Float)o;
		} else if (o instanceof Long) {
			return (int)(long)(Long)o;
		} else {
			return null;
		}
	}
	
	private static Boolean castBoolean(Object o) {
		if (o == null) {
			return null;
		} else if (o instanceof Boolean) {
			return (Boolean)o;
		} else {
			return null;
		}
	}
}
