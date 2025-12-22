package mp.example.classes;

import mp.example.KinPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class Contador {
    private final KinPlugin plugin;
    private BukkitTask task;
    private int secondsLeft;

    private final Runnable onFinish;
    private final Runnable onTick;

    public Contador(
            KinPlugin plugin,
            int seconds,
            Runnable onTick,
            Runnable onFinish
    ) {
        this.plugin = plugin;
        this.secondsLeft = seconds;
        this.onTick = onTick;
        this.onFinish = onFinish;
    }

    public void start() {
        if (task != null) return; // ya está corriendo

        task = Bukkit.getScheduler().runTaskTimer(
                plugin,
                () -> {
                    if (secondsLeft <= 0) {
                        stop();
                        if (onFinish != null) onFinish.run();
                        return;
                    }

                    if (onTick != null) onTick.run();
                    secondsLeft--;
                },
                0L,
                20L // 20 ticks = 1 segundo
        );
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void reset(int newSeconds) {
        stop();
        this.secondsLeft = newSeconds;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public boolean isRunning() {
        return task != null;
    }
}
