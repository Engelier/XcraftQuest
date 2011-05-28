package me.Engelier.XcraftQuest;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class XcraftQuestInventoryListener extends PlayerListener {
	private XcraftQuest plugin = null;
	
	public XcraftQuestInventoryListener(XcraftQuest instance) {
		plugin = instance;
	}
	
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		Item item = event.getItem();
		
		if (plugin.quester.hasQuester(player.getName())) {
			if (plugin.quester.getQuester(player.getName()).hasQuest != null) {
				plugin.quester.getQuester(player.getName()).checkProgress("collect", item.getItemStack().getTypeId() + "", 0);
			}
		}
	}	
}
