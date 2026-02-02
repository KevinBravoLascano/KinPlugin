package mp.example.completters;

import mp.example.KinPlugin;
import mp.example.classes.Equipo;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.*;

public class GiveItemTab implements TabCompleter {
    private KinPlugin plugin;
    public GiveItemTab(KinPlugin plugin) {
        this.plugin = plugin;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        // /giveitem <TAB>
        if (args.length == 2) {
            List<String> list = new ArrayList<>();
            for(Equipo teams : plugin.getEquipoControlador().getAllTeams()){
                list.add(teams.getNombre());
            }
            return list;
        }

        // /giveitem <jugador> <TAB>
        if (args.length == 3) {
            List<String> items = new ArrayList<>();
            String current = args[1].toLowerCase();

            for (Material m : Material.values()) {
                if (m.isItem() && m.name().toLowerCase().startsWith(current)) {
                    items.add(m.name().toLowerCase());
                }
            }
            return items;
        }

        // /giveitem <jugador> <item> <TAB>
        if (args.length == 4) {
            return Arrays.asList("1", "16", "32", "64");
        }

        return Collections.emptyList();
    }
}

