package mp.example.commands;

import mp.example.KinPlugin;
import mp.example.classes.Equipo;
import mp.example.classes.GameType;
import mp.example.classes.Habitacion;
import mp.example.interficies.MiniGame;
import mp.example.juegos.GameManager;
import mp.example.listeners.JuegoParejas;
import mp.example.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor {

    private final KinPlugin plugin;
    private final GameManager gameManager;
    private final JuegoParejas juegoParejas;

    public MainCommand(KinPlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.juegoParejas = plugin.juegoParejas;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MessageUtils.getColoredMessage("&cOnly players can run this command!"));
            return true;
        }

        if (!player.isOp()) {
            player.sendMessage("§cNo tienes permisos para usar este comando.");
            return true;
        }

        if (args.length < 1) {
            help(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            //parejas clicks
            case "parejas" -> {

                if (args.length < 2) {
                    sender.sendMessage("§cUso: /parejas <tiempo>");
                    return true;
                }

                int tiempo;

                try {
                    tiempo = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cEl tiempo debe ser un número");
                    return true;
                }

                if (tiempo <= 0) {
                    sender.sendMessage("§cEl tiempo debe ser mayor que 0");
                    return true;
                }

                if (juegoParejas.isActivo()) {
                    sender.sendMessage("§cEl juego ya está activo");
                    return true;
                }

                juegoParejas.iniciarJuego(tiempo);
                sender.sendMessage("§aJuego de parejas iniciado por " + tiempo + " segundos");
            }

            // =========================
            // HABITACIONES
            // =========================
            case "generarhabitacion" -> {
                if (args.length < 5) {
                    player.sendMessage("Uso: /kin generarHabitacion <x> <y> <z> <id>");
                    return true;
                }
                try {
                    int x = Integer.parseInt(args[1]);
                    int y = Integer.parseInt(args[2]);
                    int z = Integer.parseInt(args[3]);
                    String id = args[4];

                    Habitacion habitacion = new Habitacion(player, x, y, z, 1, id);
                    habitacion.generar();
                    plugin.habitacionesManager.addHabitacion(habitacion);

                    player.sendMessage("§aHabitación creada con ID: " + id);

                } catch (NumberFormatException e) {
                    player.sendMessage("§cLas dimensiones deben ser números");
                }

            }
            case "habitacion" -> {
                plugin.habitacionesManager.start(args);
            }
            case "setobjetivo" -> {
                int jugadores = Bukkit.getOnlinePlayers().stream().filter(pl -> pl.getGameMode() == GameMode.SURVIVAL).collect(Collectors.toList()).size();
                int objetivo = ThreadLocalRandom.current().nextInt(0, jugadores+1);
                Bukkit.broadcastMessage("Numero por habitacion: "+objetivo);
                plugin.habitacionesManager.setObjetivo(objetivo);
            }

            // =========================
            // RED LIGHT
            // =========================
            case "verde" -> plugin.redLightManager.setRed(false);
            case "rojo" -> plugin.redLightManager.setRed(true);

            case "luces" -> {
                if (args.length < 2) {
                    player.sendMessage("Uso: /kin luces <tiempo en segundos>");
                    return true;
                }
                plugin.redLightManager.start(args);
            }

            // =========================
            // EQUIPOS
            // =========================
            case "equipo" -> handleEquipoCommand(args, sender);

            // =========================
            // AYUDA
            // =========================
            case "help" -> help(sender);

            default -> player.sendMessage("§cComando desconocido. Usa /kin help");
        }

        return true;
    }

    /* =========================
       EQUIPOS
       ========================= */
    private void handleEquipoCommand(String[] args, CommandSender sender) {

        if (args.length < 2) {
            sender.sendMessage("§cSubcomando requerido: crear/entrar/salir/info/random");
            return;
        }

        switch (args[1].toLowerCase()) {

            case "crear" -> crearEquipo(args, sender);

            case "entrar" -> {
                if (args.length < 4) return;
                String teamName = args[2];
                Player p = Bukkit.getPlayer(args[3]);
                if (p == null) return;
                boolean ok = plugin.getEquipoControlador().addPlayerToTeam(p, teamName);
                sender.sendMessage(ok ? "§aSe unió al equipo " + teamName : "§cNo se pudo unir (¿lleno o no existe?)");
            }

            case "salir" -> {
                if (args.length < 3) return;
                Player p = Bukkit.getPlayer(args[2]);
                if (p == null) return;
                boolean ok = plugin.getEquipoControlador().removePlayerFromTeam(p);
                sender.sendMessage(ok ? "§aSaliste de tu equipo." : "§cNo estás en ningún equipo.");
            }

            case "info" -> {
                for (Equipo team : plugin.getEquipoControlador().getAllTeams()) {
                    sender.sendMessage("§b--- Equipo: " + team.getNombre() + " ---");
                    for (UUID uuid : team.getPlayers().keySet()) {
                        Player p = Bukkit.getPlayer(uuid);
                        if (p != null) sender.sendMessage(p.getName() + " : " + team.getClicks(p) + " clicks");
                    }
                }
            }

            case "random" -> {
                if (args.length < 3) return;
                int size;
                try {
                    size = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cEl tamaño debe ser un número.");
                    return;
                }
                if (size <= 0) {
                    sender.sendMessage("§cDebe ser mayor que 0.");
                    return;
                }

                List<Player> jugadores = Bukkit.getOnlinePlayers().stream()
                        .filter(p -> p.getGameMode() != GameMode.SPECTATOR)
                        .collect(Collectors.toList());

                if (jugadores.isEmpty()) {
                    sender.sendMessage("§cNo hay jugadores disponibles.");
                    return;
                }

                plugin.getEquipoControlador().crearEquipoRandom(jugadores, size);
                sender.sendMessage("§aEquipos generados correctamente.");
            }

            default -> sender.sendMessage("§cSubcomando desconocido");
        }
    }

    /* =========================
       CREAR EQUIPO
       ========================= */
    private void crearEquipo(String[] args, CommandSender sender) {
        if (args.length < 4) return;
        String nombre = args[2];
        int maxSize;
        try {
            maxSize = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cEl tamaño debe ser un número.");
            return;
        }

        boolean ok = plugin.getEquipoControlador().crearEquipo(nombre, maxSize);
        sender.sendMessage(ok ? "§aEquipo creado: " + nombre : "§cEse equipo ya existe");
    }

    /* =========================
       HELP
       ========================= */
    private void help(CommandSender sender) {
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin verde - luz verde"));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin rojo - luz roja"));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin luces <segundos> - iniciar Red Light"));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin generarHabitacion <x> <y> <z> <id> - crear habitación"));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin equipo crear <nombre> <tamaño>"));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin equipo entrar <nombre> <jugador>"));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin equipo salir <jugador>"));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin equipo random <tamaño>"));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin help - mostrar comandos"));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin habitacion <segundos> "));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin setobjetivo <numeroPersonas> - objetivo Habitaciones "));
        sender.sendMessage(MessageUtils.getColoredMessage("&c/kin parejas <tiempo> "));
    }
}
