package mp.example.listeners;
import mp.example.KinPlugin;
import mp.example.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener implements Listener {
    private KinPlugin plugin;

    public PlayerListener(KinPlugin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void OnChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();

        if(message.toLowerCase().contains("papaya")){
            p.giveExp(10);
            p.sendMessage(MessageUtils.getColoredMessage("pa ti crack"));

        }

    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) { //evento captado para hace el minijuego luz roja  luz verde
        Player p = e.getPlayer();

        if (!plugin.coords.containsKey(p.getUniqueId())) return;

        double[] start = plugin.coords.get(p.getUniqueId());
        int x0 = (int) start[0];
        int z0 = (int) start[1];

        Location loc = e.getTo();

        if(loc==null){
            return;
        }

        int x= loc.getBlockX();
        int z= loc.getBlockZ();

        if (x != x0 || z != z0) {
            p.sendMessage("§cTe moviste → eliminado");
            p.setGameMode(GameMode.SPECTATOR);

            plugin.coords.remove(p.getUniqueId());

            // Mensaje a todos los jugadores
            Bukkit.broadcastMessage("§cjugador " + p.getName() + " ¡se movió y ha sido eliminado!");
        }

    }

}
