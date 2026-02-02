package mp.example.commands;

import mp.example.KinPlugin;
import mp.example.classes.Contador;
import mp.example.classes.Equipo;
import mp.example.classes.Habitacion;
import mp.example.juegos.GameManager;
import mp.example.utils.MessageUtils;
import mp.example.visuales.GameHUD;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;



import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor {

    private KinPlugin plugin;
    private GameManager gameManager;
    public MainCommand(KinPlugin plugin, GameManager gameManager) {
        this.plugin = plugin;
        this.gameManager =gameManager;
    }

    @Override
    public boolean onCommand(CommandSender Sender, Command command, String alias, String[] args) {

        if(!(Sender instanceof Player)){
            //lo hace el comando la consola
            Sender.sendMessage(MessageUtils.getColoredMessage("&cOnly players can run this command!"));
            return true;
        }

        //jugador hace el comando
        Player player = (Player) Sender;
        // Comprobar si es OP (administrador del servidor)
        if (!player.isOp()) {
            player.sendMessage("§cNo tienes permisos para usar este comando.");
            return true;
        }
        if(args.length>=1){
            //generar hasbitaciones
            if(args[0].equalsIgnoreCase("generarHabitacion")){
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                int z = Integer.parseInt(args[3]);

                Habitacion habitacion = new Habitacion((Player) Sender,x,y,z);
                habitacion.generar();
                return true;
            }
            //sacar help de comandos
            if(args[0].equalsIgnoreCase("help")){
                help(args,Sender);
                return true;
            }
            //borrar equipos creados
            if(args[0].equalsIgnoreCase("clear")){
                plugin.getEquipoControlador().borrarEquipos();
            }
            //dar objeto a equipo concreto
            if(args[0].equalsIgnoreCase("give")){
                String equipo = args[1];
                String Item =  args[2];
                String cantidad=args[3];
                plugin.getEquipoControlador().giveEquipo(equipo,Item,cantidad);
            }


            //luz gverde luz rojoa squids
            if(args[0].equalsIgnoreCase("verde")) {
                gameManager.setRed(false); // luz verde
                plugin.coords.clear(); // limpiar coordenadas iniciales
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendTitle("§a◉", "Luz VERDE, pueden avanzar!", 10, 60, 10);
                }
            }

            if(args[0].equalsIgnoreCase("rojo")) {
                gameManager.setRed(true); // luz roja
                for (Player p : Bukkit.getOnlinePlayers()) {
                    double x = p.getLocation().getBlockX();
                    double z = p.getLocation().getBlockZ();
                    plugin.coords.put(p.getUniqueId(), new double[]{x, z}); // guardar posición
                    p.sendTitle("§c◉", "Luz ROJA, no avances!", 10, 60, 10);
                }
            }

            //comadno para equipos
            if(args[0].equalsIgnoreCase("equipo")) {
                if (args[1].equalsIgnoreCase("crear")) {
                    crearEquipo(args, Sender);
                    return true;
                }
                if (args[1].equalsIgnoreCase("entrar")) {
                    String name = args[2];
                    String jugador = args[3];
                    Player p = Bukkit.getPlayer(jugador);
                    boolean ok = plugin.getEquipoControlador().addPlayerToTeam(p, name);
                    player.sendMessage(ok ?
                            "§aSe unio al equipo " + name :
                            "§cNo se pudo unir (¿lleno o no existe?)");
                    return true;
                }
                if (args[1].equalsIgnoreCase("salir")) {
                    String jugador = args[2];
                    Player p = Bukkit.getPlayer(jugador);
                    boolean ok = plugin.getEquipoControlador().removePlayerFromTeam(p);
                    player.sendMessage(ok ?
                            "§aSaliste de tu equipo." :
                            "§cNo estás en ningún equipo.");
                    return true;
                }
                if (args[1].equalsIgnoreCase("info")) {
                    for (Equipo team : plugin.getEquipoControlador().getAllTeams()) {
                        team.broadcast("Clicks del equipo\n");
                        player.sendMessage("Clicks del equipo :" + team.getNombre() + "\n");
                        for (UUID uuid : team.getPlayers().keySet()) {
                            Player p = Bukkit.getPlayer(uuid);
                            assert p != null;
                            int clicks = team.getClicks(p);
                            team.broadcast(p.getName() + " :" + clicks + " realizados");
                            player.sendMessage(p.getName() + " :" + clicks + " realizados");
                        }
                    }
                }
                if (args[1].equalsIgnoreCase("random")) {

                    int tamaño;

                    try {
                        tamaño = Integer.parseInt(args[2]);
                    } catch (NumberFormatException e) {
                        Sender.sendMessage("§cEl tamaño del equipo debe ser un número.");
                        return true;
                    }

                    if (tamaño <= 0) {
                        Sender.sendMessage("§cEl tamaño del equipo debe ser mayor que 0.");
                        return true;
                    }

                    List<Player> jugadores = Bukkit.getOnlinePlayers().stream()
                            .filter(p -> p.getGameMode() != GameMode.SPECTATOR)
                            .collect(Collectors.toList());

                    if (jugadores.isEmpty()) {
                        Sender.sendMessage("§cNo hay jugadores disponibles.");
                        return true;
                    }

                    plugin.getEquipoControlador().crearEquipoRandom(jugadores, tamaño);
                    Sender.sendMessage("§aEquipos generados correctamente.");
                    for (Equipo e : plugin.getEquipoControlador().getAllTeams()) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("§a").append(e.getNombre()).append(" §7miembros: ");

                        for (UUID id : e.getPlayers().keySet()) {
                            Player p = Bukkit.getPlayer(id);
                            if (p != null) {
                                sb.append(p.getName()).append(", ");
                            }
                        }

                        // Quitar la última coma
                        if (sb.length() > 2) {
                            sb.setLength(sb.length() - 2);
                        }

                        for (Player p : plugin.getServer().getOnlinePlayers()) {
                            p.sendMessage(sb.toString());
                        }
                    }
                }
            }




            if(args[0].equalsIgnoreCase("iniciarParejas")){
                for(Equipo team : plugin.getEquipoControlador().getAllTeams()) {
                    String[] mensajes = {"El juego empieza en..\n", "3\n", "2\n", "1\n", "YA\n"};

                    new BukkitRunnable() {
                        int index = 0;

                        @Override
                        public void run() {
                            if (index >= mensajes.length) {
                                cancel();
                                return;
                            }

                            team.broadcast(mensajes[index]);
                            index++;
                        }
                    }.runTaskTimer(plugin, 0L, 40L); // 2 segundos entre cada mensaje
                    for (UUID uuid : team.getPlayers().keySet()) {
                        Player p = Bukkit.getPlayer(uuid);
                        assert p != null;
                        team.resetClicks(p);
                    }
                }
            }


            if(args[0].equalsIgnoreCase("detenerParejas")) {

                for(Equipo team : plugin.getEquipoControlador().getAllTeams()) {

                    int minClicks = Integer.MAX_VALUE;
                    Player menor = null;

                    team.broadcast("§b--- Clicks del equipo " + team.getNombre() + " ---");

                    for(UUID uuid : team.getPlayers().keySet()) {

                        Player p = Bukkit.getPlayer(uuid);
                        if(p == null) continue; // jugador offline

                        int clicks = team.getClicks(p);

                        if(clicks < minClicks) {
                            minClicks = clicks;
                            menor = p;
                        }

                        team.broadcast(p.getName() + " : §a" + clicks + " clicks");

                    }

                    // Eliminar jugador con menos clicks
                    if(menor != null) {
                        team.broadcast("§cJugador eliminado: " + menor.getName());
                        menor.setGameMode(GameMode.SPECTATOR);
                    }
                }
            }
            if(args[0].equalsIgnoreCase("luces")) {
                luces(args);
            }


        }
        return  true;
    }

    //comando help para comandos
    public void help(String[] args,CommandSender Sender) {
        Sender.sendMessage(MessageUtils.getColoredMessage("&c/kin verde (poner luz verde) \n"));
        Sender.sendMessage(MessageUtils.getColoredMessage("&c/kin rojo (poner luz roja) \n"));
        Sender.sendMessage(MessageUtils.getColoredMessage("&c/kin equipo crear <nombre> <tamaño> (crear equipos de x nombre y x capacidad) \n"));
        Sender.sendMessage(MessageUtils.getColoredMessage("&c/kin equipo entrar <nombre> <jugador> (entrar en x equipo) \n"));
        Sender.sendMessage(MessageUtils.getColoredMessage("&c/kin equipo random  <tamañoEquipos> \n"));
        Sender.sendMessage(MessageUtils.getColoredMessage("&c/kin luces  <tiempo contador en s> \n"));
    }
    //comando para juego con timer de luces rojo verde
    public void luces(String []args){
        int secondsLeft = Integer.parseInt(args[1]);
        plugin.gameManager.clearSafePlayers();
        final Contador[] contadorHolder = new Contador[1];

        Contador contador = new Contador(
                plugin,
                secondsLeft,
                () -> {
                    int tiempoRestante = contadorHolder[0].getSecondsLeft();
                    var vivos = plugin.gameManager.getPlayersAlive();
                    boolean luzRoja = plugin.gameManager.isRedLight();
                    GameHUD.updateTimer(tiempoRestante, luzRoja, vivos);
                },
                () -> {
                    Bukkit.broadcastMessage("⛔ Tiempo terminado");
                    var vivos = plugin.gameManager.getPlayersAlive();
                    for (Player p : vivos) {
                        p.setGameMode(GameMode.SPECTATOR);
                    }

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (plugin.gameManager.isPlayerSafe(p)) {
                            p.sendMessage("✅ Sobreviviste");
                        }
                    }

                    GameHUD.clearTimer();
                    gameManager.stopGame();
                }
        );

        contadorHolder[0] = contador; // asignar al holder
        gameManager.startGame(contador); // startGame inicia el contador
    }
    //crear euipoo
    public void crearEquipo(String[] args, CommandSender Sender) {
        String nombre = args[2];
        int maxSize= 0;
        try {
            maxSize = Integer.parseInt(args[3]);
        } catch (Exception e) {
            Sender.sendMessage("§cEl tamaño debe ser un número.");

        }
        boolean ok = plugin.getEquipoControlador().crearEquipo(nombre, maxSize);
        Sender.sendMessage(ok ?
                "§aEquipo creado: " + nombre :
                "§cEse equipo ya existe");

    }
}





