package de.minehattan.eduardbaer.FightCountdown;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	static final Comparator<Tournament> Date_Order = new Comparator<Tournament>() {
		public int compare(Tournament a, Tournament b) {
			return a.getTournamentDate().compareTo(b.getTournamentDate());
		}
	};

	@Override
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTasks(this);
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
	}

	@Override
	public void onEnable() {
		configFile = new File(getDataFolder(), "config.yml");
		tournamentsFile = new File(getDataFolder(), "tournaments.yml");

		createConfigs();
		loadTournaments();

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
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
		}
		getConfig().options().copyDefaults(true);
		saveConfig();

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

	private void loadTournaments() {
		t = new ArrayList<Tournament>();
		for (String key : tournaments.getConfigurationSection("tournaments").getKeys(false)) {
			Tournament tmpTour = new Tournament(this, key, tournaments.getString("tournaments." + key + ".arena"), tournaments.getString("tournaments." + key
					+ ".host"));
			if (tmpTour.isAfter(0)) {
				t.add(tmpTour);
			}
		}
		Collections.sort(t, Date_Order);

		if (getConfig().getBoolean("cleanTournaments")) {
			tournaments.set("tournaments", null);
			for (int i = 0; i < t.size(); i++) {
				getTournaments().set("tournaments." + t.get(i).getUnformatedDate() + ".arena", t.get(i).getArena());
				getTournaments().set("tournaments." + t.get(i).getUnformatedDate() + ".host", t.get(i).getHost());
			}
			saveTournaments();
		}
		for (int i = 0; i < t.size(); i++) {
			t.get(i).startScheduler();
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();

		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (commandName.equals("tournament")) {
				if (args.length == 0) {
					return false;
				} else if (args[0].equals("next")) {
					if (!player.hasPermission("fc.tournament.next")) {
						return true;
					}
					updateTournaments();
					if (t.isEmpty()) {
						send(player, getConfig().getString("noTournaments"));
					} else {
						send(player, getConfig().getString("nextTournament").replaceAll("%time", t.get(0).getDate()).replaceAll("%arena", t.get(0).getArena())
								.replaceAll("%host", t.get(0).getHost()));
					}
					return true;
				} else if (args[0].equals("list")) {
					if (!player.hasPermission("fc.tournament.list")) {
						return true;
					}
					if (t.isEmpty()) {
						send(player, getConfig().getString("noTournaments"));
						return true;
					}
					if (args.length == 1) {
						updateTournaments();
						if (t.isEmpty()) {
							send(player, getConfig().getString("noTournaments"));
							return true;
						}
						int pages = (int) Math.ceil((double) (t.size()) / 8);
						send(player,
								ChatColor.GOLD
										+ getConfig().getString("tournamentList").replaceFirst("%page", "1").replaceAll("%totalPages", String.valueOf(pages)));
						for (int i = 0; i < 8; i++) {
							if (i < t.size()) {
								send(player,
										getConfig().getString("tournamentListMask").replaceAll("%date", t.get(i).getDate())
												.replaceAll("%arena", t.get(i).getArena()).replaceAll("%host", t.get(i).getHost()));
							}
						}
						return true;
					}
					if (args.length == 2 && isInteger(args[1])) {
						updateTournaments();
						if (t.isEmpty()) {
							send(player, getConfig().getString("noTournaments"));
							return true;
						}
						int pages = (int) Math.ceil((double) (t.size()) / 8);

						if (Integer.parseInt(args[1]) > pages) {
							send(player, getConfig().getString("noPage").replaceAll("%page", args[1]));
							return true;
						}

						send(player,
								ChatColor.GOLD
										+ getConfig().getString("tournamentList").replaceAll("%page", args[1]).replaceAll("%totalPages", String.valueOf(pages)));
						for (int i = Integer.parseInt(args[1]) * 8 - 7; i < Integer.parseInt(args[1]) * 8; i++) {
							System.out.println("i: " + i);
							if (i < t.size()) {
								send(player, t.get(i).getDate() + ", " + t.get(i).getArena() + ", " + t.get(i).getHost());
								return true;
							}
						}
					}
					return false;
				} else if (args[0].equals("help")) {
					send(player, ChatColor.GOLD + "======FightCountdown help page - /tournament======");
					send(player, "/tournament help - this page");
					send(player, "/tournament next - show you details abaout the next tournament");
					send(player, "/tournament list [#] - list future tournaments");
					return true;
				} else if (args[0].equals("reload")) {
					if (!player.hasPermission("fc.tournament.reload")) {
						return true;
					}
					reloadAllTournaments();
					send(player, getConfig().getString("reloadMessage"));
					return true;
				}
				return false;
			}
			if (commandName.equals("fight")) {
				if (args.length == 0) {
					return false;
				} else if (args[0].equals("reload")) {
					if (!player.hasPermission("fc.reload")) {
						return true;
					}
					reloadConfig();
					send(player, getConfig().getString("reloadMessage"));
					return true;
				} else if (args[0].equals("help")) {
					send(player, ChatColor.GOLD + "======FightCountdown help page - /fight======");
					send(player, "/fight help - this page");
					send(player, "/fight dice - chooses between stone sword and bow");
					send(player, "/fight next - gives you the details when the next tournament takes place.");
					send(player, "/fight set [-l] [seconds] - to set up a countdown");
					send(player, "/fight break - to stop the countdown");
					return true;
				} else if (args[0].equals("dice")) {
					if (!player.hasPermission("fc.fight.dice")) {
						return true;
					}
					broadcast(getConfig().getString("diceMessage").replaceAll("%weapon",
							(String) getConfig().getList("dice").get((int) ((Math.random() * getConfig().getList("dice").size())))));
					return true;
				} else if (args[0].equals("set")) {
					if (!player.hasPermission("fc.fight.set")) {
						return true;
					}
					if (args.length == 1) {
						count = getConfig().getInt("defaultCount");
					}
					if (args.length == 2) {
						if (args[1].equals("-l") && player.hasPermission("fc.fight.set.lightning")) {
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
					}
					if (args.length == 3) {
						if (args[1].equals("-l") && player.hasPermission("fc.fight.set.lightning")) {
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
					if (count < 0) {
						send(player, getConfig().getString("wrongCountdown").replaceAll("%maximumCount", Integer.toString(getConfig().getInt("maximumCount"))));
						return true;
					}

					if (count > getConfig().getInt("maximumCount") && (!player.hasPermission("fc.fight.set.bypass"))) {
						send(player, getConfig().getString("wrongCountdown").replaceAll("%maximumCount", Integer.toString(getConfig().getInt("maximumCount"))));
						return true;
					}

					if (this.getServer().getScheduler().isCurrentlyRunning(cdTask) || this.getServer().getScheduler().isQueued(cdTask)) {
						send(player, getConfig().getString("countdownRunning"));
						return true;
					}
					broadcast(getConfig().getString("startCountdown"));
					runs = 0;
					cdTask = this.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
						public void run() {
							if (runs == count) {
								broadcast(getConfig().getString("startFight"));
								if (lightning) {
									lightCoords.getWorld().strikeLightningEffect(lightCoords);
								}
								stopTimer(cdTask);
							} else {
								broadcast(count - runs + "...");
							}
							runs++;
						}
					}, 0, 20L);
					return true;
				} else if (args[0].equals("break") && args.length == 1) {
					if (!player.hasPermission("fc.fight.break")) {
						return true;
					}
					stopTimer(cdTask);
					send(player, getConfig().getString("breakMessage"));
					return true;
				}
				return false;
			}
		} else {
			if (args[0].equals("break") && args.length == 1) {
				stopTimer(cdTask);
				sender.sendMessage(ChatColor.AQUA + getConfig().getString("breakMessage"));
				return true;
			}
			if (args[0].equals("reload")) {
				reloadConfig();
				reloadAllTournaments();
				sender.sendMessage(ChatColor.AQUA + getConfig().getString("reloadMessage"));
				return true;
			}
			return true;
		}
		return false;
	}

	/**
	 * Sends a message to every online player and into the server console
	 * 
	 * @param text
	 *            the message
	 */
	public void broadcast(String text) {
		getServer().broadcastMessage("[FC] " + ChatColor.AQUA + text);
	}

	/**
	 * Sends a message to a player
	 * 
	 * @param player
	 *            recipient of the message
	 * @param text
	 *            the message
	 */
	public void send(Player player, String text) {
		player.sendMessage(ChatColor.AQUA + text);
	}

	public void stopTimer(int task) {
		this.getServer().getScheduler().cancelTask(task);
	}

	public void reloadAllTournaments() {
		for (int i = 0; i < t.size(); i++) {
			t.get(i).killTasks();
		}
		reloadTournaments();
		loadTournaments();
	}

	public void updateTournaments() {
		if (!t.isEmpty()) {
			boolean updated = false;
			while (!updated) {
				if (t.get(0).isAfter(0)) {
					updated = true;
				} else {
					t.remove(0);
					if (getConfig().getBoolean("cleanTournaments")) {
						tournaments.set("tournaments", null);
						for (int i = 0; i < t.size(); i++) {
							getTournaments().set("tournaments." + t.get(i).getUnformatedDate() + ".arena", t.get(i).getArena());
							getTournaments().set("tournaments." + t.get(i).getUnformatedDate() + ".host", t.get(i).getHost());
						}
						saveTournaments();
					}
				}

			}
		}

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

	public boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}

	public void reloadTournaments() {
		if (tournamentsFile == null) {
			tournamentsFile = new File(getDataFolder(), "tournaments.yml");
		}
		tournaments = YamlConfiguration.loadConfiguration(tournamentsFile);

		InputStream defConfigStream = this.getResource("tournaments.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
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
			this.getLogger().log(Level.SEVERE, "Could not save config to " + tournamentsFile, ex);
		}
	}
}