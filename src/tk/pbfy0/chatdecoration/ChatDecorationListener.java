package tk.pbfy0.chatdecoration;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.ChatColor;

public final class ChatDecorationListener implements Listener {
	private static Pattern pattern = Pattern.compile(":(\\S+):(?=\\S)(.+?)(?=\\S)::");
	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		if(event.isCancelled())
			return;
		if(!event.getPlayer().hasPermission("chatdecoration.use"))
			return;
		String text = event.getMessage();
		text = text.replaceAll("==((?=\\S).+?(?=\\S))==", ChatColor.BOLD + "$1" + ChatColor.RESET);
		text = text.replaceAll("--((?=\\S).+?(?=\\S))--", ChatColor.STRIKETHROUGH + "$1" + ChatColor.RESET);
		text = text.replaceAll("__((?=\\S).+?(?=\\S))__", ChatColor.UNDERLINE + "$1" + ChatColor.RESET);
		text = text.replaceAll("\\*\\*((?=\\S).+?(?=\\S))\\*\\*", ChatColor.ITALIC + "$1" + ChatColor.RESET);
		text = findColorCodes(text, event.getPlayer());
		event.setMessage(text);
	}
	private String findColorCodes(String input, Player player){
		StringBuffer output = new StringBuffer();
		Matcher matcher = pattern.matcher(input);
		while(matcher.find()){
			ChatColor color;
			try{
				color = ChatColor.valueOf(matcher.group(1).toUpperCase());
			}catch(IllegalArgumentException ex){
				player.sendMessage(ChatColor.RED + "Unknown color code \"" + matcher.group(1) + "\".");
				matcher.appendReplacement(output, matcher.group(2));
				continue;
			}
			String rep = color + matcher.group(2) + ChatColor.RESET;
			matcher.appendReplacement(output, rep);
		}
		matcher.appendTail(output);
		return output.toString();
	}
}