package mp.example;
import mp.example.classes.EquipoControlador;
import mp.example.commands.MainCommand;
import mp.example.listeners.JuegoParejas;
import mp.example.listeners.PlayerListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.UUID;

public class KinPlugin extends JavaPlugin {

    private String version = getDescription().getVersion();
    public HashMap<UUID, double[]> coords = new HashMap<>();
    private EquipoControlador equipoControlador;
    public void onEnable() {

        registerCommands();
        registerEvents();
        Bukkit.getConsoleSender().sendMessage("[KinPlugin"+version+"] Plugin Enabled");
        equipoControlador = new EquipoControlador();

    }

    public EquipoControlador getEquipoControlador() {
        return equipoControlador;
    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[KinPlugin"+version+"] Plugin Disabled");
    }

    public void registerCommands() {
        this.getCommand("miplugin").setExecutor(new MainCommand(this));
    }

    public void registerEvents() {

        getServer().getPluginManager().registerEvents(new PlayerListener(this),this);
        getServer().getPluginManager().registerEvents(new JuegoParejas(this),this);
    }

}
