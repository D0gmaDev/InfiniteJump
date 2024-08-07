package fr.d0gma.infinite.structure;

import fr.d0gma.infinite.parkour.Parkour;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

import java.util.Random;

public class SoloBlock implements Structure {

    private final Material  material;
    private final BlockFace blockFace;

    public SoloBlock(Material material, BlockFace blockFace) {
        this.material = material;
        this.blockFace = blockFace;
    }

    public SoloBlock(Material material) {
        this(material, null);
    }

    @Override
    public void paste(Block block, Parkour parkour, Random random) {
        block.setType(material);

        BlockData blockData = block.getBlockData();

        if (blockData instanceof Directional directional && this.blockFace != null) {
            directional.setFacing(this.blockFace);
            block.setBlockData(directional);
        }
    }
}
