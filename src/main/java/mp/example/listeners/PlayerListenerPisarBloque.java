package mp.example.listeners;

import mp.example.KinPlugin;
import mp.example.classes.GameType;
import mp.example.interficies.MiniGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Listener principal de movimientos de jugadores.
 * Filtra movimientos, evita exceso de procesamiento y delega a minijuegos.
 */
public class PlayerListenerPisarBloque implements Listener {

    private final KinPlugin plugin;

    public PlayerListenerPisarBloque(KinPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        // 🔹 FILTRO 1: mismo bloque (ahorro de rendimiento)
        if (mismoBloque(e)) return;

        Player player = e.getPlayer();

        // 🔹 FILTRO 2: juego activo
        if (!plugin.gameManager.isGameRunning()) return;

        // 🔹 FILTRO 3: obtener minijuego actual del jugador
        GameType type = plugin.gameManager.getPlayerGame(player);
        MiniGame miniGame = switch (type) {
            case RED_LIGHT -> plugin.redLightManager;
            case HABITACIONES -> plugin.habitacionesManager;
            default -> null;
        };

        if (miniGame != null && miniGame.isRunning()) {
            miniGame.onMove(player, e);
        }
    }

    /* =========================
       UTILIDAD: MISMO BLOQUE
       ========================= */
    private boolean mismoBloque(PlayerMoveEvent e) {
        if (e.getFrom() == null || e.getTo() == null) return true;

        return e.getFrom().getBlockX() == e.getTo().getBlockX()
                && e.getFrom().getBlockY() == e.getTo().getBlockY()
                && e.getFrom().getBlockZ() == e.getTo().getBlockZ();
    }
}
