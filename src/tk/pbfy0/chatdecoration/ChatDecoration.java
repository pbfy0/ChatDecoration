package tk.pbfy0.chatdecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatDecoration extends JavaPlugin {
	private static final Pattern pattern = Pattern.compile(":(\\S+?):(?=\\S)(.+?)(?=\\S)::");
	private ChatDecorationListener listener;
	private List<ChatColor> colors;
	private static List<ChatColor> rainbow = Arrays.asList(ChatColor.DARK_RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.DARK_GREEN, ChatColor.DARK_BLUE, ChatColor.DARK_PURPLE);
	private Random random;
	private PluginManager pm;
	@Override
	public void onEnable(){
		listener = new ChatDecorationListener(this);
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
		getLogger().info("ChatDecoration enabled");
		
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
				if(sender.hasPermission("chatcolor.color.rainbow")){
					tempc.append(rainbow("rainbow"));
				}
				if(tempc.length() != 0){
					//tempc.deleteCharAt(tempc.length()-1);
					sender.sendMessage(tempc.toString());
				}
				
				return true;
			}
			if(args.length == 2 && args[0].equals("reference")){
				ChatColor color;
				try{
					color = ChatColor.valueOf(args[1].toUpperCase());
				}catch(IllegalArgumentException e){
					sender.sendMessage("Unknown color code \"" + args[1] + "\"");
					return true;
				}
				sender.sendMessage(ChatColor.RED + color.toString().replace(ChatColor.COLOR_CHAR, '&'));
				return true;
			}
		}
		
		return false;
	}
	
	protected String rainbow(String input){
		StringBuilder s = new StringBuilder();
		int rs = rainbow.size();
		for(int i = 0, n = input.length() ; i < n ; i++){
			s.append(rainbow.get(i % rs));
			s.append(input.charAt(i));
		}
		return s.toString();
	}
	
	public String colorize(String input){
		return colorize(input, null);
	}
	
	public String colorize(String text, Player player){
		if(player == null || player.hasPermission("chatdecoration.style.bold"))
			text = text.replaceAll("==((?=\\S).+?(?=\\S))==", ChatColor.BOLD + "$1" + ChatColor.RESET);
		if(player == null || player.hasPermission("chatdecoration.style.strikethrough"))
			text = text.replaceAll("--((?=\\S).+?(?=\\S))--", ChatColor.STRIKETHROUGH + "$1" + ChatColor.RESET);
		if(player == null || player.hasPermission("chatdecoration.style.underline"))
			text = text.replaceAll("__((?=\\S).+?(?=\\S))__", ChatColor.UNDERLINE + "$1" + ChatColor.RESET);
		if(player == null || player.hasPermission("chatdecoration.style.italics"))
			text = text.replaceAll("\\*\\*((?=\\S).+?(?=\\S))\\*\\*", ChatColor.ITALIC + "$1" + ChatColor.RESET);
		text = findColorCodes(text, player);
		return text;

	}
	
	private String findColorCodes(String input, Player player){
		StringBuffer output = new StringBuffer();
		Matcher matcher = pattern.matcher(input);
		while(matcher.find()){
			String resume = ChatColor.getLastColors(input.substring(0, matcher.start()));
			if(resume.isEmpty()) resume = ChatColor.RESET.toString();
			String colorString = matcher.group(1);
			String text = matcher.group(2);
			ChatColor color;
			try{
				color = ChatColor.valueOf(colorString.toUpperCase());
			}catch(IllegalArgumentException ex){
				if(colorString.toLowerCase().equals("rainbow")){
					matcher.appendReplacement(output, rainbow(text) + resume);
				}else{
					if(player != null) player.sendMessage(ChatColor.RED + "Unknown color code \"" + colorString + "\".");
					matcher.appendReplacement(output, text);
				}
				continue;
			}
			String rep = player == null || player.hasPermission("chatdecoration.color." + color.name().toLowerCase()) ?
					color + text + resume : text;
			matcher.appendReplacement(output, rep);
		}
		matcher.appendTail(output);
		return output.toString();
	}

	
	@Override
	public void onDisable(){
		for(ChatColor c : colors){
			pm.removePermission("chatdecoration.color." + c.name().toLowerCase());
		}
		colors.clear();
	}

}
