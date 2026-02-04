package mp.example.juegos;

import mp.example.classes.Contador;
import mp.example.classes.GameType;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * GameManager centraliza el control de todos los minijuegos.
 * Maneja jugadores activos, seguros, contadores y estados de juego.
 */
public class GameManager {

    private final Map<UUID, GameType> playerGames = new HashMap<>();
    private final Map<GameType, Set<UUID>> safePlayers = new HashMap<>();
    private final Map<GameType, Contador> contadores = new HashMap<>();

    private boolean isRed = false; // Solo para RedLight

    /* =======================
       CONTROL DEL JUEGO
       ======================= */

    public void startGame(GameType type, Contador contador) {
        // Inicializa jugadores seguros
        safePlayers.put(type, new HashSet<>());

        // Guarda el contador del juego
        contadores.put(type, contador);

        contador.start();
    }

    public void stopGame(GameType type) {
        // Limpia jugadores seguros
        Set<UUID> safe = safePlayers.get(type);
        if (safe != null) safe.clear();

        // Para y elimina contador
        Contador contador = contadores.get(type);
        if (contador != null) {
            contador.stop();
            contadores.remove(type);
        }

        // Saca jugadores del minijuego
        playerGames.entrySet().removeIf(entry -> entry.getValue() == type);
    }

    /* =======================
       RED LIGHT
       ======================= */

    public void setRed(boolean red) {
        isRed = red;
    }

    public boolean isRedLight() {
        return isRed;
    }

    /* =======================
       JUGADORES Y MINIJUEGOS
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

    /* =======================
       SAFE PLAYERS
       ======================= */

    public void markPlayerSafe(Player player, GameType type) {
        safePlayers.computeIfAbsent(type, k -> new HashSet<>())
                .add(player.getUniqueId());
    }

    public void clearSafePlayers(GameType type) {
        Set<UUID> set = safePlayers.get(type);
        if (set != null) set.clear();
    }

    public boolean isPlayerSafe(Player player, GameType type) {
        Set<UUID> set = safePlayers.get(type);
        return set != null && set.contains(player.getUniqueId());
    }

    /* =======================
       JUGADORES VIVOS
       ======================= */

    public List<Player> getPlayersAlive(GameType type) {
        List<Player> alive = new ArrayList<>();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (isInGame(p, type)
                    && !isPlayerSafe(p, type)
                    && (p.getGameMode() == GameMode.SURVIVAL
                    || p.getGameMode() == GameMode.ADVENTURE)) {
                alive.add(p);
            }
        }

        return alive;
    }

    /* =======================
       GET CONTADOR
       ======================= */

    public Contador getContador(GameType type) {
        return contadores.get(type);
    }

    /* =======================
       JUEGO ACTIVO
       ======================= */

    public boolean isGameRunning() {
        return !contadores.isEmpty();
    }
}

