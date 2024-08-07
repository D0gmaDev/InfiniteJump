package fr.d0gma.infinite.zone;

import fr.d0gma.infinite.structure.SchematicStructure;
import fr.d0gma.infinite.structure.SetSoloBlock;
import fr.d0gma.infinite.structure.SoloBlock;
import fr.d0gma.infinite.structure.Structure;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public enum Zone {

    PAST(0, 0, 0f, new SoloBlock(Material.AIR)),
    FUTURE(0, 0, 0f, new SoloBlock(Material.AIR)),
    CHECKPOINT(0, 0, 0f, new SoloBlock(Material.EMERALD_BLOCK)),

    NORMAL(15, 15, 1f, new SoloBlock(Material.WHITE_TERRACOTTA), 0, 60),
    GLASS(15, 10, 1f, new SetSoloBlock(Set.of(Material.RED_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS))),
    GLASS_PANE(6, 5, 2.5f, new SoloBlock(Material.WHITE_STAINED_GLASS_PANE), -1f, 30),
    FENCE(6, 5, 2f, new SoloBlock(Material.WARPED_FENCE), -1, 30),
    SLIME_BLOCK(4, 10, 2f, new SoloBlock(Material.SLIME_BLOCK), -1, 40, 2, 1),
    PACKED_ICE(4, 10, 2f, new SoloBlock(Material.PACKED_ICE), 1, 15),
    SOUL_SAND(5, 10, 1.2f, new SoloBlock(Material.SOUL_SAND), -1, 30),
    QUARTZ_STAIRS(20, 5, 1.2f, new SoloBlock(Material.QUARTZ_STAIRS, BlockFace.SOUTH), -1, 0),
    CHEST(5, 10, 1.2f, new SoloBlock(Material.CHEST)),
    BREWING_STAND(4, 10, 2f, new SoloBlock(Material.BREWING_STAND), -1, 50),
    BLEAK(4, 7, 1f, new SoloBlock(Material.SCULK_SENSOR), 0, 50, 2, 0),
    TRI_LADDERS(8, 1, 1.5f, new SchematicStructure("schematics/tri_ladders.schem", new Vector(-2, 0, 0), new Vector(0, 4, 3))),
    HAY_STACK(8, 1, 1.5f, new SchematicStructure("schematics/hay_stack.schem", new Vector(-2, 0, 0), new Vector(0, 3, 6))),

    ;

    private static final Set<Zone> unplayableZones = Set.of(PAST, FUTURE, CHECKPOINT);
    private static final List<Zone> playableZones = Arrays.stream(values()).filter(Predicate.not(unplayableZones::contains)).toList();

    private final int frequency;
    private final float complexity;
    private final Structure structure;
    private final int length;
    private final float distanceModifier;
    private final int maxAngle;
    private final int maxDescent;
    private final int maxAscent;

    Zone(int frequency, int length, float complexity, Structure structure) {
        this(frequency, length, complexity, structure, 0, 40);
    }

    Zone(int frequency, int length, float complexity, Structure structure, float distanceModifier, int maxAngle) {
        this(frequency, length, complexity, structure, distanceModifier, maxAngle, 1, 1);
    }

    Zone(int frequency, int length, float complexity, Structure structure, float distanceModifier, int maxAngle, int maxDescent, int maxAscent) {
        this.frequency = frequency;
        this.length = length + 1;
        this.complexity = complexity;
        this.structure = structure;
        this.distanceModifier = distanceModifier;
        this.maxAngle = maxAngle;
        this.maxDescent = maxDescent;
        this.maxAscent = maxAscent;
    }

    public static List<Zone> getPlayableZones() {
        return playableZones;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public Structure getStructure() {
        return this.structure;
    }

    public float getDistanceModifier() {
        return this.distanceModifier;
    }

    public int getMaxAngle() {
        return this.maxAngle;
    }

    public int getMaxDescent() {
        return this.maxDescent;
    }

    public int getMaxAscent() {
        return this.maxAscent;
    }

    public int getLength() {
        return this.length;
    }

    public float getComplexity() {
        return this.complexity;
    }
}


