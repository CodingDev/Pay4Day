package de.CodingDev.Pay4Day;

import java.io.IOException;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.CodingDev.Pay4Day.org.mcstats.Metrics;

public class Pay4Day extends JavaPlugin{
	String prefix = ChatColor.GREEN + "[Pay4Day] ";
	
	public static Economy econ = null;
	
	public void onDisable(){
		getLogger().info("Pay4Day has been disabled!");
	}
	
	public void onEnable(){
		if (!setupEconomy()){
			getLogger().warning(String.format("[%s] - Disabled due to no Vault dependency found!", new Object[] { getDescription().getName() }));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getLogger().info("Pay4Day has been enabled!");
		getLogger().warning("We have new config.yml options & language Settings!!!");
		getLogger().info("-------------------------");
		getLogger().info("By: R3N3PDE");
		getLogger().info("Website: http://codingdev.de/");
		getLogger().info("Website: http://r3n3p.de/");
		getLogger().info("Updates: http://dev.bukkit.org/server-mods/pay4day/");
		getLogger().info("Version: " + getDescription().getVersion());
		getLogger().info("-------------------------");
		try
		{
			Metrics metrics = new Metrics(this);
			metrics.start();
		}catch (IOException localIOException) {}
	}
	
	private boolean setupEconomy(){
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = (Economy)rsp.getProvider();
		return econ != null;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		if(command.getName().equalsIgnoreCase("pay4day")){
			if (!(sender instanceof Player)){
				getLogger().info(getConfig().getString("msg." + getConfig().getString("config.language") + ".cmd_command"));
				return true;
			}else{
				if(args.length == 0){
					//Classic Command
					if(sender.hasPermission("pay4day.use.classic")){
						builWeather(sender, "classic", getConfig().getDouble("config.price.classic"));
					}else{
						sender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
					}
				}else if(args.length == 1){
					if(args[0].equalsIgnoreCase("day")){
						if(sender.hasPermission("pay4day.use.day")){
							builWeather(sender, "day", getConfig().getDouble("config.price.day"));
						}else{
							sender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
						}
					}else if(args[0].equalsIgnoreCase("night")){
						if(sender.hasPermission("pay4day.use.night")){
							builWeather(sender, "night", getConfig().getDouble("config.price.night"));
						}else{
							sender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
						}
					}else if(args[0].equalsIgnoreCase("sun")){
						if(sender.hasPermission("pay4day.use.sun")){
							builWeather(sender, "sun", getConfig().getDouble("config.price.sun"));
						}else{
							sender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
						}
					}else if(args[0].equalsIgnoreCase("rain")){
						if(sender.hasPermission("pay4day.use.rain")){
							builWeather(sender, "rain", getConfig().getDouble("config.price.rain"));
						}else{
							sender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
						}
					}else if(args[0].equalsIgnoreCase("storm")){
						if(sender.hasPermission("pay4day.use.storm")){
							builWeather(sender, "storm", getConfig().getDouble("config.price.storm"));
						}else{
							sender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
						}
					}
				}
			}
		}else if(command.getName().equalsIgnoreCase("pay4night")){
			getServer().dispatchCommand(sender, "pay4day night");
		}else if(command.getName().equalsIgnoreCase("pay4sun")){
			getServer().dispatchCommand(sender, "pay4day sun");
		}else if(command.getName().equalsIgnoreCase("pay4rain")){
			getServer().dispatchCommand(sender, "pay4day rain");
		}else if(command.getName().equalsIgnoreCase("pay4storm")){
			getServer().dispatchCommand(sender, "pay4day storm");
		}
		return true;
	}
	
	private void builWeather(CommandSender csender, String typ, double price) {
		Player sender = (Player) csender;
		EconomyResponse r = econ.withdrawPlayer(sender.getName(), price);
		if (r.transactionSuccess()){
			if(typ.equalsIgnoreCase("classic")){
				if(getConfig().getBoolean("config.classic.setDay")){
					sender.getWorld().setTime(0);
				}else if(getConfig().getBoolean("config.classic.setNight")){
					sender.getWorld().setTime(18000);
				}else if(getConfig().getBoolean("config.classic.setSun")){
					sender.getWorld().setThundering(false);
					sender.getWorld().setStorm(false);
				}else if(getConfig().getBoolean("config.classic.setRain")){
					sender.getWorld().setThundering(false);
					sender.getWorld().setStorm(true);
				}else if(getConfig().getBoolean("config.classic.setStorm")){
					sender.getWorld().setThundering(true);
					sender.getWorld().setStorm(true);
				}
			}else if(typ.equalsIgnoreCase("day")){
				sender.getWorld().setTime(0);
			}else if(typ.equalsIgnoreCase("night")){
				sender.getWorld().setTime(18000);
			}else if(typ.equalsIgnoreCase("sun")){
				sender.getWorld().setThundering(false);
				sender.getWorld().setStorm(false);
			}else if(typ.equalsIgnoreCase("rain")){
				sender.getWorld().setThundering(false);
				sender.getWorld().setStorm(true);
			}else if(typ.equalsIgnoreCase("storm")){
				sender.getWorld().setThundering(true);
				sender.getWorld().setStorm(true);
			}
			
			sender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".surcces_msg_" + typ));
			
			if(getConfig().getBoolean("config.sendLocalMsg")){
				for(Player p : getServer().getOnlinePlayers()){
					String broadcast = getConfig().getString("msg." + getConfig().getString("config.language") + ".broadcast_" + typ).replace("%price%", String.valueOf(price));
					broadcast = broadcast.replace("%currency%", getConfig().getString("config.currency"));
					broadcast = broadcast.replace("%player%", sender.getName());
					p.sendMessage(prefix + broadcast);
				}
			}
			
			String str = getConfig().getString("msg." + getConfig().getString("config.language") + ".surcess_money_msg").replace("%price%", String.valueOf(price));
			String surcess_money_msg = str.replace("%currency%", getConfig().getString("config.currency"));
			sender.sendMessage(prefix + surcess_money_msg);
		}else{
			String str = getConfig().getString("msg." + getConfig().getString("config.language") + ".surcess_money_msg").replace("%price%", String.valueOf(price));
			String surcess_money_msg = str.replace("%currency%", getConfig().getString("config.currency"));
			sender.sendMessage(prefix + surcess_money_msg);
		}
	}

	private void loadConfig(){
		getConfig().addDefault("config.price.classic", 100.0);
		getConfig().addDefault("config.price.day", 100.0);
		getConfig().addDefault("config.price.night", 100.0);
		getConfig().addDefault("config.price.sun", 100.0);
		getConfig().addDefault("config.price.rain", 100.0);
		getConfig().addDefault("config.price.storm", 100.0);
		
		getConfig().addDefault("config.classic.setDay", true);
		getConfig().addDefault("config.classic.setNight", false);
		getConfig().addDefault("config.classic.setSun", true);
		getConfig().addDefault("config.classic.setRain", false);
		getConfig().addDefault("config.classic.setStorm", false);
		
		getConfig().addDefault("config.language", "en");
		getConfig().addDefault("config.currency", "Dollar");
		
		getConfig().addDefault("config.sendLocalMsg", true);
		
		/*------------------------ ENGLISH -------------------------------*/
		
		getConfig().addDefault("msg.en.surcces_msg_classic", "Successfully turned day and sun.");
		getConfig().addDefault("msg.en.surcces_msg_day", "Successfully turned day.");
		getConfig().addDefault("msg.en.surcces_msg_night", "Successfully turned night.");
		getConfig().addDefault("msg.en.surcces_msg_sun", "Successfully turned sun.");
		getConfig().addDefault("msg.en.surcces_msg_rain", "Successfully turned rain.");
		getConfig().addDefault("msg.en.surcces_msg_storm", "Successfully turned storm.");
		
		getConfig().addDefault("msg.en.broadcast_classic", "%player% spent %price% %currency% for it to be day and sun.");
		getConfig().addDefault("msg.en.broadcast_day", "%player% spent %price% %currency% for it to be day.");
		getConfig().addDefault("msg.en.broadcast_night", "%player% spent %price% %currency% for it to be night.");
		getConfig().addDefault("msg.en.broadcast_sun", "%player% spent %price% %currency% for it to be sun.");
		getConfig().addDefault("msg.en.broadcast_rain", "%player% spent %price% %currency% for it to be rain.");
		getConfig().addDefault("msg.en.broadcast_storm", "%player% spent %price% %currency% for it to be storm.");
		
		
		
		getConfig().addDefault("msg.en.surcess_money_msg", "You spent %price% %currency%.");
		getConfig().addDefault("msg.en.not_enough_money", "You do not have enough money, you need %price% %currency%");
		getConfig().addDefault("msg.en.permissions", "You do not have permissions for this command.");
		getConfig().addDefault("msg.en.cmd_command", "Only players can use this command.");
		
		
		/*------------------------- GERMAN ------------------------------*/
		
		
		getConfig().addDefault("msg.de.surcces_msg_classic", "Du hast dir Tag & Gutes Wetter gekauft.");
		getConfig().addDefault("msg.de.surcces_msg_day", "Du hast dir Tag & Gutes Wetter gekauft.");
		getConfig().addDefault("msg.de.surcces_msg_night", "Du hast dir die Nacht gekauft.");
		getConfig().addDefault("msg.de.surcces_msg_sun", "Du hast dir die Sonne gekauft.");
		getConfig().addDefault("msg.de.surcces_msg_rain", "Du hast dir den Regen gekauft.");
		getConfig().addDefault("msg.de.surcces_msg_storm", "Du hast dir den Sturm gekauft.");
		
		getConfig().addDefault("msg.de.broadcast_classic", "%player% hat sich Tag & Sonne gekauft für %price% %currency%.");
		getConfig().addDefault("msg.de.broadcast_day", "%player% hat sich Tag gekauft für %price% %currency%.");
		getConfig().addDefault("msg.de.broadcast_night", "%player% hat sich Nacht gekauft für %price% %currency%.");
		getConfig().addDefault("msg.de.broadcast_sun", "%player% hat sich Sonne gekauft für %price% %currency%.");
		getConfig().addDefault("msg.de.broadcast_rain", "%player% hat sich Regen gekauft für %price% %currency%.");
		getConfig().addDefault("msg.de.broadcast_storm", "%player% hat sich Stumr gekauft für %price% %currency%.");
		
		getConfig().addDefault("msg.de.surcess_money_msg", "Dir wurden %price% %currency%s abgezogen");
		getConfig().addDefault("msg.de.not_enough_money", "Du hast zu wenig Geld, du brauchst %price% %currency%.");
		getConfig().addDefault("msg.de.permissions", "Du hast nicht die Permissions für diesen Befehl.");
		getConfig().addDefault("msg.de.cmd_command", "Nur Spieler können diesen Befehl benutzen.");
		
		getConfig().options().copyDefaults(true);
		saveConfig();
	}
}
