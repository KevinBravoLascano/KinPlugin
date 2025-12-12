package mp.example.classes;

import mp.example.visuales.HUDSidebar;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Equipo {
    private String nombre;
    private int tamaño;
    private Map<UUID, Integer> players = new HashMap<>();

    public Equipo(String nombre,int max){
        this.nombre = nombre;
        tamaño=max;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public int getTamanio() {
        return tamaño;
    }
    public void setTamanio(int tamanio) {
        tamanio = tamanio;
    }
    public Map<UUID,Integer> getPlayers() {
        return players;
    }
    public void setPlayers(Map<UUID,Integer> players) {
        this.players = players;
    }
    public boolean addPlayer(Player player) {
        UUID id = player.getUniqueId();

        // Verificar si ya está en el equipo
        if (players.containsKey(id)) return false;

        // Verificar límite de jugadores (si tienes maxSize)
        if (players.size() >= tamaño) return false;

        // Añadir jugador con contador inicial 0
        players.put(id, 0);
        return true;
    }
    public boolean removePlayer(Player player){
        players.remove(player.getUniqueId());
        return true;
    }
    public int getClicks(Player player) {
        return players.getOrDefault(player.getUniqueId(), 0);
    }

    public void addClick(Player player) {
        UUID id = player.getUniqueId();
        players.put(id, players.getOrDefault(id, 0) + 1);

        // Actualizar el HUD a todos los jugadores del equipo
        for (UUID pid : players.keySet()) {
            Player p = Bukkit.getPlayer(pid);
            if (p != null) {
                HUDSidebar.update(this, p);
            }
        }

    }

    public void resetClicks(Player player) {
        players.put(player.getUniqueId(), 0);
    }

    public Map<UUID, Integer> getAllClicks() {
        return players;
    }
    public void broadcast(String msg) {
        for (UUID id : players.keySet()) {
            Player p = Bukkit.getPlayer(id);
            if (p != null) {
                p.sendMessage(msg);
            }
        }
    }


}
