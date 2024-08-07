package fr.d0gma.infinite.structure;

import fr.d0gma.infinite.parkour.Parkour;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class SetSoloBlock implements Structure {

    private final List<Material> materials;

    public SetSoloBlock(Set<Material> materials) {
        this.materials = List.copyOf(materials);
    }

    @Override
    public void paste(Block block, Parkour parkour, Random random) {
        block.setType(this.materials.get(random.nextInt(this.materials.size())));
    }
}
