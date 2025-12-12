package mp.example.commands;

import mp.example.KinPlugin;
import mp.example.classes.Equipo;
import mp.example.utils.MessageUtils;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.HashMap;
import java.util.UUID;

public class MainCommand implements CommandExecutor {

    private KinPlugin plugin;
    public MainCommand(KinPlugin plugin) {
        this.plugin = plugin;
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

        if(args.length>=1){

            if(args[0].equalsIgnoreCase("help")){
                Sender.sendMessage(MessageUtils.getColoredMessage("&c/kin verde (poner luz verde) \n"));
                Sender.sendMessage(MessageUtils.getColoredMessage("&c/kin rojo (poner luz roja) \n"));
                Sender.sendMessage(MessageUtils.getColoredMessage("&c/kin equipo crear <nombre> <tamaño> (crear equipos de x nombre y x capacidad) \n"));
                Sender.sendMessage(MessageUtils.getColoredMessage("&c/kin equipo entrar <nombre> <jugador> (entrar en x equipo) \n"));

            }

            //luz gverde luz rojoa squids
            if(args[0].equalsIgnoreCase("verde")){
                plugin.coords.clear();

                // Título verde para todos los jugadores
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    p.sendTitle("§a◉", "Luz VERDE, pueden avanzar!", 10, 60, 10);
                }


            }

            if(args[0].equalsIgnoreCase("rojo")){
                String simbolo = "§c◉";


                player.sendTitle(simbolo, "Luz ROJA no avances o seras eliminado", 10, 60, 10);

                // Guardar X y Z del jugador en este momento

                for(Player p : Bukkit.getOnlinePlayers()) {
                    double x = p.getLocation().getBlockX();
                    double z = p.getLocation().getBlockZ();
                    plugin.coords.put(p.getUniqueId(), new double[]{x, z});
                }



            }

            //comadno para equipos
            if(args[0].equalsIgnoreCase("equipo")){
                if(args[1].equalsIgnoreCase("crear")){
                    String nombre = args[2];
                    int maxSize;
                    try {
                        maxSize = Integer.parseInt(args[3]);
                    } catch (Exception e) {
                        player.sendMessage("§cEl tamaño debe ser un número.");
                        return true;
                    }
                    boolean ok = plugin.getEquipoControlador().crearEquipo(nombre, maxSize);
                    player.sendMessage(ok ?
                            "§aEquipo creado: " + nombre :
                            "§cEse equipo ya existe");
                    return true;
                }
                if(args[1].equalsIgnoreCase("entrar")){
                    String name = args[2];
                    String jugador = args[3];
                    Player p = Bukkit.getPlayer(jugador);
                    boolean ok = plugin.getEquipoControlador().addPlayerToTeam(p, name);
                    player.sendMessage(ok ?
                            "§aSe unio al equipo " + name :
                            "§cNo se pudo unir (¿lleno o no existe?)");
                    return true;
                }
                if(args[1].equalsIgnoreCase("salir")){
                    String jugador = args[2];
                    Player p = Bukkit.getPlayer(jugador);
                    boolean ok = plugin.getEquipoControlador().removePlayerFromTeam(p);
                    player.sendMessage(ok ?
                            "§aSaliste de tu equipo." :
                            "§cNo estás en ningún equipo.");
                    return true;
                }
                if(args[1].equalsIgnoreCase("info")){
                    for(Equipo team : plugin.getEquipoControlador().getAllTeams()){
                        team.broadcast("Clicks del equipo\n");
                        player.sendMessage("Clicks del equipo :"+ team.getNombre()+"\n");
                        for(UUID uuid : team.getPlayers().keySet()){
                            Player p = Bukkit.getPlayer(uuid);
                            assert p != null;
                            int clicks= team.getClicks(p);
                            team.broadcast(p.getName()+" :"+clicks+" realizados");
                            player.sendMessage(p.getName()+" :"+clicks+" realizados");
                        }
                    }
                }

            }

            if(args[0].equalsIgnoreCase("iniciarParejas")){
                for(Equipo team : plugin.getEquipoControlador().getAllTeams()){
                    String[] mensajes = {"El juego empieza en..\n","3\n", "2\n", "1\n"};

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
                    for(UUID uuid : team.getPlayers().keySet()){
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
                    player.sendMessage("§b--- Clicks del equipo " + team.getNombre() + " ---");

                    for(UUID uuid : team.getPlayers().keySet()) {

                        Player p = Bukkit.getPlayer(uuid);
                        if(p == null) continue; // jugador offline

                        int clicks = team.getClicks(p);

                        if(clicks < minClicks) {
                            minClicks = clicks;
                            menor = p;
                        }

                        team.broadcast(p.getName() + " : §a" + clicks + " clicks");
                        player.sendMessage(p.getName() + " : §a" + clicks + " clicks");
                    }

                    // Eliminar jugador con menos clicks
                    if(menor != null) {
                        team.broadcast("§cJugador eliminado: " + menor.getName());
                        menor.setGameMode(GameMode.SPECTATOR);
                    }
                }
            }


        }
        return true;
    }
}
