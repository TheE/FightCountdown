package de.minehattan.eduardbaer.FightCountdown;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * FightCountdown for Bukkit
 * 
 * @author EduardBaer, TheE
 */
public class FightCountdown extends JavaPlugin {
	private boolean lightning;
	private Location lightCoords;
	private int count, runs, cdTask;
	private File configFile;
	private FileConfiguration tournaments = null;
	private File tournamentsFile = null;
	private ArrayList<Tournament> t;
	private Calendar dn;

	@Override
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is disabled!");
	}

	@Override
	public void onEnable() {
		configFile = new File(getDataFolder(), "config.yml");
		tournamentsFile = new File(getDataFolder(), "tournaments.yml");

		createConfigs();
		loadTournaments();

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is enabled!");
	}

	private void createConfigs() {
		if (!getDataFolder().exists()) {
			try {
				getDataFolder().mkdir();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		
		if (!tournamentsFile.exists()) {
			try {
				tournamentsFile.createNewFile();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			getTournaments().options().copyDefaults(true);
			saveTournaments();
		}
		getTournaments();
	}
	
	private void loadTournaments(){
		t = new ArrayList<Tournament>();
		for(String key : tournaments.getConfigurationSection("tournaments").getKeys(false)){
			Tournament tmpTour = new Tournament(key, tournaments.getString("tournaments."+key+".arena"), tournaments.getString("tournaments."+key+".host"));
			t.add(tmpTour);
		}
	
		dn = Calendar.getInstance();
		dn.set(Calendar.MILLISECOND, 0);
		String[] schedule =getConfig().getString("announcementSchedule").split(", ");
		for (int i = 0; i < t.size(); i++){
			for (int o = 0; o < schedule.length; o++){
				if (t.get(i).isAfter(dn, Integer.parseInt(schedule[o]))){
					final String time = t.get(i).getTime();
					final String arena = t.get(i).getArena();
					final String host = t.get(i).getHost();
					this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
								public void run() {
									broadcast(getConfig().getString("announceTournament").replaceAll("%time",time).replaceAll("%arena",arena).replaceAll("%host", host));
								}
							},
							20L * t.get(i).secondsFrom(dn, Integer.parseInt(schedule[o])));
				}
				dn.set(Calendar.MILLISECOND, 0);
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command command,
		String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();

		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (commandName.equals("fight")) {
				if (args.length == 0) {
					return false;
				}

				else if (args[0].equals("reload")) {
					if (!hasPersmission(player, command, args, "reload")) {
						return true;
					}
					reloadConfig();
					reloadTournaments();
					loadTournaments();
					send(player, getConfig().getString("reloadMessage"));
					return true;
				}

				else if (args[0].equals("help")) {
					send(player, "FightCountdown help page");
					send(player, "/fight help - this page");
					send(player, "/fight dice - chooses between stone sword and bow");
					send(player, "/fight next - gives you the details when the next tournament takes place.");
					send(player, "/fight set [-l] [seconds] - to set up a countdown");
					send(player, "/fight break - to stop the countdown");
					return true;
				}

				else if (args[0].equals("dice")) {
					if (!hasPersmission(player, command, args, "dice")) {
						return true;
					}
					if (Math.random() < 0.5) {
						broadcast(getConfig().getString("dice").replaceAll("%weapon", getConfig().getString("diceBow")));
					} else {
						broadcast(getConfig().getString("dice").replaceAll("%weapon", getConfig().getString("diceSword")));
					}
					return true;
				}

				else if (args[0].equals("next")) {
						if (!hasPersmission(player, command, args, "next")) {
							return true;
						}
						int pointer = -1;;
						for (int i = 0; i < t.size(); i++){
							if (t.get(i).isAfter(dn)){
								if (pointer == -1){
									pointer = i;
								} else if (t.get(i).isBefore(t.get(pointer).getFullDate())){
									pointer = i;
								}
							}
						}
						if (pointer == -1){
							send (player, getConfig().getString("nextMessageNone"));
							return true;
						}
						 send (player, getConfig().getString("nextMessage").replaceAll("%time", t.get(pointer).getDate()).replaceAll("%arena", t.get(pointer).getArena()).replaceAll("%host", t.get(pointer).getHost()));
						
						return true;
				}

				else if (args[0].equals("set")) {
					if (!hasPersmission(player, command, args, "set")) {
						return true;
					}
					if (args.length == 1) {
						count = getConfig().getInt("defaultCount");
					} if (args.length == 2){
						if (args[1].equals("-l") && hasPersmission(player, command, args, "set.lightning")) {
							count = getConfig().getInt("defaultCount");
							lightning = true;
							lightCoords = player.getTargetBlock(null, 40).getLocation();
						} else {
							try {
								count = Integer.valueOf(args[1]).intValue();
							} catch (NumberFormatException e) {
								return false;
							}
						}
					} if (args.length == 3){
						if (args[1].equals("-l") && hasPersmission(player, command, args, "set.lightning")) {
							lightning = true;
							lightCoords = player.getTargetBlock(null, 40).getLocation();
							try {
								count = Integer.valueOf(args[2]).intValue();
							} catch (NumberFormatException e) {
								return false;
							}
						} else {
							return false;
						}
				}
					if (getConfig().getInt("maximumCount") != 0 && count > getConfig().getInt("maximumCount")) {
						count = getConfig().getInt("maximumCount");
					}
					broadcast(getConfig().getString("startCountdown"));
					runs = 0;
					cdTask = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
								public void run() {
									if (runs == count) {
										broadcast(getConfig().getString("startFight"));
										if (lightning){
											lightCoords.getWorld().strikeLightningEffect(lightCoords);
										}
										stopTimer(cdTask);
									} else {
										broadcast(count-runs + "...");
									}
									runs++;
								}
							}, 0, 20L);
					return true;
				}

				else if (args[0].equals("break") && args.length == 1) {
					if (!hasPersmission(player, command, args, "break")) {
						return true;
					}
					stopTimer(cdTask);
					send(player, getConfig().getString("breakMessage"));
					return true;
				}
				return false;
			} // check if fight
		} // check if player
		return true;
	}

	/**
	 * Sends a message to every online player and into the server console
	 * 
	 * @param text the message
	 */
	public void broadcast(String text) {
		getServer().broadcastMessage("[FC] " + ChatColor.AQUA + text);
	}

	/**
	 * Sends a message to a player
	 * 
	 * @param player recipient of the message
	 * @param text the message
	 */
	public void send(Player player, String text) {
		player.sendMessage(ChatColor.AQUA + text);
	}

	public boolean hasPersmission(Player player, Command command,
			String[] args, String perm) {
		if (player.hasPermission("fightcountdown." + perm)) {
			return true;
		}
		send(player, ChatColor.RED + 
				getConfig().getString("needPermission").replace("%command", "/fight "
						+ arrayToString(args)));
		System.out.println(player.getDisplayName()
				+ " issued server command: /fight " + arrayToString(args));
		return false;
	}
	
	public void stopTimer(int task) {
		this.getServer().getScheduler().cancelTask(task);
	}

	public static String arrayToString(String[] a) {
		String separator = " ";
		StringBuffer result = new StringBuffer();
		if (a.length > 0) {
			result.append(a[0]);
			for (int i = 1; i < a.length; i++) {
				result.append(separator);
				result.append(a[i]);
			}
		}
		return result.toString();
	}
	
	public void reloadTournaments() {
		if (tournamentsFile == null) {
			tournamentsFile = new File(getDataFolder(), "tournaments.yml");
			tournaments.options().pathSeparator(',');
		}
		tournaments = YamlConfiguration.loadConfiguration(tournamentsFile);

		// Look for defaults in the jar
		InputStream defConfigStream = this.getResource("tournaments.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration
					.loadConfiguration(defConfigStream);
			tournaments.setDefaults(defConfig);
		}
	}

	public FileConfiguration getTournaments() {
		if (tournaments == null) {
			this.reloadTournaments();
		}
		return tournaments;
	}

	public void saveTournaments() {
		if (tournaments == null || tournamentsFile == null) {
			return;
		}
		try {
			tournaments.save(tournamentsFile);
		} catch (IOException ex) {
			this.getLogger().log(Level.SEVERE,
					"Could not save config to " + tournamentsFile, ex);
		}
	}

}
