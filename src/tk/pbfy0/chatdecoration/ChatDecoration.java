package tk.pbfy0.chatdecoration;

import org.bukkit.plugin.java.JavaPlugin;

public final class ChatDecoration extends JavaPlugin {
	@Override
	public void onEnable(){
		getLogger().info("ChatDecoration enabled");
		getServer().getPluginManager().registerEvents(new ChatDecorationListener(), this);
	}
	
	@Override
	public void onDisable(){
		
	}
}
