package mp.example.visuales;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.Collection;

public class GameHUD {

    public static void updateTimer(int secondsLeft, boolean luzRoja, Collection<Player> vivos) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();

        Objective obj = board.registerNewObjective(
                "timerHUD",
                "dummy",
                luzRoja ? "§c🔴 LUZ ROJA" : "§a🟢 LUZ VERDE"
        );
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        int score = vivos.size() + 3;

        obj.getScore("§fTiempo: §e" + secondsLeft + "s").setScore(score--);
        obj.getScore("§7──────────").setScore(score--);

        for (Player p : vivos) {
            obj.getScore("§7• " + p.getName()).setScore(score--);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(board);
        }
    }

    public static void clearTimer() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }
}
