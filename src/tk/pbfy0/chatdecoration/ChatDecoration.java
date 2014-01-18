package tk.pbfy0.chatdecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatDecoration extends JavaPlugin {
	private ChatDecorationListener listener;
	private List<ChatColor> colors;
	private Random random;
	private PluginManager pm;
	@Override
	public void onEnable(){
		getLogger().info("ChatDecoration enabled");
		listener = new ChatDecorationListener();
		pm = getServer().getPluginManager();
		pm.registerEvents(listener, this);
		colors = getColors();
		Permission allcolors = pm.getPermission("chatdecoration.color.*");
		if(allcolors == null){ // why is this required??
			allcolors = new Permission("chatcolor.color.*");
		}
		for(ChatColor c : colors){
			Permission p = new Permission("chatdecoration.color." + c.name().toLowerCase(), PermissionDefault.TRUE);
			p.addParent(allcolors, true);
			pm.addPermission(p);
		}
		random = new Random();
		
	}
	
	private List<ChatColor> getColors(){
		List<ChatColor> colors_ = new ArrayList<ChatColor>(Arrays.asList(ChatColor.values()));
		colors_.remove(ChatColor.RESET);
		//colors_.remove(ChatColor.MAGIC);
		colors_.remove(ChatColor.UNDERLINE);
		colors_.remove(ChatColor.ITALIC);
		colors_.remove(ChatColor.STRIKETHROUGH);
		colors_.remove(ChatColor.BOLD);
		return colors_;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("cd")){
			if(args.length == 1 && args[0].equals("help")){
				if(sender.hasPermission("chatdecoration.style.underline"))
					sender.sendMessage("__text__: " + ChatColor.UNDERLINE + "text" + ChatColor.RESET);
				if(sender.hasPermission("chatdecoration.style.bold"))
					sender.sendMessage("==text==: " + ChatColor.BOLD + "text" + ChatColor.RESET);
				if(sender.hasPermission("chatdecoration.style.strikethrough"))
					sender.sendMessage("--text--: " + ChatColor.STRIKETHROUGH + "text" + ChatColor.RESET);
				if(sender.hasPermission("chatdecoration.style.italics"))
					sender.sendMessage("**text**: " + ChatColor.ITALIC + "text" + ChatColor.RESET);
				List<ChatColor> tcolor;
				if(sender.hasPermission("chatdecoration.color.*")){
					tcolor = colors;
				}else{
					tcolor = new ArrayList<ChatColor>();
					for(ChatColor c : colors){
						if(sender.hasPermission("chatdecoration.color." + c.name().toLowerCase()))
							tcolor.add(c);
					}
				}
				if(tcolor.size() > 0){
					sender.sendMessage(":color:text:: colors the text with the color given");
					ChatColor color = tcolor.get(random.nextInt(tcolor.size()));
					sender.sendMessage("For example, :" + color.name().toLowerCase() + ":text:: becomes " + color + "text" + ChatColor.RESET);
					sender.sendMessage("For reference for color names, use /cd colors");
				}
				return true;
			}
			if(args.length == 1 && args[0].equals("colors")){
				StringBuilder tempc = new StringBuilder();
				for(ChatColor c : colors){
					if(sender.hasPermission("chatdecoration.color." + c.name().toLowerCase()))
							tempc.append(c + c.name().toLowerCase() + ChatColor.RESET + " ");
				}
				if(tempc.length() != 0){
					tempc.deleteCharAt(tempc.length()-1);
					sender.sendMessage(tempc.toString());
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void onDisable(){
		for(ChatColor c : colors){
			pm.removePermission("chatdecoration.color." + c.name().toLowerCase());
		}
	}

}
