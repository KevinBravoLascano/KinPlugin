package mp.example.listeners;
import mp.example.KinPlugin;
import mp.example.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class

PlayerListener implements Listener {
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
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location loc = e.getTo();
        if (loc == null) return;

        Material blockUnder = loc.getBlock().getRelative(0, -1, 0).getType();

        // Si pisa RED_WOOL, siempre se marca seguro
        if (blockUnder == Material.RED_WOOL && !plugin.gameManager.isPlayerSafe(p)) {
            plugin.gameManager.markPlayerSafe(p);
            p.sendMessage("§a¡Zona segura alcanzada!");
        }

        // Solo elimina si la luz es roja y el jugador no está seguro
        if (plugin.gameManager.isRedLight() && !plugin.gameManager.isPlayerSafe(p)) {
            double[] start = plugin.coords.getOrDefault(p.getUniqueId(), new double[]{loc.getBlockX(), loc.getBlockZ()});
            int x0 = (int) start[0];
            int z0 = (int) start[1];

            int x = loc.getBlockX();
            int z = loc.getBlockZ();

            if (x != x0 || z != z0) {
                p.sendMessage("§cTe moviste → eliminado");
                p.setGameMode(GameMode.SPECTATOR);
                plugin.coords.remove(p.getUniqueId());
                Bukkit.broadcastMessage("§cJugador " + p.getName() + " ¡se movió y ha sido eliminado!");
            }
        }
    }



}
