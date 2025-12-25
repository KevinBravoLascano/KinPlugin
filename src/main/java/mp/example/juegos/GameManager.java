package mp.example.juegos;


import mp.example.classes.Contador;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {

    private boolean gameRunning = false;
    private final Set<Player> safePlayers = new HashSet<>();
    private Contador contador;
    private boolean isRed=false;

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

    public void setRed(boolean red) {
        isRed = red;
    }
    public boolean isRedLight(){
        return isRed;
    }

    public void markPlayerSafe(Player player) {
        safePlayers.add(player);
    }

    public boolean isPlayerSafe(Player player) {
        return safePlayers.contains(player);
    }
    public List<Player> getPlayersAlive() {
        List<Player> alive = new ArrayList<>();

        for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
            // Solo jugadores online, en modo supervivencia o aventura y que no estén marcados como seguros
            if (gameRunning && !safePlayers.contains(p) &&
                    (p.getGameMode() == org.bukkit.GameMode.SURVIVAL || p.getGameMode() == org.bukkit.GameMode.ADVENTURE)) {
                alive.add(p);
            }
        }

        return alive;
    }


}
