package mp.example.classes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Habitacion {

    private final World world;
    private final Location base;
    private final int ancho;
    private final int alto;
    private final int largo;

    public Habitacion(Player player, int ancho, int alto, int largo) {
        this.world = player.getWorld();
        this.base = player.getLocation();
        this.ancho = ancho;
        this.alto = alto;
        this.largo = largo;
    }

    public void generar() {
        int x1 = base.getBlockX();
        int y1 = base.getBlockY();
        int z1 = base.getBlockZ();

        int x2 = x1 + ancho - 1;
        int y2 = y1 + alto - 1;
        int z2 = z1 + largo - 1;

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {

                    boolean esBorde =
                            x == x1 || x == x2 ||
                                    y == y1 || y == y2 ||
                                    z == z1 || z == z2;

                    Material material = esBorde ? Material.GLASS : Material.AIR;
                    world.getBlockAt(x, y, z).setType(material);
                }
            }
        }
    }
}

