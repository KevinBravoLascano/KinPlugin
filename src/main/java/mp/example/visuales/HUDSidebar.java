package mp.example.visuales;

import mp.example.classes.Equipo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HUDSidebar {

    // Guardamos el scoreboard por jugador para no recrearlo cada click
    private static final Map<UUID, Scoreboard> scoreboards = new HashMap<>();

    public static void update(Equipo equipo, Player player) {
        Scoreboard board = scoreboards.get(player.getUniqueId());
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (board == null) {
            board = manager.getNewScoreboard();
            scoreboards.put(player.getUniqueId(), board);
        }

        Objective obj = board.getObjective("equipoHUD");
        if (obj == null) {
            obj = board.registerNewObjective("equipoHUD", "dummy", "§aEquipo: §b" + equipo.getNombre());
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        } else {
            obj.setDisplayName("§aEquipo: §b" + equipo.getNombre());
            // Limpiar líneas antiguas
            for (String line : board.getEntries()) {
                board.resetScores(line);
            }
        }

        int score = equipo.getPlayers().size() + 1;

        for (UUID id : equipo.getPlayers().keySet()) {
            Player p = Bukkit.getPlayer(id);
            String name = (p != null) ? p.getName() : "Desconocido";
            int clicks = equipo.getClicks(p);
            Score line = obj.getScore("§e" + name + ": §b" + clicks);
            line.setScore(score);
            score--;
        }

        player.setScoreboard(board);
    }
}

