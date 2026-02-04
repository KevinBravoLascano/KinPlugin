package mp.example.juegos;

import mp.example.KinPlugin;
import mp.example.classes.Contador;
import mp.example.classes.GameType;
import mp.example.classes.Habitacion;
import mp.example.interficies.MiniGame;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manager para el minijuego de Habitaciones.
 * Controla las habitaciones, jugadores y objetivos.
 */
public class HabitacionesManager implements MiniGame {

    private final KinPlugin plugin;
    private final List<Habitacion> habitaciones = new ArrayList<>();
    private boolean running = false;

    public HabitacionesManager(KinPlugin plugin) {
        this.plugin = plugin;
    }

    /* =========================
       HABITACIONES
       ========================= */

    public void addHabitacion(Habitacion habitacion) {
        habitaciones.add(habitacion);
    }

    public void removeHabitacion(String id) {
        habitaciones.removeIf(h -> h.getId().equalsIgnoreCase(id));
    }

    public Habitacion getHabitacion(String id) {
        for (Habitacion h : habitaciones) {
            if (h.getId().equalsIgnoreCase(id)) return h;
        }
        return null;
    }

    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }
    public void setObjetivo(int objetivo) {
        for (Habitacion h : habitaciones) {
            h.setObjetivo(objetivo);
        }
    }

    /* =========================
       MOVIMIENTO DE JUGADORES
       ========================= */

    @Override
    public void onMove(Player player, PlayerMoveEvent e) {
        if (!running) return;

        for (Habitacion habitacion : habitaciones) {
            habitacion.actualizarJugador(player);
        }
    }

    /* =========================
       VERIFICAR OBJETIVOS
       ========================= */

    public void checkObjetivos() {

        for (Habitacion habitacion : habitaciones) {

            if (habitacion.cumpleObjetivo()) {
                Bukkit.broadcastMessage(
                        "§aLa habitación §e" + habitacion.getId() +
                                " §aha cumplido el objetivo!"
                );
                for(UUID jugador : habitacion.getJugadores()) {
                    Player player = Bukkit.getPlayer(jugador);
                    if(player != null) {
                        plugin.gameManager.markPlayerSafe(player,GameType.HABITACIONES);
                    }
                }

            }

        }
    }

    /* =========================
       MiniGame Implementation
       ========================= */

    @Override
    public void start(String[] args) {
        if (args.length < 2) return;

        int secondsLeft;
        try {
            secondsLeft = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Bukkit.getLogger().warning("Tiempo inválido para Habitaciones: " + args[1]);
            return;
        }

        running = true;

        // Asignar jugadores activos al juego de habitaciones
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getGameMode() != GameMode.SPECTATOR) {
                plugin.gameManager.setPlayerGame(p, GameType.HABITACIONES);
            }
        }

        // Limpiar safePlayers por si acaso
        plugin.gameManager.clearSafePlayers(GameType.HABITACIONES);

        // Crear contador
        Contador contador = new Contador(
                plugin,
                secondsLeft,
                this::onTick,
                this::stop
        );

        plugin.gameManager.startGame(GameType.HABITACIONES, contador);
    }

    @Override
    public void stop() {
        if (!running) return;

        running = false;

        checkObjetivos(); // al finalizar revisa quién cumple objetivo

        var vivos = plugin.gameManager.getPlayersAlive(GameType.HABITACIONES);

        for (Player p : vivos) {
            Bukkit.broadcastMessage("Jugador: " + p.getName()+" eliminado");
            p.setGameMode(GameMode.SPECTATOR);
        }
        plugin.gameManager.stopGame(GameType.HABITACIONES);

        // Limpiar HUD, contadores o cualquier cosa visual
    }

    @Override
    public void onTick() {
        if (!running) return;

        // Aquí podrías actualizar HUD o temporizador
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
