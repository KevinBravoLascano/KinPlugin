package mp.example.listeners;

import mp.example.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void OnChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();

        if(message.toLowerCase().contains("papaya")){
            p.giveExp(10);
            p.sendMessage(MessageUtils.getColoredMessage("pa ti crack"));
        }
    }
}
