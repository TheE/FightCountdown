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
		System.out.println("FightCountdown is disabled!");
	}

	@Override
	public void onEnable() {
		setupPermissions();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
		
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		String[] split = args;
		String commandName = command.getName().toLowerCase();
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (commandName.equals("fight")) {
				
				if (!(this).Permissions.has(player, "fightcountdown.fight")) {
					send(player, "You don't have the right to use /fight!");
					return true;
				}
				
				if (split.length == 0) {
					return false;
				}
				else {
					try{
						count = Integer.valueOf(split[0]).intValue();
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
					
				}

			}
			return true;
		}
	
		return false;
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
	 * Sends a message to a player and into the server console
	 * @param player recipient of the message 
	 * @param text the message
	 */
	public void send(Player player, String text) {
		player.sendMessage(ChatColor.WHITE + text);
		System.out.println("[FC -> " + player.getDisplayName() + "] " + text);
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

