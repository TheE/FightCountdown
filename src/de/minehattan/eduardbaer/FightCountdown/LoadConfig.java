package de.minehattan.eduardbaer.FightCountdown;

public class LoadConfig {
	static boolean usePermissions;
	static int maxCount;
	static int defaultCount;
	static String broadcastColor;
	static String messageColor;
	
	static String txtPermission;
	static String txtDice;
	static String txtDiceBow;
	static String txtDiceSword;
	static String txtClearNext;
	static String txtFightAnnounce;
	static String txtStartFight;
	static String txtBrake;
	
	public static void loadMain() {
		String configFile = FightCountdown.maindir + "plugin.properties";
		
		Config config = new Config(configFile);
		config.load();
		
		usePermissions = config.getBoolean("usePermissions", true);
		maxCount = config.getInteger("maximumCountdown", 0);
		defaultCount = config.getInteger("defaultCount", 5);
		broadcastColor = config.getString("broadcastColor", "ChatColor.AQUA");
		messageColor = config.getString("messageColor", "ChatColor.WHITE");
		
		config.save("FightCountdown configuration");

	}
	
	public static void loadText() {
		String textFile = FightCountdown.maindir + "messages.properties";
		
		Config config = new Config(textFile);
		config.load();
		
		txtPermission = config.getString("needPermission", "You don't have the permission to use %command!");
		txtDice = config.getString("dice", "Allowed weapon in this fight is %weapon!");
		txtDiceBow = config.getString("diceBow", "a bow");
		txtDiceSword = config.getString("diceSword", "an iron sword");
		txtClearNext = config.getString("clearMessage", "§cMessage removed!");
		txtFightAnnounce = config.getString("fightAnnounce", "Be ready, the fight starts in:");
		txtStartFight = config.getString("startFight", "Fight!");
		txtBrake = config.getString("brake", "§cCountdown stopped");
		
		config.save("FightCountdown text");
		
	}

}
