package fr.d0gma.infinite.parkour;

import fr.d0gma.infinite.zone.Zone;
import org.bukkit.block.Block;

public record ParkourSection(Block startBlock, Block endBlock, Zone zone, float score) {

}
