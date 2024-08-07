package fr.d0gma.infinite.structure;

import fr.d0gma.infinite.parkour.Parkour;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import java.util.Random;

public interface Structure {

    void paste(Block block, Parkour parkour, Random random);

    default Vector getEnd(Block block, Parkour parkour, Random random) {
        return new Vector();
    }

}
