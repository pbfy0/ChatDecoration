package tk.pbfy0.chatdecoration;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatDecorationListener implements Listener {
	private ChatDecoration plugin;
	public ChatDecorationListener(ChatDecoration plugin_){
		plugin = plugin_;
	}
	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onChat(AsyncPlayerChatEvent event){
		Player player = event.getPlayer();
		event.setMessage(plugin.colorize(event.getMessage(), player));
	}
}
