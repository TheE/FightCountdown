package de.minehattan.eduardbaer.FightCountdown;


import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * FightCountdown for Bukkit
 * 
 * @author EduardBaer
 */
public class FightCountdown extends JavaPlugin {

	public PermissionHandler Permissions;

	static String maindir = "plugins/FightCountdown/";

	String next = "";
	int count;

	List<Player> player1;
	List<Player> player2;

	boolean runThread;




	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is disabled!");	}

	@Override
	public void onEnable() {

		new File(maindir).mkdirs();

		LoadConfig.loadMain();
		LoadConfig.loadText();

		setupPermissions();

		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version "
				+ pdfFile.getVersion() + " is enabled!");

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

				if (args[0].equals("info")) {
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
						broadcast(LoadConfig.txtDice.replace("%weapon", LoadConfig.txtDiceBow));
					}
					else {
						broadcast(LoadConfig.txtDice.replace("%weapon", LoadConfig.txtDiceSword));
					}

					return true;

				}

				else if (args[0].equals("next")) {
					if (args.length == 2 && args[1].equals("clear")) {
						if (!hasPersmission(player, command, args, "next.clear")) {
							return true;
						}

						next = "";

						send(player, LoadConfig.txtClearNext);

						System.out.println("[FC] " + player.getDisplayName() + " removed next");

						return true;

					}
					else if (args.length >= 2) {
						if (!hasPersmission(player, command, args, "next")) {
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
						count = LoadConfig.defaultCount;
					}
					else {
						try{
							count = Integer.valueOf(args[1]).intValue();
						} catch(NumberFormatException e) {return false;}
					}

					if (LoadConfig.maxCount != 0 && count > LoadConfig.maxCount) {
						count = LoadConfig.maxCount;
					}

					Thread counter = new Thread() {
						public void run() {
							broadcast(LoadConfig.txtStartCountdown);

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
								broadcast(LoadConfig.txtStartFight);
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
						send(player, "§cOne or both arguments are invalid");
						return true;
					}
					
					broadcast(LoadConfig.txtAnnounceFight.replace("%player1", player1.get(0).getDisplayName()).replace("%player2", player2.get(0).getDisplayName()));

					Thread fight = new Thread() {
						public void run() {
							while(true) {
								if (runThread) {
									if (player1.get(0).getHealth() < 1) {
										broadcast(LoadConfig.txtAnnounceWinner.replace("%player", player1.get(0).getDisplayName()));
										break;
									}
									else if (player2.get(0).getHealth() < 1) {
										broadcast(LoadConfig.txtAnnounceWinner.replace("%player", player2.get(0).getDisplayName()));
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
					broadcast(LoadConfig.txtBreak);

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
		getServer().broadcastMessage(LoadConfig.broadcastColor + text);
		System.out.println("[FC] " + text);
	}

	/**
	 * Sends a message to a player
	 * @param player recipient of the message 
	 * @param text the message
	 */
	public void send(Player player, String text) {
		player.sendMessage(LoadConfig.broadcastColor + text);
	}

	/**
	 * Setup this plugin for Permissions
	 */
	private void setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

		if (this.Permissions == null) {
			if (test != null) {
				this.Permissions = ((Permissions)test).getHandler();
			} else {
				System.out.println("Permission system not detected, defaulting to OP");
			}
		}
	}

	public boolean hasPersmission(Player player, Command command, String[] args, String perm) {
		if (!(this).Permissions.has(player, "fightcountdown." + perm) && LoadConfig.usePermissions) {
			send(player, LoadConfig.txtPermission.replace("%command", "/fight " + arrayToString(args)));
			System.out.println(player.getDisplayName() + " issued server command: /fight " + arrayToString(args));
			return false;
		}
		return true;
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

