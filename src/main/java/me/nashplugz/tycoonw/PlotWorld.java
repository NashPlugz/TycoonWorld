package me.nashplugz.tycoonw;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.bukkit.Material;
import java.util.Random;

public class PlotWorld extends ChunkGenerator {
    public static final int PLOT_SIZE = 35;
    public static final int BORDER_WIDTH = 1;
    public static final int PATH_WIDTH = 3;
    public static final int PLOT_HEIGHT = 64;
    public static final int TOTAL_SIZE = PLOT_SIZE + 2 * BORDER_WIDTH + PATH_WIDTH;

    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int chunkX, int chunkZ, ChunkData chunkData) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int worldX = chunkX * 16 + x;
                int worldZ = chunkZ * 16 + z;
                generatePlotAt(chunkData, worldX, worldZ);
            }
        }
    }

    private void generatePlotAt(ChunkData chunk, int worldX, int worldZ) {
        int plotX = Math.floorDiv(worldX, TOTAL_SIZE);
        int plotZ = Math.floorDiv(worldZ, TOTAL_SIZE);
        int relativeX = Math.floorMod(worldX, TOTAL_SIZE);
        int relativeZ = Math.floorMod(worldZ, TOTAL_SIZE);

        // Bedrock base
        chunk.setBlock(worldX & 15, 0, worldZ & 15, Material.BEDROCK);

        // Fill with stone up to PLOT_HEIGHT - 1
        for (int y = 1; y < PLOT_HEIGHT; y++) {
            chunk.setBlock(worldX & 15, y, worldZ & 15, Material.STONE);
        }

        // Plot area
        if (relativeX >= BORDER_WIDTH && relativeX < PLOT_SIZE + BORDER_WIDTH &&
                relativeZ >= BORDER_WIDTH && relativeZ < PLOT_SIZE + BORDER_WIDTH) {
            chunk.setBlock(worldX & 15, PLOT_HEIGHT - 1, worldZ & 15, Material.DIRT);
            chunk.setBlock(worldX & 15, PLOT_HEIGHT, worldZ & 15, Material.GRASS_BLOCK);
        }
        // Border area
        else if ((relativeX == BORDER_WIDTH - 1 || relativeX == PLOT_SIZE + BORDER_WIDTH) &&
                (relativeZ >= BORDER_WIDTH - 1 && relativeZ <= PLOT_SIZE + BORDER_WIDTH) ||
                (relativeZ == BORDER_WIDTH - 1 || relativeZ == PLOT_SIZE + BORDER_WIDTH) &&
                        (relativeX >= BORDER_WIDTH - 1 && relativeX <= PLOT_SIZE + BORDER_WIDTH)) {
            chunk.setBlock(worldX & 15, PLOT_HEIGHT, worldZ & 15, Material.STONE);
            chunk.setBlock(worldX & 15, PLOT_HEIGHT + 1, worldZ & 15, Material.SMOOTH_STONE_SLAB);
        }
        // Path area
        else {
            chunk.setBlock(worldX & 15, PLOT_HEIGHT, worldZ & 15, Material.BIRCH_PLANKS);
        }
    }

    @Override
    public boolean shouldGenerateNoise() {
        return false;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return false;
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return false;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return false;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }
}
