package mp.example.listeners;

import mp.example.KinPlugin;
import mp.example.classes.Equipo;
import mp.example.visuales.HUDSidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.UUID;

public class JuegoParejas implements Listener {

    private final KinPlugin plugin;

    private boolean activo = false;
    private int tiempoRestante;
    private BukkitRunnable task;
    private BossBar bossBar;

    public JuegoParejas(KinPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isActivo() {
        return activo;
    }

    /* =========================
       INICIAR JUEGO
       ========================= */
    public void iniciarJuego(int segundos) {
        if (activo) return;

        activo = true;
        tiempoRestante = segundos;

        // Crear BossBar
        bossBar = Bukkit.createBossBar("§eTiempo restante: §c" + tiempoRestante + "s", BarColor.GREEN, BarStyle.SOLID);

        // Resetear clicks y añadir jugadores al bossBar
        for (Equipo equipo : plugin.getEquipoControlador().getAllTeams()) {
             // método que resetea todos los clicks del equipo
            for (UUID playerId : equipo.getPlayers().keySet()) {
                Player p = Bukkit.getPlayer(playerId);
                if (p != null){
                    equipo.resetClicks(p);
                    bossBar.addPlayer(p);}
            }
        }

        Bukkit.broadcastMessage("§a¡Juego de Parejas iniciado!");
        iniciarTimer();
    }

    /* =========================
       TIMER PRINCIPAL
       ========================= */
    private void iniciarTimer() {
        task = new BukkitRunnable() {
            @Override
            public void run() {

                if (tiempoRestante <= 0) {
                    finalizarJuego();
                    cancel();
                    return;
                }

                tiempoRestante--;

                // Actualizar HUD y ActionBar
                for (Equipo equipo : plugin.getEquipoControlador().getAllTeams()) {
                    for (UUID id : equipo.getPlayers().keySet()) {
                        Player p = Bukkit.getPlayer(id);
                        if (p != null) {
                            HUDSidebar.update(equipo, p);

                            // ActionBar
                            p.spigot().sendMessage(
                                    ChatMessageType.ACTION_BAR,
                                    new TextComponent("§eTiempo restante: §c" + tiempoRestante + "s")
                            );
                        }
                    }
                }

                // Actualizar BossBar
                if (bossBar != null) {
                    double progreso = Math.max(0, (double) tiempoRestante / (tiempoRestante + 1));
                    bossBar.setProgress(progreso);
                    bossBar.setTitle("§eTiempo restante: §c" + tiempoRestante + "s");
                }
            }
        };

        task.runTaskTimer(plugin, 0, 20);
    }

    /* =========================
       FINALIZAR JUEGO
       ========================= */
    public void finalizarJuego() {
        activo = false;

        if (task != null) {
            task.cancel();
            task = null;
        }

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        Bukkit.broadcastMessage("§c¡Tiempo terminado!");

        // Eliminar jugador con menos clicks de cada equipo
        for (Equipo equipo : plugin.getEquipoControlador().getAllTeams()) {
            int minimo = Integer.MAX_VALUE;
            Player perdedor = null;

            for (UUID id : equipo.getPlayers().keySet()) {
                Player p = Bukkit.getPlayer(id);
                if (p == null) continue;

                int clicks = equipo.getClicks(p);
                if (clicks < minimo) {
                    minimo = clicks;
                    perdedor = p;
                }
            }

            if (perdedor != null) {
                Bukkit.broadcastMessage("Jugador: " + perdedor.getName() + " eliminado con "+equipo.getClicks(perdedor)+" clicks!" );
                perdedor.setGameMode(GameMode.SPECTATOR);
            }
        }
    }

    /* =========================
       LISTENER DE CLICKS
       ========================= */
    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {

        if (!activo) return;
        if (event.getAction() != Action.LEFT_CLICK_BLOCK){

            return;
        }

        Player player = event.getPlayer();


        Equipo team = plugin.getEquipoControlador().equipoJugador(player);
        if (team == null) return;

        team.addClick(player);

        // Actualizar HUD al instante
        for (UUID id : team.getPlayers().keySet()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) HUDSidebar.update(team, p);
        }
    }
}

