package fr.d0gma.infinite.parkour;

import fr.d0gma.infinite.zone.Zone;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bukkit.block.Block;

public class ParkourRoadmap {

    private final Block         startBlock;
    private final List<Segment> segmentList = new ArrayList<>();
    private final Set<Integer>  checkpointZ = new HashSet<>();

    private int currentSegmentId = 0;

    public ParkourRoadmap(Block startBlock) {
        this.startBlock = startBlock;
        this.segmentList.add(new Segment(0, new ParkourSection(startBlock, startBlock, Zone.PAST, 0)));
    }

    public void addSection(ParkourSection parkourSection) {
        int z = parkourSection.startBlock().getZ();
        this.segmentList.add(new Segment(z, parkourSection));
        this.checkpointZ.add(z);
    }

    public void incrementCurrentSegmentId() {
        this.currentSegmentId++;
    }

    public boolean isCheckpoint(int z) {
        return this.checkpointZ.contains(z);
    }

    public Block getStartBlock() {
        return this.startBlock;
    }

    public ParkourSection getCurrentSection() {
        return this.segmentList.get(this.currentSegmentId).section();
    }

    public ParkourSection getLastGeneratedSection() {
        return this.segmentList.getLast().section();
    }

    public Zone getZone(int z) {
        if (this.segmentList.isEmpty())
            return Zone.FUTURE;

        if (isCheckpoint(z))
            return Zone.CHECKPOINT;

        for (int i = this.currentSegmentId; i >= 0; i--) {
            Segment segment = this.segmentList.get(i);

            if (z > segment.startZ())
                return segment.section().zone();
        }

        return Zone.PAST;
    }

    int getCheckpointReached(){
        return this.currentSegmentId - 1;
    }

    private record Segment(int startZ, ParkourSection section) {

    }

}
