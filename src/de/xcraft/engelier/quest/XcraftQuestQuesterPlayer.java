package de.xcraft.engelier.quest;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.nijikokun.register.payment.Method.MethodAccount;

public class XcraftQuestQuesterPlayer{
	private XcraftQuest plugin = null;

	public Map<Integer, Integer> blockPlaced = new HashMap<Integer, Integer>();
	public Map<Integer, Integer> blockDamaged = new HashMap<Integer, Integer>();
	public Map<Integer, Integer> blockBroken = new HashMap<Integer, Integer>();
	public Map<String, Integer> entityKilled = new HashMap<String, Integer>();
	public String player = null;
	public String hasQuest = null;
	public Integer questLevel = 0;
	public Integer experience = 0;
	public Integer questCount = 0;
	public Integer moneyGained = 0;
	
	public XcraftQuestQuesterPlayer(String playerName, XcraftQuest instance) {
		player = playerName;
		plugin = instance;
	}
	
	@SuppressWarnings("unchecked")
	public void checkProgress(String type, String id, Integer done) {
		Map<String, Object> targets = (Map<String, Object>)plugin.quests.quests.getNode(hasQuest + "/targets");
		for (Map.Entry<String, Object> thisTarget: targets.entrySet()) {
			Map<String, Object> things = (Map<String, Object>)thisTarget.getValue();

			if (things.get("type").equals(type) && things.get("id").toString().equals(id)) {
				Integer target = (Integer)things.get("amount");
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false))
					target *= (questLevel + 1);
				
				if (type.equals("collect")) {
					Map<Integer, ? extends ItemStack> inv = getPlayer().getInventory().all(new Integer(id));
					done = 1;
					for (Map.Entry<Integer, ? extends ItemStack> stack: inv.entrySet()) {
						done += stack.getValue().getAmount();
					}
				}
				
				if (Math.round(target / 4) == done || Math.round(target / 2) == done || Math.round(target / 4 * 3) == done || Math.round(target) == Math.round(done)) {
					String message;
					
					message = ChatColor.GOLD + plugin.lang.getString("quest_progress", "Quest progress") + ": ";
					if (done >= target) {
						message += ChatColor.YELLOW + done.toString() + "/" + target.toString() + " " + things.get("display"); 					
					} else {
						message += ChatColor.AQUA + done.toString() + ChatColor.WHITE + "/" + target.toString() + " " + things.get("display"); 
					}

					getPlayer().sendMessage(message);
				}
			}
		}
	}
	
	public void brokeBlock(Integer id) {
		if (hasQuest == null)
			return;
		
		if (blockBroken.get(id) == null) {
			blockBroken.put(id, 1);
		} else {
			blockBroken.put(id, blockBroken.get(id) + 1);
		}
		
		checkProgress("break", id.toString(), blockBroken.get(id));
	}
	
	public void damagedBlock(Integer id) {
		if (hasQuest == null)
			return;
		
		if (blockDamaged.get(id) == null) {
			blockDamaged.put(id, 1);
		} else {
			blockDamaged.put(id, blockDamaged.get(id) + 1);
		}

		checkProgress("damage", id.toString(), blockDamaged.get(id));
	}
	
	public void placedBlock(Integer id) {
		if (hasQuest == null)
			return;
		
		if (blockPlaced.get(id) == null) {
			blockPlaced.put(id, 1);
		} else {
			blockPlaced.put(id, blockPlaced.get(id) + 1);
		}

		checkProgress("place", id.toString(), blockPlaced.get(id));
	}
	
	public void killedEntity(String id) {
		if (hasQuest == null)
			return;
		
		if (entityKilled.get(id) == null) {
			entityKilled.put(id, 1);
		} else {
			entityKilled.put(id, entityKilled.get(id) + 1);
		}

		checkProgress("kill", id, entityKilled.get(id));
	}
	
	public Integer getBlocksBroken(int id) {
		Integer ret = blockBroken.get(id);
		
		if (ret == null) {
			return 0;
		} else {
			return ret;
		}
	}

	public Integer getBlocksPlaced(int id) {
		Integer ret = blockPlaced.get(id);
		
		if (ret == null) {
			return 0;
		} else {
			return ret;
		}
	}

	public Integer getBlocksDamaged(int id) {
		Integer ret = blockDamaged.get(id);
		
		if (ret == null) {
			return 0;
		} else {
			return ret;
		}
	}
	
	public Integer getEntitysKilled(String id) {
		Integer ret = entityKilled.get(id);
		
		if (ret == null) {
			return 0;
		} else {
			return ret;
		}
	}
	
	public Integer getInventoryAmount(Integer id) {
		PlayerInventory inv = getPlayer().getInventory();
		
		Integer amount = 0;
		
		HashMap<Integer, ? extends ItemStack> itemInv = inv.all(id);
		for (Map.Entry<Integer, ? extends ItemStack> thisStack: itemInv.entrySet()) {
			amount += thisStack.getValue().getAmount();
		}
		
		return amount;
	}
	
	public void removeInventoryAmount(Integer id, Integer amount) {
		PlayerInventory inv = getPlayer().getInventory();
		
		Integer has = getInventoryAmount(id);
		inv.remove(id);
		if (has > amount)
			inv.addItem(new ItemStack(id, has - amount));
		
		return;
	}
	
	public void checkLevel() {
		Integer neededXP = plugin.config.getInt("level/" + questLevel, Integer.MAX_VALUE);
		if (experience >= neededXP) {
			questLevel++;
			
			if (plugin.config.getBoolean("global/announceLevelUp", false)) {
				plugin.getServer().broadcastMessage(ChatColor.GOLD + player + " " + plugin.lang.getString("reached_new_level", "just reached a new Quest Level") + " (" + ChatColor.LIGHT_PURPLE + questLevel + ChatColor.GOLD + ")");
				plugin.getServer().broadcastMessage(ChatColor.GOLD + plugin.lang.getString("quests_done", "Quests done") + ": " + ChatColor.LIGHT_PURPLE + questCount + ChatColor.GOLD + ", " + plugin.lang.getString("experience_gained", "Experience gained") + ": " + ChatColor.LIGHT_PURPLE + experience);
			}
		}	
	}
	
	public void giveQuest() {
		if (hasQuest != null) {
			getPlayer().sendMessage(plugin.lang.getString("already_has_quest", "You already have an active quest!"));
			return;
		}
		
		if (plugin.quests.levelQuests.get(questLevel) == null) {
			getPlayer().sendMessage(plugin.lang.getString("no_quests_found", "Could not find any suitable quests. Blame your admins!"));
			return;
		}
		
		if (plugin.ecoMethod != null && plugin.config.getInt("costs/giveQuest", 0) > 0) {
			MethodAccount account = plugin.ecoMethod.getAccount(player);
			if (account.balance() < plugin.config.getInt("costs/giveQuest", 0)) {
				getPlayer().sendMessage(plugin.lang.getString("insufficient_funds", "Could not give Quest: Insufficient funds."));
				return;
			} else {
				account.subtract(plugin.config.getInt("costs/giveQuest", 0));
			}
		}
		
		Random rand = new Random();
		hasQuest = plugin.quests.levelQuests.get(questLevel).get(rand.nextInt(plugin.quests.levelQuests.get(questLevel).size()));
		
		// reset tracking
		blockPlaced = new HashMap<Integer, Integer>();
		blockDamaged = new HashMap<Integer, Integer>();
		blockBroken = new HashMap<Integer, Integer>();
		entityKilled = new HashMap<String, Integer>();
		
		showQuest();
	}
	
	@SuppressWarnings("unchecked")
	public void showQuest() {
		if (hasQuest == null) {
			getPlayer().sendMessage(plugin.lang.getString("does_not_have_quest", "You don't have an active quest"));
			return;
		}
		
		getPlayer().sendMessage("Quest: " + ChatColor.GOLD + plugin.quests.quests.getString(hasQuest + "/name", ""));
		String infoLine = plugin.quests.quests.getString(hasQuest + "/infoStart", null);
		if (infoLine != null)
			getPlayer().sendMessage(ChatColor.GREEN + infoLine);

		getPlayer().sendMessage(plugin.lang.getString("objective", "Objective") + ":");
		Map<String, Object> targets = (Map<String, Object>)plugin.quests.quests.getNode(hasQuest + "/targets");
		for (Map.Entry<String, Object> thisTarget: targets.entrySet()) {
			Map<String, Object> things = (Map<String, Object>)thisTarget.getValue();
			
			Integer target = null;
			Integer done = null;
			String progress = null;
			
			if (things.get("type").equals("break")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}
				done = getBlocksBroken((Integer)things.get("id"));
				
				if (done >= target) {
					progress = ChatColor.YELLOW + done.toString() + "/" + target.toString() + " " + things.get("display"); 					
				} else {
					progress = ChatColor.AQUA + done.toString() + ChatColor.WHITE + "/" + target.toString() + " " + things.get("display"); 
				}
				
				getPlayer().sendMessage("  " + progress);
			}

			if (things.get("type").equals("place")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}
				done = getBlocksPlaced((Integer)things.get("id"));
				
				if (done >= target) {
					progress = ChatColor.YELLOW + done.toString() + "/" + target.toString() + " " + things.get("display"); 					
				} else {
					progress = ChatColor.AQUA + done.toString() + ChatColor.WHITE + "/" + target.toString() + " " + things.get("display"); 
				}
				
				getPlayer().sendMessage("  " + progress);

			}

			if (things.get("type").equals("damage")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}
				done = getBlocksDamaged((Integer)things.get("id"));
				
				if (done >= target) {
					progress = ChatColor.YELLOW + done.toString() + "/" + target.toString() + " " + things.get("display"); 					
				} else {
					progress = ChatColor.AQUA + done.toString() + ChatColor.WHITE + "/" + target.toString() + " " + things.get("display"); 
				}
				
				getPlayer().sendMessage("  " + progress);
			}

			if (things.get("type").equals("kill")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}
				done = getEntitysKilled((String)things.get("id"));
				
				if (done >= target) {
					progress = ChatColor.YELLOW + done.toString() + "/" + target.toString() + " " + things.get("display"); 					
				} else {
					progress = ChatColor.AQUA + done.toString() + ChatColor.WHITE + "/" + target.toString() + " " + things.get("display"); 
				}
				
				getPlayer().sendMessage("  " + progress);
			}
			
			if (things.get("type").equals("collect")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}
				done = getInventoryAmount((Integer)things.get("id"));
				
				if (done >= target) {
					progress = ChatColor.YELLOW + done.toString() + "/" + target.toString() + " " + things.get("display"); 					
				} else {
					progress = ChatColor.AQUA + done.toString() + ChatColor.WHITE + "/" + target.toString() + " " + things.get("display"); 
				}
				
				getPlayer().sendMessage("  " + progress);
			}
		}

		getPlayer().sendMessage(plugin.lang.getString("reward", "Reward") + ":");
		
		if (plugin.ecoMethod != null && plugin.quests.quests.getInt(hasQuest + "/rewards/money", 0) > 0) {
			getPlayer().sendMessage("  " + ChatColor.GREEN + plugin.ecoMethod.format(plugin.quests.quests.getInt(hasQuest + "/rewards/money", 0)));
		}
		
		if (plugin.quests.quests.getInt(hasQuest + "/rewards/exp", 0) > 0) {
			getPlayer().sendMessage("  " + ChatColor.GREEN + plugin.quests.quests.getString(hasQuest + "/rewards/exp", "0") + " " + plugin.lang.getString("experience", "Experience"));
		}
		
		Map<String, Object> rewards = (Map<String, Object>)plugin.quests.quests.getNode(hasQuest + "/rewards/items");
		if (rewards != null)
		for (Map.Entry<String, Object> thisItem: rewards.entrySet()) {
			Map<String, Object> items = (Map<String, Object>)thisItem.getValue();
			getPlayer().sendMessage("  " + ChatColor.WHITE + items.get("amount") + " " + ChatColor.GREEN + items.get("display"));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void finishQuest() {
		if (hasQuest == null) {
			getPlayer().sendMessage(plugin.lang.getString("does_not_have_quest", "You don't have an active quest"));
			return;
		}

		Integer target = null;
		Integer done = null;
		
		Map<String, Object> targets = (Map<String, Object>)plugin.quests.quests.getNode(hasQuest + "/targets");
		for (Map.Entry<String, Object> thisTarget: targets.entrySet()) {
			Map<String, Object> things = (Map<String, Object>)thisTarget.getValue();
			
			if (things.get("type").equals("break")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}
				done = getBlocksBroken((Integer)things.get("id"));

				if (target > done) {
					getPlayer().sendMessage(ChatColor.RED + plugin.lang.getString("not_finished", "Your quest isn't done! Type:") + " /quest info");
					return;
				}
			}

			if (things.get("type").equals("place")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}
				done = getBlocksPlaced((Integer)things.get("id"));

				if (target > done) {
					getPlayer().sendMessage(ChatColor.RED + plugin.lang.getString("not_finished", "Your quest isn't done! Type:") + " /quest info");
					return;
				}
			}

			if (things.get("type").equals("damage")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}
				done = getBlocksDamaged((Integer)things.get("id"));

				if (target > done) {
					getPlayer().sendMessage(ChatColor.RED + plugin.lang.getString("not_finished", "Your quest isn't done! Type:") + " /quest info");
					return;
				}
			}

			if (things.get("type").equals("kill")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}
				done = getEntitysKilled((String)things.get("id"));

				if (target > done) {
					getPlayer().sendMessage(ChatColor.RED + plugin.lang.getString("not_finished", "Your quest isn't done! Type:") + " /quest info");
					return;
				}
			}
			
			if (things.get("type").equals("collect")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}
				done = getInventoryAmount((Integer)things.get("id"));

				if (target > done) {
					getPlayer().sendMessage(ChatColor.RED + plugin.lang.getString("not_finished", "Your quest isn't done! Type:") + " /quest info");
					return;
				}
			}			
		}

		for (Map.Entry<String, Object> thisTarget: targets.entrySet()) {
			Map<String, Object> things = (Map<String, Object>)thisTarget.getValue();
			
			if (things.get("type").equals("collect")) {
				if (plugin.config.getBoolean("global/scaleQuestsWithLevel", false)) {
					target = (Integer)things.get("amount") * (questLevel + 1);
				} else {
					target = (Integer)things.get("amount");
				}				
				removeInventoryAmount((Integer)things.get("id"), target);
			}
		}
		
		getPlayer().sendMessage(ChatColor.DARK_PURPLE + "**** " + plugin.lang.getString("quest_finished", "Quest finished!") + " ****");

		if (plugin.quests.quests.getString(hasQuest + "/infoEnd", null) != null)
			getPlayer().sendMessage(ChatColor.GREEN + plugin.quests.quests.getString(hasQuest + "/infoEnd", ""));
		
		if (plugin.quests.quests.getInt(hasQuest + "/rewards/exp", 0) > 0) {
			getPlayer().sendMessage(ChatColor.AQUA + plugin.lang.getString("reward_gained", "**You have been rewarded with") + " " + ChatColor.WHITE + plugin.quests.quests.getInt(hasQuest + "/rewards/exp", 0) + " " + ChatColor.AQUA + plugin.lang.getString("experience", "Experience"));			
		}
		
		if (plugin.ecoMethod != null && plugin.quests.quests.getInt(hasQuest + "/rewards/money", 0) > 0) {
			plugin.ecoMethod.getAccount(player).add(plugin.quests.quests.getInt(hasQuest + "/rewards/money", 0));
			moneyGained += plugin.quests.quests.getInt(hasQuest + "/rewards/money", 0);
			getPlayer().sendMessage(ChatColor.AQUA + plugin.lang.getString("reward_gained", "**You have been rewarded with") + " " + ChatColor.WHITE + plugin.ecoMethod.format(plugin.quests.quests.getInt(hasQuest + "/rewards/money", 0)));
		}

		Map<String, Object> rewards = (Map<String, Object>)plugin.quests.quests.getNode(hasQuest + "/rewards/items");
		if (rewards != null) {
			PlayerInventory inv = getPlayer().getInventory();
			
			for (Map.Entry<String, Object> thisItem: rewards.entrySet()) {
				Map<String, Object> items = (Map<String, Object>)thisItem.getValue();
				inv.addItem(new ItemStack((Integer)items.get("id"), (Integer)items.get("amount")));
				getPlayer().sendMessage(ChatColor.AQUA + plugin.lang.getString("reward_gained", "**You have been rewarded with") + " " + ChatColor.WHITE + items.get("amount") + " " + ChatColor.GREEN + items.get("display"));
			}
		}
		
		questCount += 1;
		
		if (plugin.quests.quests.getInt(hasQuest + "/rewards/exp", 0) > 0) {
			experience += plugin.quests.quests.getInt(hasQuest + "/rewards/exp", 0);
			checkLevel();
		}
		
		hasQuest = null;
	}
	
	public void dropQuest() {
		if (hasQuest == null) {
			getPlayer().sendMessage(ChatColor.RED + plugin.lang.getString("does_not_have_quest", "You don't have an active quest"));
			return;
		}
		
		if (plugin.ecoMethod != null && plugin.config.getInt("costs/dropQuest", 0) > 0) {
			MethodAccount account = plugin.ecoMethod.getAccount(player);
			if (account.balance() < plugin.config.getInt("costs/dropQuest", 0)) {
				getPlayer().sendMessage(ChatColor.RED + plugin.lang.getString("insufficient_funds", "Could not drop Quest: Insufficient funds."));
				return;
			} else {
				account.subtract(plugin.config.getInt("costs/dropQuest", 0));
				hasQuest = null;
				getPlayer().sendMessage(ChatColor.GREEN + plugin.lang.getString("dropped_quest", "Successfully dropped your quest."));
			}
		} else {
			getPlayer().sendMessage(ChatColor.GREEN + plugin.lang.getString("dropped_quest", "Successfully dropped your quest."));
			hasQuest = null;
		}
	}
		
	private Player getPlayer() {
		return plugin.getServer().getPlayer(player);
	}
}
