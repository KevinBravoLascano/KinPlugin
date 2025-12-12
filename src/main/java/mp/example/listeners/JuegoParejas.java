package mp.example.listeners;

import mp.example.KinPlugin;
import mp.example.classes.Equipo;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class JuegoParejas implements Listener {
    private KinPlugin plugin;

    public JuegoParejas(KinPlugin plugin) {
        this.plugin = plugin;

    }
    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {

        // Solo contar CLIC IZQUIERDO en un bloque
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        Player player = event.getPlayer();

        // Obtener el equipo del jugador
        Equipo team = plugin.getEquipoControlador().equipoJugador(player);
        if (team == null) return;

        // Sumar un click a su contador
        team.addClick(player);
    }


}
