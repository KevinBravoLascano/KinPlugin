package mp.example.juegos;


import mp.example.classes.Contador;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class GameManager {

    private boolean gameRunning = false;
    private final Set<Player> safePlayers = new HashSet<>();
    private Contador contador;

    public void startGame(Contador contador) {
        gameRunning = true;
        safePlayers.clear();
        this.contador = contador;
        contador.start();
    }

    public void stopGame() {
        gameRunning = false;
        safePlayers.clear();
        if (contador != null) {
            contador.stop();
            contador = null;
        }
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public void markPlayerSafe(Player player) {
        safePlayers.add(player);
    }

    public boolean isPlayerSafe(Player player) {
        return safePlayers.contains(player);
    }
}
