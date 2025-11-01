package mp.example.commands;

import mp.example.utils.MessageUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender Sender, Command command, String alias, String[] args) {
        if(!(Sender instanceof Player)){
            //lo hace el comando la consola
            Sender.sendMessage(MessageUtils.getColoredMessage("&cOnly players can run this command!"));
            return true;
        }
        //jugador hace el comando
        Player player = (Player) Sender;
        Sender.sendMessage(MessageUtils.getColoredMessage("dale bro"));
        return true;
    }
}
