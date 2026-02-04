package mp.example.interficies;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

public interface MiniGame {
    void start(String[] args);            // Inicia el juego
    void stop();             // Para el juego

    void onTick();           // Lógica periódica (temporizador, HUD, etc.)
    boolean isRunning();     // Saber si el juego está activo

    void onMove(Player p, PlayerMoveEvent e);
}
