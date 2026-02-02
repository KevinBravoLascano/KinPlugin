package mp.example.juegos;

import mp.example.KinPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public class RedLightManager {

    private final KinPlugin plugin;

    public RedLightManager(KinPlugin plugin) {
        this.plugin = plugin;
    }

    public void onMove(Player p, PlayerMoveEvent e) {

        Location loc = e.getTo();
        if (loc == null) return;

        var gm = plugin.gameManager;

        // Zona segura
        if (loc.getBlock().getRelative(0, -1, 0).getType() == Material.RED_WOOL
                && !gm.isPlayerSafe(p)) {
            gm.markPlayerSafe(p);
            p.sendMessage("§a¡Zona segura alcanzada!");
            return;
        }

        // Luz roja
        if (gm.isRedLight() && !gm.isPlayerSafe(p)) {
            eliminarJugador(p, loc);
        }
    }

    private void eliminarJugador(Player p, Location loc) {
        double[] start = plugin.coords.getOrDefault(
                p.getUniqueId(),
                new double[]{loc.getBlockX(), loc.getBlockZ()}
        );

        int x0 = (int) start[0];
        int z0 = (int) start[1];

        if (loc.getBlockX() != x0 || loc.getBlockZ() != z0) {
            p.sendMessage("§cTe moviste → eliminado");
            p.setGameMode(GameMode.SPECTATOR);
            plugin.coords.remove(p.getUniqueId());
            Bukkit.broadcastMessage(
                    "§cJugador " + p.getName() + " ¡se movió y ha sido eliminado!"
            );
        }
    }
}

