package de.minehattan.eduardbaer.FightCountdown;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Config extends Properties {
	static final long serialVersionUID = 0L;
	static final Logger log = Logger.getLogger("minecraft");
	private String fileName;

	public Config(String configFile) {
		this.fileName = configFile;
	}

	public void load() {
		File configFile = new File(this.fileName);
		if(configFile.exists()) {
			try {
				load(new FileInputStream(this.fileName));
			} catch (IOException e) {
				log.log(Level.SEVERE, "Unable to load " + this.fileName, e);
			}
		}
	}
	
	public void save(String start) {
		try {
			store(new FileOutputStream(this.fileName), start);
		} catch (IOException e) {
			log.log(Level.SEVERE, "Unable to save " + this.fileName, e);
		}
	}
	
	public int getInteger(String key, int value) {
		if (containsKey(key)) {
			return Integer.parseInt(getProperty(key));
		}
		
		put(key, String.valueOf(value));
		return value;
	}
	
	public double getDouble(String key, double value) {
		if (containsKey(key)) {
			return Double.parseDouble(getProperty(key));
		}
		
		put(key, String.valueOf(value));
		return value;
	}
	
	public String getString(String key, String value) {
		if (containsKey(key)) {
			return getProperty(key);
		}
		
		put(key, value);
		return value;
	}
	
	public boolean getBoolean(String key, boolean value) {
		if (containsKey(key)) {
			String boolString = getProperty(key);
			return (boolString.length() > 0) && (boolString.toLowerCase().charAt(0) == 't');
		}
		
		put(key, value ? "true" : "false");
		return value;
	}
	
	

}
