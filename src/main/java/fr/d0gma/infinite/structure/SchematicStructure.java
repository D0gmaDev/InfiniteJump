package fr.d0gma.infinite.structure;

import fr.d0gma.core.world.schematic.Schematic;
import fr.d0gma.infinite.parkour.Parkour;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Random;

public class SchematicStructure implements Structure {

    private final Schematic schematic;
    private final Vector startOffset;
    private final Vector endOffset;

    public SchematicStructure(String schematicName, Vector startOffset, Vector endOffset) {
        schematic = null; //TODO load Schematic
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public void paste(Block block, Parkour parkour, Random random) {
        schematic.paste(block.getRelative(startOffset.getBlockX(), startOffset.getBlockY(), startOffset.getBlockZ()).getLocation());
    }

    @Override
    public Vector getEnd(Block block, Parkour parkour, Random random) {
        return this.endOffset;
    }
}
