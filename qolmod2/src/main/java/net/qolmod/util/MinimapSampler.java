package net.qolmod.util;

import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MinimapSampler {

    public static final int RADIUS = 40;
    public static final int SIZE   = RADIUS * 2 + 1;

    private final int[][] pixels = new int[SIZE][SIZE];
    private int tickCooldown = 0;

    public void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;
        if (--tickCooldown > 0) return;
        tickCooldown = 5;
        sample(client.world, (int) client.player.getX(), (int) client.player.getZ());
    }

    private void sample(World world, int cx, int cz) {
        for (int dz = -RADIUS; dz <= RADIUS; dz++) {
            for (int dx = -RADIUS; dx <= RADIUS; dx++) {
                int x = cx + dx;
                int z = cz + dz;
                int y = world.getTopY();
                BlockPos.Mutable pos = new BlockPos.Mutable(x, y, z);
                while (y > world.getBottomY()) {
                    pos.setY(--y);
                    if (!world.getBlockState(pos).isAir()) break;
                }
                MapColor mc = world.getBlockState(pos).getMapColor(world, pos);
                int raw = mc.color;
                float shade = Math.min(1f, (y + 64) / 128f);
                int r = (int)(((raw >> 16) & 0xFF) * shade);
                int g = (int)(((raw >> 8)  & 0xFF) * shade);
                int b = (int)(( raw        & 0xFF) * shade);
                pixels[dz + RADIUS][dx + RADIUS] = (0xFF << 24) | (r << 16) | (g << 8) | b;
            }
        }
    }

    public int[][] getPixels() { return pixels; }
}
