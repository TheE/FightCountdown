package de.minehattan.eduardbaer.FightCountdown;


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
	
	public static PermissionHandler Permissions;
	int count;
	

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " is disabled!");
	}

	@Override
	public void onEnable() {
		setupPermissions();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		
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
					send(player, ChatColor.AQUA + "/fight set <seconds> - to set up a countdown");
					
					return true;
				}
				
				else if (args[0].equals("set") && args[1] != "") {
					
					if (!(this).Permissions.has(player, "fightcountdown.fight")) {
						send(player, "You don't have the right to use /fight set " + args[1] + "!");
						System.out.println(player.getDisplayName() + " issued server command: /fight set " + args[1]);
						return true;
					}
					
					try{
						count = Integer.valueOf(args[1]).intValue();
					} catch(NumberFormatException e) {return false;}
					
					Thread counter = new Thread() {
						public void run() {
							broadcast("Be ready, the fight starts in:");

							for (int i = count; i > 0; i--) {
								broadcast(i + "...");
								try{
									Thread.sleep(1000);
								} catch(InterruptedException e){}
							}
								
							broadcast("Fight!");
						}
					};
						
					counter.start();
						
					return true;
					
				} //check if set
				
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
		getServer().broadcastMessage(ChatColor.RED + text);
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

}

