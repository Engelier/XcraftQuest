package me.Engelier.XcraftQuest;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijikokun.register.payment.Methods;

public class XcraftQuestPluginListener extends ServerListener {
	private XcraftQuest plugin = null;
	private Methods methods = null;
	
	public XcraftQuestPluginListener(XcraftQuest instance) {
		plugin = instance;
		methods = new Methods();
	}
	
	public void checkForPermissions() {
		Plugin permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
		
		if (permissions != null && permissions.isEnabled())
			plugin.permissions = ((Permissions)permissions).getHandler();
	}
	
	@Override
	public void onPluginEnable(PluginEnableEvent event) {
		Plugin thisPlugin = event.getPlugin();
		
		if (!methods.hasMethod()) {
			if (methods.setMethod(thisPlugin)) {
				plugin.ecoMethod = methods.getMethod();
				plugin.log.info(plugin.getNameBrackets() + "Found payment method (" + plugin.ecoMethod.getName() + " version: " + plugin.ecoMethod.getVersion() + ")");
			}
		}
		
		if (thisPlugin.getDescription().getName().equals("Permissions")) {
			plugin.permissions = ((Permissions)thisPlugin).getHandler();
			plugin.log.info(plugin.getNameBrackets() + "hooked into Permissions " + thisPlugin.getDescription().getVersion());
		}
	}
	
	@Override
	public void onPluginDisable(PluginDisableEvent event) {
		Plugin thisPlugin = event.getPlugin();
		
		if (methods != null && methods.hasMethod()) {
			Boolean check = methods.checkDisabled(thisPlugin);
			
			if (check) {
				plugin.ecoMethod = null;
				plugin.log.info(plugin.getNameBrackets() + "Payment method was disabled. Disabling money rewards.");
			}
		}

		if (thisPlugin.getDescription().getName().equals("Permissions")) {
			plugin.permissions = null;
			plugin.log.info(plugin.getNameBrackets() + "lost Permissions plugin.");
		}
	}
}
