package de.minehattan.eduardbaer.FightCountdown;

public class LoadConfig {
	static boolean usePermissions;
	static int maxCount;
	
	public static void loadMain() {
		String configFile = FightCountdown.maindir + "plugin.properties";
		
		Config config = new Config(configFile);
		config.load();
		
		usePermissions = config.getBoolean("usePermissions", true);
		maxCount = config.getInteger("maximumCountdown", 0);
		
		config.save("FightCountdown configuration");
		
	}

}
