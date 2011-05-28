package me.Engelier.XcraftQuest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.Yaml;

public class XcraftQuestQuester {
	private XcraftQuest plugin = null;
	private Map<String, XcraftQuestQuesterPlayer> questers = new HashMap<String, XcraftQuestQuesterPlayer>();
	
	public XcraftQuestQuester (XcraftQuest instance) {
		plugin = instance;
	}
	
	@SuppressWarnings("unchecked")
	public void load() {
		File questerFile = new File(plugin.getDataFolder(), "questers.yml");
		if (!questerFile.exists())
			return;
		
		try {
			Yaml yaml = new Yaml();
			Map<String, Object> questersYaml = (Map<String, Object>) yaml.load(new FileInputStream(questerFile));
			for (Map.Entry<String, Object> thisQuester: questersYaml.entrySet()) {
				String playerName = thisQuester.getKey();
				Map<String, Object> playerData = (Map<String, Object>) thisQuester.getValue();
				
				XcraftQuestQuesterPlayer questerPlayer = new XcraftQuestQuesterPlayer(playerName, plugin);
				questerPlayer.hasQuest = (String) playerData.get("hasQuest");
				questerPlayer.questLevel = (Integer) playerData.get("questLevel");
				questerPlayer.experience = (Integer) playerData.get("experience");
				questerPlayer.questCount = (Integer) playerData.get("questCount");
				questerPlayer.moneyGained = (Integer) playerData.get("moneyGained");
				questerPlayer.blockBroken = (Map<Integer, Integer>) playerData.get("broke");
				questerPlayer.blockPlaced = (Map<Integer, Integer>) playerData.get("placed");
				questerPlayer.blockDamaged = (Map<Integer, Integer>) playerData.get("damaged");
				questerPlayer.entityKilled = (Map<String, Integer>) playerData.get("killed");
				
				questers.put(playerName, questerPlayer);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void save() {
		File questerFile = new File(plugin.getDataFolder(), "questers.yml");
		try {
			if (!questerFile.exists())
				questerFile.createNewFile();

			Map<String, Object> toDump = new HashMap<String, Object>();
			
			for (Map.Entry<String, XcraftQuestQuesterPlayer> thisQuester: questers.entrySet()) {
				String playerName = thisQuester.getKey();
				
				Map<String, Object> values = new HashMap<String, Object>();
				values.put("hasQuest", questers.get(playerName).hasQuest);
				values.put("questLevel", questers.get(playerName).questLevel);
				values.put("experience", questers.get(playerName).experience);
				values.put("questCount", questers.get(playerName).questCount);
				values.put("moneyGained", questers.get(playerName).moneyGained);
				values.put("broke", questers.get(playerName).blockBroken);
				values.put("placed", questers.get(playerName).blockPlaced);
				values.put("damaged", questers.get(playerName).blockDamaged);
				values.put("killed", questers.get(playerName).entityKilled);
				
				
				toDump.put(playerName, values);
			}
			
			Yaml yaml = new Yaml();
			String dump = yaml.dump(toDump);
			
			FileOutputStream fh = new FileOutputStream(questerFile);
			new PrintStream(fh).println(dump);
			fh.flush();
			fh.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	public Boolean hasQuester(String name) {
		if (questers.get(name) == null)
			return false;
		else
			return true;
	}
	
	public XcraftQuestQuesterPlayer getQuester(String name) {
		XcraftQuestQuesterPlayer thisQuester = questers.get(name);
		
		if (thisQuester == null) {
			thisQuester = new XcraftQuestQuesterPlayer(name, plugin);
			questers.put(name, thisQuester);
		}
		
		return thisQuester;
	}
	
	public void showStats(Player player, XcraftQuestQuesterPlayer quester) {
		player.sendMessage(ChatColor.GOLD + plugin.lang.getString("ststs_for", "Quest stats for") + " " + quester.player);
		player.sendMessage(plugin.lang.getString("quest_level", "Quest Level") + ": " + quester.questLevel);
		player.sendMessage(plugin.lang.getString("experience", "Experience") + ": " + quester.experience + "/" + plugin.config.getString("level/" + quester.questLevel, "error"));
		player.sendMessage(plugin.lang.getString("quests_completed", "Quests completed") + ": " + quester.questCount);
		
		if (plugin.ecoMethod != null)
			player.sendMessage(plugin.lang.getString("money_gained", "Money gained") + ": " + plugin.ecoMethod.format(quester.moneyGained));
		
		if (quester.hasQuest != null)
			player.sendMessage(plugin.lang.getString("active_quest", "Active Quest") + ": " + plugin.quests.quests.getString(quester.hasQuest + "/name", "null"));
		else
			player.sendMessage(plugin.lang.getString("does_not_have_quest", "You don't have an active quest"));
	}
}	
