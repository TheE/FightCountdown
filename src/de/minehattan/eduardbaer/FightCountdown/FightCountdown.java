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
					player.sendMessage(ChatColor.WHITE + "You don't have the right to use this command!");
					return true;
				}
				
				if (split.length == 0) {
					return false;
				}
				else {
					int count = 0;
					try{
					count = Integer.valueOf(split[0]).intValue();
					} catch(NumberFormatException e) {return false;}
					
					getServer().broadcastMessage(ChatColor.RED + "Be ready, the fight starts in:");
					
					for (int i = count; i > 0; i--) {
						getServer().broadcastMessage(ChatColor.RED + "" + i + "...");
						try{
							Thread.sleep(1000);
						} catch(InterruptedException e){}
					}
					getServer().broadcastMessage(ChatColor.RED + "Fight!");
	                    
				}

			}
			return true;
		}
	
		return false;
	}
	
	private void setupPermissions() {
		Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

		if (this.Permissions == null) {
			if (test != null) {
				this.Permissions = ((Permissions)test).getHandler();
			} else {
//				log.info("Permission system not detected, defaulting to OP");
				System.out.println("Permission system not detected, defaulting to OP");
			}
		}
	}

}

