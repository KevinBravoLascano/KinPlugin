package mp.example;
import mp.example.classes.EquipoControlador;
import mp.example.commands.MainCommand;
import mp.example.completters.GiveItemTab;
import mp.example.juegos.GameManager;
import mp.example.juegos.HabitacionesManager;
import mp.example.juegos.RedLightManager;
import mp.example.listeners.JuegoParejas;
import mp.example.listeners.PlayerListenerPisarBloque;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.UUID;

public class KinPlugin extends JavaPlugin {

    private String version = getDescription().getVersion();
    public HashMap<UUID, double[]> coords = new HashMap<>();
    private EquipoControlador equipoControlador;
    public GameManager gameManager = new GameManager();
    public RedLightManager redLightManager;
    public HabitacionesManager habitacionesManager;
    public JuegoParejas juegoParejas;
    public void onEnable() {
        juegoParejas = new JuegoParejas(this);
        registerCommands();
        registerEvents();
        Bukkit.getConsoleSender().sendMessage("[KinPlugin"+version+"] Plugin Enabled");
        equipoControlador = new EquipoControlador();
        this.redLightManager = new RedLightManager(this);
        this.habitacionesManager = new HabitacionesManager(this);

    }

    public EquipoControlador getEquipoControlador() {
        return equipoControlador;
    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[KinPlugin "+version+" ] Plugin Disabled");
    }

    public void registerCommands() {

        this.getCommand("miplugin").setExecutor(new MainCommand(this, gameManager));
        this.getCommand("miplugin").setTabCompleter(new GiveItemTab(this));
    }

    public void registerEvents() {

        getServer().getPluginManager().registerEvents(new PlayerListenerPisarBloque(this),this);
        getServer().getPluginManager().registerEvents(juegoParejas,this);
    }

}
