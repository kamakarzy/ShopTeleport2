package com.gmail.nomad856.minecraft;

import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.plugin.PluginDescriptionFile;

public class Main extends JavaPlugin {
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Main plugin;
	public static Economy econ = null;
	public static Permission perms = null;
	public static Chat chat = null;

	@Override
	public void onEnable() {
		getLogger().info(
				"By nomad856 aka kamakarzy with thanks to bukkit fourms team");
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + "Has Been Disabled");
		setupPermissions();
		plugin = this;
		getConfig().options().copyDefaults(true);
		saveConfig();
		return;
	}

	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + "Has Been Disabled");
		saveConfig();

	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer()
				.getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;

	}

	public void setShop(Player p, Location loc) {
		String player = p.getName();
		getConfig().set(
				player,
				p.getWorld().getName() + "," + (int) loc.getX() + ","
						+ (int) loc.getY() + "," + (int) loc.getZ());
		saveConfig();
	}

	public void TeleportShop(Player p, String shopowner) {
		String[] locs = getConfig().getString(shopowner).split(",");
		World world = Bukkit.getWorld(locs[0]);
		int x = Integer.parseInt(locs[1]);
		int y = Integer.parseInt(locs[2]);
		int z = Integer.parseInt(locs[3]);
		p.teleport(new Location(world, x, y, z));
	}

	public void deleteShop(Player p) {
		String name = p.getName();
		getConfig().set(name, null);
		saveConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		Player player = (Player) sender;
		Location X = player.getLocation();
		if (commandLabel.equalsIgnoreCase("setshop")) {

			if (perms.has(player, "shopteleportation.setshop")) {

				if (getConfig().getString(player.getName()) != null) {

					player.sendMessage(ChatColor.RED
							+ "You already have a Shop saved");
				} else {

					Location location = player.getLocation();
					String playername = sender.getName();
					Bukkit.getPlayer(playername).getWorld().playSound(location,Sound.BLAZE_DEATH,1, 0);
					setShop(player, X);
					player.sendMessage(ChatColor.GOLD + "Shop Set At: "
							+ ChatColor.GREEN + player.getLocation().getX()
							+ ChatColor.GOLD + "," + ChatColor.GREEN
							+ player.getLocation().getY() + ChatColor.GOLD
							+ "," + ChatColor.GREEN
							+ player.getLocation().getZ() + ChatColor.GOLD
							+ ",");

				}
			} else {
				player.sendMessage("No Permission To use this");
				return true;
			}
		}

		else if (commandLabel.equalsIgnoreCase("shop")) {

			if (args.length == 0) {
				if (perms.has(player, "shopteleportation.shopself")) {
					if (getConfig().getString(player.getName()) != null) {
						TeleportShop(player, player.getName());
					} else
						player.sendMessage(ChatColor.RED
								+ " You Do not have a shop!");
				} else {
					player.sendMessage(ChatColor.RED + "NO PERMISSIONS");
				}
			} else if (args.length == 1) {

				String targetPlayer;
				if (Bukkit.getServer().getPlayer(args[0]) != null) {
					targetPlayer = Bukkit.getServer().getPlayer(args[0])
							.getName();
				} else {
					if (Bukkit.getOfflinePlayer(args[0]).hasPlayedBefore()) {
						targetPlayer = Bukkit.getOfflinePlayer(args[0])
								.getName();
					} else {
						player.sendMessage(ChatColor.RED + "Unknown player");
						return true;
					}
				}

				if (perms.has(player, "shopteleportation.shopOther")) {

					if (getConfig().contains(targetPlayer)) {

						TeleportShop(player, targetPlayer);
						player.sendMessage(ChatColor.GREEN + "Welcome to "
								+ targetPlayer + "'s Shop");
						return true;

					} else {
						player.sendMessage(targetPlayer + ChatColor.RED
								+ "Does not have a shop!");
						return true;
					}
				} else {
					player.sendMessage(ChatColor.RED + "NO PERMISSION");
					return true;
				}
			}
		}
		else if (commandLabel.equalsIgnoreCase("streload")){
			if(perms.has(sender, "shopteleportation.Reload")){
			plugin.reloadConfig();
			player.sendMessage(plugin + "was reloaded");
			}
			else{
				player.sendMessage(ChatColor.RED + "NO PERMISSION");
			}
		}
		else if (commandLabel.equalsIgnoreCase("delshop")) {
			deleteShop(player);
			player.sendMessage(ChatColor.RED + "Shop Has Been Deleted");
			return true;
		}
		else if (commandLabel.equalsIgnoreCase("sthelp"));{
		return false;
		}
	}
}