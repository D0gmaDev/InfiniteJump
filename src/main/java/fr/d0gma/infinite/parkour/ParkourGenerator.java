package fr.d0gma.infinite.parkour;

import fr.d0gma.infinite.zone.Zone;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Random;

public class ParkourGenerator {

    private final Random random;
    private final Curve curve;
    private final Parkour parkour;
    private final List<Zone> zones;

    private final int totalFrequency;

    public ParkourGenerator(Random random, Block spawn, Parkour parkour, List<Zone> zones) {
        this.random = random;
        this.curve = new Curve(random, spawn.getX(), spawn.getY());
        this.parkour = parkour;
        this.zones = zones;
        this.totalFrequency = zones.stream().mapToInt(Zone::getFrequency).sum();
    }

    public ParkourSection generateNewSection(ParkourSection lastSection, float lengthMultiplier) {

        float sectionScore = 0;

        Block block = lastSection.endBlock();
        Zone zone = pickNextZone();

        this.curve.moveCurve(0, -1);
        block = block.getRelative(0, -1, 0);

        int length = Math.round(zone.getLength() * lengthMultiplier);

        for (int i = 0; i < length; i++) {
            ParkourBlock parkourBlock = nextBlock(block, zone, i == length - 1);
            block = parkourBlock.endBlock();
            sectionScore += parkourBlock.score();
        }

        return new ParkourSection(lastSection.endBlock(), block, zone, sectionScore);
    }

    private ParkourBlock nextBlock(Block block, Zone type, boolean end) {
        Curve.NextPoint next = this.curve.nextPoint(4 + type.getDistanceModifier(), 1, type.getMaxAngle(), type.getMaxDescent(), type.getMaxAscent());

        if (end) {
            type = Zone.CHECKPOINT;
        }

        block = block.getRelative(next.diffX(), next.diffY(), next.diffZ());

        type.getStructure().paste(block, this.parkour, this.random);

        if (type == Zone.CHECKPOINT) {
            placeCheckpoint(block);
        }

        Vector endVector = type.getStructure().getEnd(block, this.parkour, this.random);

        this.curve.moveCurve(endVector.getBlockX(), endVector.getBlockY());

        return new ParkourBlock(block.getRelative(endVector.getBlockX(), endVector.getBlockY(), endVector.getBlockZ()), next.score());
    }


    private void placeCheckpoint(Block block) {
        block.getRelative(0, 1, 0).setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
    }

    private Zone pickNextZone() {
        int randomPick = this.random.nextInt(this.totalFrequency);

        for (Zone zone : this.zones) {
            randomPick -= zone.getFrequency();
            if (randomPick <= 0) {
                return zone;
            }
        }
        return Zone.FUTURE;
    }

    private record ParkourBlock(Block endBlock, float score) {

    }

}
