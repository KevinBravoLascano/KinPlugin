package mp.example.visuales;

import mp.example.classes.Equipo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.UUID;

public class HUDSidebar {

    public static void update(Equipo equipo, Player player) {

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective("equipoHUD", "dummy", "§aEquipo: §b" + equipo.getNombre());
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int score = equipo.getPlayers().size() + 1; // para orden descendente

        // Mostrar todos los jugadores y sus clics
        for (UUID id : equipo.getPlayers().keySet()) {
            Player p = Bukkit.getPlayer(id);

            String name = (p != null) ? p.getName() : "Desconocido";

            int clicks = equipo.getPlayers().get(id);

            // Línea del jugador
            Score line = obj.getScore("§e" + name + ": §b" + clicks);
            line.setScore(score);

            score--;
        }

        player.setScoreboard(board);
    }
}
