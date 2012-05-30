package de.minehattan.eduardbaer.FightCountdown;


import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
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
	private List<Player> player1;
	private List<Player> player2;
	private boolean runThread;
	private String announceFight, announceWinner, breakMessage, clearMessage,
			dice, diceBow, diceSword, reloadMessage, startCountdown, startFight,
			needPermission, next;
	private int count, defaultCount, maximumCount;

	private File pluginFolder;
	private File configFile;

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is disabled!");	}

	@Override
	public void onEnable() {
		pluginFolder = getDataFolder();
		configFile = new File (pluginFolder, "config.yml");
		next = "";

		createConfig();
		saveConfig();
		loadConfig();

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is enabled!");

	}
	private void createConfig() {
		if (!pluginFolder.exists()){
			try {
				pluginFolder.mkdir();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if (!configFile.exists()){
			try {
				configFile.createNewFile();
				getConfig().options().copyDefaults(true);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	private void loadConfig(){
		defaultCount = getConfig().getInt("defaultCount");
		maximumCount = getConfig().getInt("maximumCount");
		
		announceFight = getConfig().getString("announceFight");
		announceWinner = getConfig().getString("announceWinner");
		breakMessage = getConfig().getString("breakMessage");
		clearMessage = getConfig().getString("clearMessage");
		dice = getConfig().getString("dice");
		diceBow = getConfig().getString("diceBow");
		diceSword = getConfig().getString("diceSword");
		reloadMessage = getConfig().getString("reloadMessage");
		startCountdown = getConfig().getString("startCountdown");
		startFight = getConfig().getString("startFight");
		needPermission = getConfig().getString("needPermission");
	}

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String commandName = command.getName().toLowerCase();
		PluginDescriptionFile pdfFile = this.getDescription();

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
					loadConfig();
					send(player, reloadMessage);

					return true;

				}
				
				else if (args[0].equals("info")) {
					send(player, ChatColor.RED + pdfFile.getName() + " info page");
					send(player, ChatColor.AQUA + "A simple countdown plugin for arena fights.");
					send(player, ChatColor.AQUA + "Version: " + pdfFile.getVersion());
					send(player, ChatColor.AQUA + "Author: " + pdfFile.getAuthors());
					send(player, ChatColor.AQUA + "Website: " + pdfFile.getWebsite());

					return true;
				}

				else if (args[0].equals("help")) {
					send(player, ChatColor.RED + pdfFile.getName() + " help page");
					send(player, ChatColor.AQUA + "/fight help - this page");
					send(player, ChatColor.AQUA + "/fight info - for information about this plugin");
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
						broadcast(dice.replace("%weapon", diceBow));
					}
					else {
						broadcast(dice.replace("%weapon", diceSword));
					}

					return true;

				}

				else if (args[0].equals("next")) {
					if (args.length == 2 && args[1].equals("clear")) {
						if (!hasPersmission(player, command, args, "next.clear")) {
							return true;
						}

						next = "";

						send(player, clearMessage);

						System.out.println("[FC] " + player.getDisplayName() + " removed next");

						return true;

					}
					else if (args.length >= 2) {
						if (!hasPersmission(player, command, args, "next.set")) {
							return true;
						}

						next = "";

						for (int i = 1; i < args.length; i++) {
							next = next.concat(args[i] + " ");
							next = next.replace("&&", "§");
						}

						send(player, next);

						System.out.println("[FC] " + player.getDisplayName() + " sets next to: " + next);

						return true;

					}
					else {
						send(player, next);
						return true;
					}
				}

				else if (args[0].equals("set") && args.length <= 2) {

					if (!hasPersmission(player, command, args, "set")) {
						return true;
					}

					runThread = true;

					if (args.length == 1) {
						count = defaultCount;
					}
					else {
						try{
							count = Integer.valueOf(args[1]).intValue();
						} catch(NumberFormatException e) {return false;}
					}

					if (maximumCount != 0 && count > maximumCount) {
						count = maximumCount;
					}

					Thread counter = new Thread() {
						public void run() {
							broadcast(startCountdown);

							for (int i = count; i > 0; i--) {
								broadcast(i + "...");
								try{
									Thread.sleep(1000);
								} catch(InterruptedException e){}
								if (!(runThread)) {
									break;
								}
							}

							if (runThread)
								broadcast(startFight);
						}
					};

					counter.start();

					return true;

				}

				else if (args[0].equals("set") && args.length == 3) {

					if (!hasPersmission(player, command, args, "set")) {
						return true;
					}
					
					runThread = true;

					player1 = getServer().matchPlayer(args[1]);
					player2 = getServer().matchPlayer(args[2]);

					if (player1.size() == 1 && player2.size() == 1) {
						if (player1.get(0).getHealth() < 20) {
							player1.get(0).setHealth(20);
						}
						if (player2.get(0).getHealth() < 20) {
							player2.get(0).setHealth(20);
						}
					}
					else {
						send(player, "§cOne or both arguments are invalid.");
						return true;
					}
					
					broadcast(announceFight.replace("%player1", player1.get(0).getDisplayName()).replace("%player2", player2.get(0).getDisplayName()));

					Thread fight = new Thread() {
						public void run() {
							while(true) {
								if (runThread) {
									if (player1.get(0).getHealth() < 1) {
										broadcast(announceWinner.replace("%player", player1.get(0).getDisplayName()));
										break;
									}
									else if (player2.get(0).getHealth() < 1) {
										broadcast(announceWinner.replace("%player", player2.get(0).getDisplayName()));
										break;
									}
								} else {
									break;
								}
								
								try {
									Thread.sleep(3000);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}

							}

						}
					};

					fight.start();

					return true;
				}

				else if (args[0].equals("break") && args.length == 1) {

					if (!hasPersmission(player, command, args, "break")) {
						return true;
					}

					runThread = false;
					broadcast(breakMessage);

					return true;
				}

				return false;

			} //check if fight
		} //check if player

		return true;
	}

	/**
	 * Sends a message to every online player and into the server console
	 * @param text the message
	 */
	public void broadcast(String text) {
		getServer().broadcastMessage(ChatColor.AQUA + text);
		System.out.println("[FC] " + text);
	}

	/**
	 * Sends a message to a player
	 * @param player recipient of the message 
	 * @param text the message
	 */
	public void send(Player player, String text) {
		player.sendMessage(ChatColor.WHITE + text);
	}

	public boolean hasPersmission(Player player, Command command, String[] args, String perm) {
		if (player.hasPermission("fightcountdown." + perm)) {
			return true;
		}
		send(player, needPermission.replace("%command", "/fight " + arrayToString(args)));
		System.out.println(player.getDisplayName() + " issued server command: /fight " + arrayToString(args));
		return false;
	}

	public static String arrayToString(String[] a) {
		String separator = " ";
		StringBuffer result = new StringBuffer();
		if (a.length > 0) {
			result.append(a[0]);
			for (int i=1; i<a.length; i++) {
				result.append(separator);
				result.append(a[i]);
			}
		}
		return result.toString();
	}

}
