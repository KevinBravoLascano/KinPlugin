package mp.example.juegos;

import mp.example.KinPlugin;
import mp.example.classes.Contador;
import mp.example.classes.GameType;
import mp.example.interficies.MiniGame;
import mp.example.visuales.GameHUD;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Minijuego RedLight / GreenLight.
 * Controla jugadores, luces, contador y HUD.
 */
public class RedLightManager implements MiniGame {

    private final KinPlugin plugin;
    private boolean running = false;

    public RedLightManager(KinPlugin plugin) {
        this.plugin = plugin;
    }

    /* =======================
       MiniGame Métodos
       ======================= */

    @Override
    public void onTick() {
        if (!running) return;

        Contador contador = plugin.gameManager.getContador(GameType.RED_LIGHT);
        if (contador == null) return;

        int tiempoRestante = contador.getSecondsLeft();
        var vivos = plugin.gameManager.getPlayersAlive(GameType.RED_LIGHT);
        boolean luzRoja = plugin.gameManager.isRedLight();
        GameHUD.updateTimer(tiempoRestante, luzRoja, vivos);
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void start(String[] args) {
        if (args.length < 2) return; // seguridad
        int secondsLeft;
        try {
            secondsLeft = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Bukkit.getLogger().warning("Tiempo inválido para RedLight: " + args[1]);
            return;
        }

        running = true;
        plugin.gameManager.clearSafePlayers(GameType.RED_LIGHT);

        // 🔹 ASIGNAR TODOS LOS JUGADORES QUE NO ESTÉN EN SPECTATOR AL JUEGO
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() != GameMode.SPECTATOR) {
                plugin.gameManager.setPlayerGame(p, GameType.RED_LIGHT);
            }
        }

        Contador contador = new Contador(
                plugin,
                secondsLeft,
                this::onTick,
                this::finishGame
        );

        plugin.gameManager.startGame(GameType.RED_LIGHT, contador);
    }

    @Override
    public void stop() {
        if (!running) return;
        running = false;

        plugin.gameManager.stopGame(GameType.RED_LIGHT);
        GameHUD.clearTimer();
    }

    @Override
    public void onMove(Player player, PlayerMoveEvent e) {
        if (!running) return;

        Location loc = e.getTo();
        if (loc == null) return;

        // Zona segura
        if (loc.getBlock().getRelative(0, -1, 0).getType() == Material.RED_WOOL
                && !plugin.gameManager.isPlayerSafe(player, GameType.RED_LIGHT)) {
            plugin.gameManager.markPlayerSafe(player, GameType.RED_LIGHT);
            player.sendMessage("§a¡Zona segura alcanzada!");
            return;
        }

        // Luz roja
        if (plugin.gameManager.isRedLight() && !plugin.gameManager.isPlayerSafe(player, GameType.RED_LIGHT)) {
            eliminarJugador(player, loc);
        }
    }
    public void setRed(boolean red) {
        plugin.gameManager.setRed(red); // actualiza la luz en GameManager
        if (red) {
            // Guardar posición de todos los jugadores vivos
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.getGameMode() != GameMode.SPECTATOR &&
                        plugin.gameManager.isInGame(p, GameType.RED_LIGHT)) {

                    double x = p.getLocation().getBlockX();
                    double z = p.getLocation().getBlockZ();
                    plugin.coords.put(p.getUniqueId(), new double[]{x, z});
                    p.sendTitle("§c◉", "Luz ROJA, no avances!", 10, 60, 10);
                }
            }
        } else {
            plugin.coords.clear();
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle("§a◉", "Luz VERDE, pueden avanzar!", 10, 60, 10);
            }
        }
    }



    /* =======================
       Métodos Auxiliares
       ======================= */

    private void eliminarJugador(Player player, Location loc) {
        double[] start = plugin.coords.getOrDefault(
                player.getUniqueId(),
                new double[]{loc.getBlockX(), loc.getBlockZ()}
        );

        int x0 = (int) start[0];
        int z0 = (int) start[1];

        if (loc.getBlockX() != x0 || loc.getBlockZ() != z0) {
            player.sendMessage("§cTe moviste → eliminado");
            player.setGameMode(GameMode.SPECTATOR);
            plugin.coords.remove(player.getUniqueId());
            Bukkit.broadcastMessage(
                    "§cJugador " + player.getName() + " ¡se movió y ha sido eliminado!"
            );
        }
    }

    private void finishGame() {
        running = false;

        Bukkit.broadcastMessage("⛔ Tiempo terminado");
        var vivos = plugin.gameManager.getPlayersAlive(GameType.RED_LIGHT);

        for (Player p : vivos) {
            p.setGameMode(GameMode.SPECTATOR);
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (plugin.gameManager.isPlayerSafe(p, GameType.RED_LIGHT)) {
                p.sendMessage("✅ Sobreviviste");
            }
        }

        plugin.gameManager.stopGame(GameType.RED_LIGHT);
        GameHUD.clearTimer();
    }
}
