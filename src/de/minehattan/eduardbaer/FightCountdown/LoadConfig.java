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
	static String txtStartCountdown;
	static String txtStartFight;
	static String txtBreak;
	static String txtAnnounceWinner;
	static String txtAnnounceFight;
	
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
		txtStartCountdown = config.getString("startCountdown", "Be ready, the fight starts in:");
		txtStartFight = config.getString("startFight", "Fight!");
		txtBreak = config.getString("break", "§cCountdown stopped");
		txtAnnounceWinner = config.getString("announceWinner", "%player winns the fight!");
		txtAnnounceFight = config.getString("announceFight", "%player1 and %player2 will fight!");
		
		config.save("FightCountdown text");
		
	}

}
