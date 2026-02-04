package mp.example.classes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Habitacion {

    private final World world;
    private final Location base;

    private final int ancho;
    private final int alto;
    private final int largo;

    private final int minX, maxX;
    private final int minY, maxY;
    private final int minZ, maxZ;

    private final Set<UUID> jugadores = new HashSet<>();

    private int objetivo;
    private String id;

    public Habitacion(Player player, int ancho, int alto, int largo, int objetivo, String id) {

        this.world = player.getWorld();
        this.base = player.getLocation().getBlock().getLocation();

        this.ancho = ancho;
        this.alto = alto;
        this.largo = largo;
        this.objetivo = objetivo;
        this.id = id;

        // Calculamos esquinas UNA SOLA VEZ
        this.minX = base.getBlockX();
        this.minY = base.getBlockY();
        this.minZ = base.getBlockZ();

        this.maxX = minX + ancho - 1;
        this.maxY = minY + alto - 1;
        this.maxZ = minZ + largo - 1;
    }

    public String getId() {
        return id;
    }

    public int getObjetivo() {
        return objetivo;
    }
    public void setObjetivo(int objetivo) {
        this.objetivo = objetivo;
    }

    /* =========================
       DETECTAR SI PLAYER ESTA DENTRO
       ========================= */

    public boolean estaDentro(Location loc) {

        if (!loc.getWorld().equals(world)) return false;

        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();

        return x >= minX && x <= maxX
                && y >= minY && y <= maxY
                && z >= minZ && z <= maxZ;
    }

    /* =========================
       GENERAR CAJA
       ========================= */

    public void generar() {

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {

                    boolean esBorde =
                            x == minX || x == maxX ||
                                    y == minY || y == maxY ||
                                    z == minZ || z == maxZ;

                    Material material = esBorde ? Material.GLASS : Material.AIR;
                    world.getBlockAt(x, y, z).setType(material);
                }
            }
        }
    }

    /* =========================
       ACTUALIZAR JUGADOR
       ========================= */

    public void actualizarJugador(Player player) {

        boolean dentro = estaDentro(player.getLocation());
        UUID uuid = player.getUniqueId();

        if (dentro) {
            jugadores.add(uuid);
            Bukkit.getLogger().info(player.getName() + " entro en habitacion " + id);

        } else {
            jugadores.remove(uuid);
            Bukkit.getLogger().info(player.getName() + " salio de habitacion " + id);

        }
    }

    /* =========================
       COMPROBAR OBJETIVO
       ========================= */

    public boolean cumpleObjetivo() {
        return jugadores.size() == objetivo;
    }

    /* =========================
       devolver lista players
       ========================= */
    public Set<UUID> getJugadores() {
        return jugadores;
    }
}

