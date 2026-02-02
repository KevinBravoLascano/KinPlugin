package mp.example.listeners;

import mp.example.KinPlugin;
import mp.example.classes.GameType;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListenerPisarBloque implements Listener {

    private final KinPlugin plugin;

    public PlayerListenerPisarBloque(KinPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        // 🔹 FILTRO 1: mismo bloque (rendimiento)
        if (mismoBloque(e)) return;

        Player p = e.getPlayer();

        // 🔹 FILTRO 2: jugador no está en ningún minijuego
        if (!plugin.gameManager.isGameRunning()) return;

        // 🔹 FILTRO 3: delegar según minijuego
        switch (plugin.gameManager.getPlayerGame(p)) {
            case RED_LIGHT -> handleRedLightMove(p, e);
            case HABITACIONES -> plugin.habitacionesManager.onMove(p);
            default -> { }
        }
    }

    /* =========================
       RED LIGHT / GREEN LIGHT
       ========================= */
    private void handleRedLightMove(Player p, PlayerMoveEvent e) {
        plugin.redLightManager.onMove(p, e);
    }

    /* =========================
       UTILIDAD CLAVE
       ========================= */
    private boolean mismoBloque(PlayerMoveEvent e) {
        if (e.getFrom() == null || e.getTo() == null) return true;

        return e.getFrom().getBlockX() == e.getTo().getBlockX()
                && e.getFrom().getBlockY() == e.getTo().getBlockY()
                && e.getFrom().getBlockZ() == e.getTo().getBlockZ();
    }
}
