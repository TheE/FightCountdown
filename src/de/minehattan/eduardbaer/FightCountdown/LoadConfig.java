package de.minehattan.eduardbaer.FightCountdown;

public class LoadConfig {
	static boolean usePermissions;
	static int maxCount;
	static int defaultCount;
	
	public static void loadMain() {
		String configFile = FightCountdown.maindir + "plugin.properties";
		
		Config config = new Config(configFile);
		config.load();
		
		usePermissions = config.getBoolean("usePermissions", true);
		maxCount = config.getInteger("maximumCountdown", 0);
		defaultCount = config.getInteger("defaultCount", 5);
		
		config.save("FightCountdown configuration");
		
	}

}
