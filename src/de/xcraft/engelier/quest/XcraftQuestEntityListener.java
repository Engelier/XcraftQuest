package de.xcraft.engelier.quest;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.*;
import org.bukkit.event.entity.*;

public class XcraftQuestEntityListener extends EntityListener {
	private XcraftQuest plugin = null;
	private Map<Integer, Player> taggedMobs = new HashMap<Integer, Player>();
	
	public XcraftQuestEntityListener (XcraftQuest instance) {
		plugin = instance;
	}
	
	@Override
	public void onEntityDeath (EntityDeathEvent event) {
		Entity died = event.getEntity();
		Player owner = taggedMobs.get(died.getEntityId());

		if (owner == null)
			return;

		taggedMobs.remove(died.getEntityId());
		
		if (!plugin.quester.hasQuester(owner.getName()))
			return;
		
		if (died instanceof Chicken) {
			plugin.quester.getQuester(owner.getName()).killedEntity("chicken");			
		} else if (died instanceof Cow) {
			plugin.quester.getQuester(owner.getName()).killedEntity("cow");
		} else if (died instanceof Creeper) {
			plugin.quester.getQuester(owner.getName()).killedEntity("creeper");			
		} else if (died instanceof Ghast) {
			plugin.quester.getQuester(owner.getName()).killedEntity("ghast");			
		} else if (died instanceof Giant) {
			plugin.quester.getQuester(owner.getName()).killedEntity("giant");			
		} else if (died instanceof Pig) {
			plugin.quester.getQuester(owner.getName()).killedEntity("pig");			
		} else if (died instanceof PigZombie) {
			plugin.quester.getQuester(owner.getName()).killedEntity("pigzombie");			
		} else if (died instanceof Sheep) {
			plugin.quester.getQuester(owner.getName()).killedEntity("sheep");			
		} else if (died instanceof Skeleton) {
			plugin.quester.getQuester(owner.getName()).killedEntity("skeleton");			
		} else if (died instanceof Slime) {
			plugin.quester.getQuester(owner.getName()).killedEntity("slime");			
		} else if (died instanceof Spider) {
			plugin.quester.getQuester(owner.getName()).killedEntity("spider");			
		} else if (died instanceof Squid) {
			plugin.quester.getQuester(owner.getName()).killedEntity("squid");			
		} else if (died instanceof Wolf) {
			plugin.quester.getQuester(owner.getName()).killedEntity("wolf");			
		} else if (died instanceof Zombie) {
			plugin.quester.getQuester(owner.getName()).killedEntity("zombie");			
		}
	}
	
	@Override
	public void onEntityDamage (EntityDamageEvent event) {
		if (taggedMobs.get(event.getEntity().getEntityId()) == null) {
			if (event instanceof EntityDamageByEntityEvent) {
				Entity attacker = ((EntityDamageByEntityEvent) event).getDamager();
				
				if (attacker instanceof Player) {
					if (!plugin.quester.hasQuester(((Player)attacker).getName()))
						return;

					taggedMobs.put(event.getEntity().getEntityId(), (Player) attacker);
				}
			}			
		}
	}
}
