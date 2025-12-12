package mp.example.classes;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
}
