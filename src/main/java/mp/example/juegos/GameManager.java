package mp.example.juegos;

import mp.example.classes.Contador;
import mp.example.classes.GameType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

public class GameManager {

    private boolean gameRunning = false;
    private boolean isRed = false;

    private final Set<UUID> safePlayers = new HashSet<>();
    private final Map<UUID, GameType> playerGames = new HashMap<>();

    private Contador contador;

    /* =======================
       CONTROL DEL JUEGO
       ======================= */

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

        // Sacar a todos del minijuego
        playerGames.clear();
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    /* =======================
       RED LIGHT / GREEN LIGHT
       ======================= */

    public void setRed(boolean red) {
        isRed = red;
    }

    public boolean isRedLight() {
        return isRed;
    }

    /* =======================
       JUGADORES
       ======================= */

    public void setPlayerGame(Player player, GameType game) {
        playerGames.put(player.getUniqueId(), game);
    }

    public GameType getPlayerGame(Player player) {
        return playerGames.getOrDefault(player.getUniqueId(), GameType.NONE);
    }

    public boolean isInGame(Player player, GameType game) {
        return getPlayerGame(player) == game;
    }

    public void markPlayerSafe(Player player) {
        safePlayers.add(player.getUniqueId());
    }

    public void clearSafePlayers() {
        safePlayers.clear();
    }

    public boolean isPlayerSafe(Player player) {
        return safePlayers.contains(player.getUniqueId());
    }

    public List<Player> getPlayersAlive() {
        List<Player> alive = new ArrayList<>();

        if (!gameRunning) return alive;

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!safePlayers.contains(p.getUniqueId()) &&
                    (p.getGameMode() == GameMode.SURVIVAL ||
                            p.getGameMode() == GameMode.ADVENTURE)) {
                alive.add(p);
            }
        }
        return alive;
    }
}

