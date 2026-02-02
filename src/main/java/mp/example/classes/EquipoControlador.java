package mp.example.classes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class EquipoControlador {
    private Map<String,Equipo> teams = new HashMap<>();
    public boolean crearEquipo(String equipo,int max){
        Equipo e = new Equipo(equipo,max);
        teams.put(equipo,e);
        return true;
    }

    public Equipo getEquipo(String equipo){
        return teams.get(equipo);
    }

    public void giveEquipo(String equipo,String item, String amount){
        Equipo e = teams.get(equipo);
        for (UUID uuid : e.getPlayers().keySet()) {
            Player p = Bukkit.getPlayer(uuid);
            String cmd = "give "+p.getName()+" "+item+" "+amount;
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(),cmd);
        }

    }

    public Equipo equipoJugador(Player player){
        for(Equipo e : teams.values()){
            if(e.getPlayers().containsKey(player.getUniqueId())){
                return e;
            }
        }
        return null;
    }

    public boolean addPlayerToTeam(Player player, String teamName) {
        Equipo team = getEquipo(teamName);
        if (team == null) return false;
        return team.addPlayer(player);
    }

    public boolean removePlayerFromTeam(Player player) {
        Equipo current = equipoJugador(player);
        if (current == null) return false;
        return current.removePlayer(player);
    }

    public Collection<Equipo> getAllTeams() {
        return teams.values();
    }

    public boolean crearEquipoRandom(List<Player> players, int tamaño) {
        teams.clear();

        int numEquipos = (int) Math.ceil((double) players.size() / tamaño);

        // Crear los equipos
        for (int i = 0; i < numEquipos; i++) {
            Equipo e = new Equipo("Equipo " + (i + 1), tamaño);
            teams.put("Equipo " + (i + 1), e);
        }

        // Convertir a lista para acceder por índice
        List<Equipo> listaEquipos = new ArrayList<>(teams.values());

        // Mezclar jugadores y asignar
        Collections.shuffle(players);
        int index = 0;
        for (Player p : players) {
            listaEquipos.get(index).addPlayer(p);
            index = (index + 1) % listaEquipos.size();
        }

        return true;
    }
    public boolean borrarEquipos(){
        teams.clear();
        return true;
    }

}
