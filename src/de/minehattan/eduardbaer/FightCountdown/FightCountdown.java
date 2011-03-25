package de.minehattan.eduardbaer.FightCountdown;


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
	

	@Override
	public void onDisable() {
        System.out.println("FightCountdown is disabled!");
	}

	@Override
	public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
		
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	    String[] split = args;
	    String commandName = command.getName().toLowerCase();
	        if (sender instanceof Player) {
	            if (commandName.equals("fight")) {
	                if (split.length == 0) {
	                    getServer().broadcastMessage(ChatColor.RED + "5...");
	                    try{
	                        Thread.sleep(1000);
	                    } catch(InterruptedException e){}
	                    getServer().broadcastMessage(ChatColor.RED + "4...");
	                    try{
	                        Thread.sleep(1000);
	                    } catch(InterruptedException e){}
	                    getServer().broadcastMessage(ChatColor.RED + "3...");
	                    try{
	                        Thread.sleep(1000);
	                    } catch(InterruptedException e){}
	                    getServer().broadcastMessage(ChatColor.RED + "2...");
	                    try{
	                        Thread.sleep(1000);
	                    } catch(InterruptedException e){}
	                    getServer().broadcastMessage(ChatColor.RED + "1...");
	                    try{
	                        Thread.sleep(1000);
	                    } catch(InterruptedException e){}
	                    getServer().broadcastMessage(ChatColor.RED + "Fight!");
	                    
	                }

	            }

	        }
	        return true;
	    }
	


}
