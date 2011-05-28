package de.xcraft.engelier.quest;

import org.bukkit.event.block.*;

public class XcraftQuestBlockListener extends BlockListener {
	private XcraftQuest plugin = null;
	
	public XcraftQuestBlockListener (XcraftQuest instance) {
		plugin = instance;
	}
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (plugin.quester.hasQuester(event.getPlayer().getName()))
			plugin.quester.getQuester(event.getPlayer().getName()).placedBlock(event.getBlockPlaced().getTypeId());
	}
	
	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		if (plugin.quester.hasQuester(event.getPlayer().getName()))
			plugin.quester.getQuester(event.getPlayer().getName()).damagedBlock(event.getBlock().getTypeId());
	}
	
	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (plugin.quester.hasQuester(event.getPlayer().getName()))
			plugin.quester.getQuester(event.getPlayer().getName()).brokeBlock(event.getBlock().getTypeId());
	}
}
