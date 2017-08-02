package io.preuss.pay4day;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Pay4Day extends JavaPlugin{
	private String prefix = ChatColor.GREEN + "[Pay4Day] ";
	
	private static Economy econ = null;
	
	public void onDisable(){
		getLogger().info("Pay4Day has been disabled!");
	}
	
	public void onEnable(){
		loadConfig();
		getLogger().info("-------------------------");
		getLogger().info("Created by: Preuß.IO GbR");
		getLogger().info("Website: http://preuss.io/");
		getLogger().info("Updates: https://dev.bukkit.org/projects/pay4day/");
		getLogger().info("Authors: " + String.join(", ", getDescription().getAuthors()));
		getLogger().info("Version: " + getDescription().getVersion());
		getLogger().info("-------------------------");
		if (!setupEconomy()){
			getLogger().warning(String.format("[%s] - Disabled due to no Vault dependency found! You can download it here https://dev.bukkit.org/projects/vault", new Object[] { getDescription().getName() }));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		getLogger().warning("We have new config.yml! If you use older version than 4.0 PLEASE REMOVE YOUR OLD CONFIG.YML");
		getLogger().info("-------------------------");
		getLogger().info("Pay4Day has been enabled!");
	}
	
	private boolean setupEconomy(){
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> serviceProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (serviceProvider == null) {
			return false;
		}
		econ = serviceProvider.getProvider();
		return econ != null;
	}
	
	public boolean onCommand(CommandSender commandSender, Command command, String commandLabel, String[] args){
		if(command.getName().equalsIgnoreCase("pay4day")){
			if (!(commandSender instanceof Player)){
				getLogger().info(getConfig().getString("msg." + getConfig().getString("config.language") + ".cmd_command"));
				return true;
			}else{
				if(args.length == 0){
					//Classic Command
					if(commandSender.hasPermission("pay4day.use.classic")){
						buildWeather(commandSender, "classic", getConfig().getDouble("config.price.classic"));
					}else{
						commandSender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
					}
				}else if(args.length == 1){
					if(args[0].equalsIgnoreCase("day")){
						if(commandSender.hasPermission("pay4day.use.day")){
							buildWeather(commandSender, "day", getConfig().getDouble("config.price.day"));
						}else{
							commandSender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
						}
					}else if(args[0].equalsIgnoreCase("night")){
						if(commandSender.hasPermission("pay4day.use.night")){
							buildWeather(commandSender, "night", getConfig().getDouble("config.price.night"));
						}else{
							commandSender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
						}
					}else if(args[0].equalsIgnoreCase("sun")){
						if(commandSender.hasPermission("pay4day.use.sun")){
							buildWeather(commandSender, "sun", getConfig().getDouble("config.price.sun"));
						}else{
							commandSender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
						}
					}else if(args[0].equalsIgnoreCase("rain")){
						if(commandSender.hasPermission("pay4day.use.rain")){
							buildWeather(commandSender, "rain", getConfig().getDouble("config.price.rain"));
						}else{
							commandSender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
						}
					}else if(args[0].equalsIgnoreCase("storm")){
						if(commandSender.hasPermission("pay4day.use.storm")){
							buildWeather(commandSender, "storm", getConfig().getDouble("config.price.storm"));
						}else{
							commandSender.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".permissions"));
						}
					}
				}
			}
		}else if(command.getName().equalsIgnoreCase("pay4night")){
			getServer().dispatchCommand(commandSender, "pay4day night");
		}else if(command.getName().equalsIgnoreCase("pay4sun")){
			getServer().dispatchCommand(commandSender, "pay4day sun");
		}else if(command.getName().equalsIgnoreCase("pay4rain")){
			getServer().dispatchCommand(commandSender, "pay4day rain");
		}else if(command.getName().equalsIgnoreCase("pay4storm")){
			getServer().dispatchCommand(commandSender, "pay4day storm");
		}
		return true;
	}
	
	private void buildWeather(CommandSender commandSender, String typ, double price) {
		Player player = (Player) commandSender;
		EconomyResponse r = econ.withdrawPlayer((OfflinePlayer) commandSender, price);
		if (r.transactionSuccess()){
			if(typ.equalsIgnoreCase("classic")){
				if(getConfig().getBoolean("config.classic.setDay")){
					player.getWorld().setTime(0);
				}else if(getConfig().getBoolean("config.classic.setNight")){
					player.getWorld().setTime(18000);
				}else if(getConfig().getBoolean("config.classic.setSun")){
					player.getWorld().setThundering(false);
					player.getWorld().setStorm(false);
				}else if(getConfig().getBoolean("config.classic.setRain")){
					player.getWorld().setThundering(false);
					player.getWorld().setStorm(true);
				}else if(getConfig().getBoolean("config.classic.setStorm")){
					player.getWorld().setThundering(true);
					player.getWorld().setStorm(true);
				}
			}else if(typ.equalsIgnoreCase("day")){
				player.getWorld().setTime(0);
			}else if(typ.equalsIgnoreCase("night")){
				player.getWorld().setTime(18000);
			}else if(typ.equalsIgnoreCase("sun")){
				player.getWorld().setThundering(false);
				player.getWorld().setStorm(false);
			}else if(typ.equalsIgnoreCase("rain")){
				player.getWorld().setThundering(false);
				player.getWorld().setStorm(true);
			}else if(typ.equalsIgnoreCase("storm")){
				player.getWorld().setThundering(true);
				player.getWorld().setStorm(true);
			}
			
			player.sendMessage(prefix + getConfig().getString("msg." + getConfig().getString("config.language") + ".surcces_msg_" + typ));
			
			if(getConfig().getBoolean("config.sendLocalMsg")){
				for(Player p : getServer().getOnlinePlayers()){
					String broadcast = getConfig().getString("msg." + getConfig().getString("config.language") + ".broadcast_" + typ).replace("%price%", String.valueOf(price));
					broadcast = broadcast.replace("%currency%", getConfig().getString("config.currency"));
					broadcast = broadcast.replace("%player%", player.getName());
					p.sendMessage(prefix + broadcast);
				}
			}
			
			String str = getConfig().getString("msg." + getConfig().getString("config.language") + ".surcess_money_msg").replace("%price%", String.valueOf(price));
			String surcess_money_msg = str.replace("%currency%", getConfig().getString("config.currency"));
			player.sendMessage(prefix + surcess_money_msg);
		}else{
			String str = getConfig().getString("msg." + getConfig().getString("config.language") + ".surcess_money_msg").replace("%price%", String.valueOf(price));
			String surcess_money_msg = str.replace("%currency%", getConfig().getString("config.currency"));
			player.sendMessage(prefix + surcess_money_msg);
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
		
		getConfig().addDefault("msg.en.surcces_msg_classic", "Successfully set day and sun.");
		getConfig().addDefault("msg.en.surcces_msg_day", "Successfully set day.");
		getConfig().addDefault("msg.en.surcces_msg_night", "Successfully set night.");
		getConfig().addDefault("msg.en.surcces_msg_sun", "Successfully set sun.");
		getConfig().addDefault("msg.en.surcces_msg_rain", "Successfully set rain.");
		getConfig().addDefault("msg.en.surcces_msg_storm", "Successfully set storm.");
		
		getConfig().addDefault("msg.en.broadcast_classic", "%player% spent %price% %currency% for day and sun.");
		getConfig().addDefault("msg.en.broadcast_day", "%player% spent %price% %currency% for day.");
		getConfig().addDefault("msg.en.broadcast_night", "%player% spent %price% %currency% for night.");
		getConfig().addDefault("msg.en.broadcast_sun", "%player% spent %price% %currency% for sun.");
		getConfig().addDefault("msg.en.broadcast_rain", "%player% spent %price% %currency% for rain.");
		getConfig().addDefault("msg.en.broadcast_storm", "%player% spent %price% %currency% for storm.");
		
		
		
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
