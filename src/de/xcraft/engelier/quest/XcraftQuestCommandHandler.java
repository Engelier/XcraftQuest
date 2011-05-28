package de.xcraft.engelier.quest;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class XcraftQuestCommandHandler {
	private XcraftQuest plugin = null;
	
	private String command = null;
	private String[] commandArgs = null;
	
	public XcraftQuestCommandHandler (XcraftQuest instance) {
		plugin = instance;
	}
	
	public void printUsage(Player player) {
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getDescription().getFullName() + " by " + plugin.getDescription().getAuthors());
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.lang.getString("commands", "Commands") + ":");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/quest help " + ChatColor.WHITE + "| " + ChatColor.AQUA + plugin.lang.getString("command_help_help", "Shows this help menu"));
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/quest give " + ChatColor.WHITE + "| " + ChatColor.AQUA + plugin.lang.getString("command_help_give", "Gives you a random quest"));
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/quest done " + ChatColor.WHITE + "| " + ChatColor.AQUA + plugin.lang.getString("command_help_done", "Attempts to turn in your current quest"));
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/quest info " + ChatColor.WHITE + "| " + ChatColor.AQUA + plugin.lang.getString("command_help_info", "Resends you your quest info/progress"));
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/quest drop " + ChatColor.WHITE + "| " + ChatColor.AQUA + plugin.lang.getString("command_help_drop", "Drops your curren quest"));
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/quest stats " + ChatColor.WHITE + "| " + ChatColor.AQUA + plugin.lang.getString("command_help_stats", "Show your stored info. Try it!"));
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/quest top <#>" + ChatColor.WHITE + "| " + ChatColor.AQUA + plugin.lang.getString("command_help_top", "Shows the <#> top questers of your server"));
	}
	
	public void printAdminUsage(Player player) {
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getDescription().getFullName() + " by " + plugin.getDescription().getAuthors());
		player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.lang.getString("admin_commands", "Admin-Commands") + ":");
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/quest admin reload " + ChatColor.WHITE + "| " + ChatColor.AQUA + plugin.lang.getString("command_adminhelp_reload", "Reloads the quest file"));
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/quest admin stats <player> " + ChatColor.WHITE + "| " + ChatColor.AQUA + plugin.lang.getString("command_adminhelp_stats", "Shows stored info for <player>"));
		player.sendMessage(ChatColor.LIGHT_PURPLE + "-> " + ChatColor.GREEN + "/quest admin setquest <player> <questid> " + ChatColor.WHITE + "| " + ChatColor.AQUA + plugin.lang.getString("command_adminhelp_setquest", "Sets <player>s active quest to <questid>"));
	}
	
	public boolean hasPermission(Player player, String permission, Boolean op) {
		if (plugin.permissions != null) {
			return (plugin.permissions.has(player, permission));
		} else {
			return (op == player.isOp());
		}
	}
	
	public String parse(Player player, String[] args) {
		if (args.length > 0) {
			this.command = args[0];
		} else {
			printUsage(player);
			return null;
		}
		this.commandArgs = args;
		
		if (command.equals("give") || command.equals("info") || command.equals("done") || command.equals("stats")) {
			if (!hasPermission(player, "XcraftQuest.quest.quest", false))
				return plugin.lang.getString("no_permission", "You don't have permission to use this command");

			if (command.equals("give")) {
				plugin.quester.getQuester(player.getName()).giveQuest();
			} else if (command.equals("info")) {
				plugin.quester.getQuester(player.getName()).showQuest();
			} else if (command.equals("done")) {
				plugin.quester.getQuester(player.getName()).finishQuest();
			} else if (command.equals("stats")) {
				plugin.quester.showStats(player, plugin.quester.getQuester(player.getName()));
			}
		} else if(command.equals("drop")) {
			if (!hasPermission(player, "XcraftQuest.quest.drop", false))
				return plugin.lang.getString("no_permission", "You don't have permission to use this command");

			plugin.quester.getQuester(player.getName()).dropQuest();
		} else if(command.equals("top")) {
			if (!hasPermission(player, "XcraftQuest.quest.top", false))
				return plugin.lang.getString("no_permission", "You don't have permission to use this command");
			
			Integer count = null;
			if (commandArgs.length > 1)
				try { count = new Integer(commandArgs[1]);  } catch(Exception ex) {}
			
			plugin.quester.showTop(player, count);
		} else if(command.equals("admin")) {
			if (commandArgs[1].equals("reload")) {
				if (!hasPermission(player, "XcraftQuest.admin.reload", true))
					return plugin.lang.getString("no_permission", "You don't have permission to use this command");
				
				plugin.quests.load();
				player.sendMessage(plugin.lang.getString("reloaded_quests", "Reloaded all quests."));
			} else if (commandArgs[1].equals("stats")) {
					if (!hasPermission(player, "XcraftQuest.admin.stats", true))
						return plugin.lang.getString("no_permission", "You don't have permission to use this command");

					if (plugin.quester.hasQuester(commandArgs[2])) {
						plugin.quester.showStats(player, plugin.quester.getQuester(commandArgs[2]));
					} else {
						player.sendMessage(plugin.lang.getString("player_not_found", "Couldn't find questing player") + " " + commandArgs[2]);
					}
			} else if (commandArgs[1].equals("setquest")) {
				if (!hasPermission(player, "XcraftQuest.admin.setquest", true))
					return plugin.lang.getString("no_permission", "You don't have permission to use this command");

				if (plugin.quester.hasQuester(commandArgs[2])) {
					if (plugin.quests.quests.getString(commandArgs[3] + "/name", null) != null) {
						plugin.quester.getQuester(commandArgs[2]).hasQuest = commandArgs[3];
						player.sendMessage(plugin.lang.getString("set_quest_to", "Set players quest to") + ": " + ChatColor.GOLD + plugin.quests.quests.getString(commandArgs[3] + "/name", ""));
					} else {
						player.sendMessage(plugin.lang.getString("invalid_questid", "Invalid quest ID") + " (" + commandArgs[3] + ")");
					}
				} else {
					player.sendMessage(plugin.lang.getString("player_not_found", "Couldn't find questing player") + " " + commandArgs[2]);
				}
			} else {
				printAdminUsage(player);
			}
				
 		} else {
 			printUsage(player);
 			return null;
		}
		
		return null;
	}
	
}
