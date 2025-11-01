package mp.example;

import mp.example.commands.MainCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class KinPlugin extends JavaPlugin {
    private String version = getDescription().getVersion();
    public void onEnable() {
        registerCommands();
        Bukkit.getConsoleSender().sendMessage("[KinPlugin"+version+"] Plugin Enabled");
    }

    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("[KinPlugin"+version+"] Plugin Disabled");
    }

    public void registerCommands() {
        this.getCommand("miplugin").setExecutor(new MainCommand());
    }
}
