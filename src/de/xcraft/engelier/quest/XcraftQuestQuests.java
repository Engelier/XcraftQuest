package de.xcraft.engelier.quest;

import java.util.*;

import de.xcraft.engelier.utils.Configuration;

public class XcraftQuestQuests {
	private XcraftQuest plugin = null;
	
	public Configuration quests = null;
	public Map<Integer, List<String>> levelQuests = new HashMap<Integer, List<String>>();
	
	public XcraftQuestQuests (XcraftQuest instance) {
		plugin = instance;
	}
	
	public void load() {
		quests = new Configuration();
		quests.load(plugin.getDataFolder().toString(), "quests.yml");
		
		for (String lvl: plugin.config.getKeys("level")) {
			Integer lvlI = new Integer(lvl);
			
			List<String> thisList = new ArrayList<String>();

			for (String id: quests.getKeys(null)) {
				if (quests.getInt(id + "/minLevel", 0) <= lvlI && quests.getInt(id + "/maxLevel", 999) >= lvlI) {
					thisList.add(id);
				}
			}	
			
			if (thisList.size() == 0)
				plugin.log.severe(plugin.getNameBrackets() + "no quests found for quest level " + lvlI);
			
			levelQuests.put(lvlI, thisList);
		}
				
		plugin.log.info(plugin.getNameBrackets() + "loaded " + quests.getKeys(null).size() + " quests.");
	}	
}
