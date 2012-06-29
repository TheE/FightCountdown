package de.minehattan.eduardbaer.FightCountdown;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * FightCountdown for Bukkit
 * 
 * @author EduardBaer
 */
public class FightCountdown extends JavaPlugin {
	private boolean lightning;
	private Location lightCoords;
	private String next;
	private int count, runs, cdTask;
	private File configFile;

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
		next = "";

		createConfig();
		saveConfig();

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is enabled!");
	}

	private void createConfig() {
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
				getConfig().options().copyDefaults(true);
			} catch (Exception e) {
				e.printStackTrace();
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
					send(player, getConfig().getString("reloadMessage"));
					return true;
				}

				else if (args[0].equals("help")) {
					send(player, ChatColor.RED + "FightCountdown help page");
					send(player, ChatColor.AQUA + "/fight help - this page");
					send(player, ChatColor.AQUA + "/fight dice - chooses between iron sword and bow");
					send(player, ChatColor.AQUA + "/fight next - gives you the details of the next fight");
					send(player, ChatColor.AQUA + "/fight next <message> - sets the details of the next fight");
					send(player, ChatColor.AQUA + "/fight next clear - to delete the message");
					send(player, ChatColor.AQUA + "/fight set [seconds] - to set up a countdown");
					send(player, ChatColor.AQUA + "/fight break - to stop the countdown");
					return true;
				}

				else if (args[0].equals("dice")) {
					if (!hasPersmission(player, command, args, "dice")) {
						return true;
					}
					if (Math.random() < 0.5) {
						broadcast(getConfig().getString("dice").replace("%weapon", getConfig().getString("diceBow")));
					} else {
						broadcast(getConfig().getString("dice").replace("%weapon", getConfig().getString("diceSword")));
					}
					return true;
				}

				else if (args[0].equals("next")) {
					if (args.length == 2 && args[1].equals("clear")) {
						if (!hasPersmission(player, command, args, "next.clear")) {
							return true;
						}
						next = "";
						send(player, getConfig().getString("clearMessage"));
						System.out.println("[FC] " + player.getDisplayName()
								+ " removed next");
						return true;
					} else if (args.length >= 2) {
						if (!hasPersmission(player, command, args, "next.set")) {
							return true;
						}
						next = "";
						for (int i = 1; i < args.length; i++) {
							next = next.concat(args[i] + " ");
							next = next.replace("&&", "§");
						}
						send(player, next);
						System.out.println("[FC] " + player.getDisplayName()
								+ " sets next to: " + next);
						return true;
					} else {
						send(player, next);
						return true;
					}
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
		player.sendMessage(ChatColor.WHITE + text);
	}

	public boolean hasPersmission(Player player, Command command,
			String[] args, String perm) {
		if (player.hasPermission("fightcountdown." + perm)) {
			return true;
		}
		send(player,
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

}
