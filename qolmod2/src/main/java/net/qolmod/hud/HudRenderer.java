package net.qolmod.hud;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.qolmod.QolMod;
import net.qolmod.util.CpsTracker;
import net.qolmod.util.MinimapSampler;

public class HudRenderer implements HudRenderCallback {

    public static final CpsTracker CPS = new CpsTracker();
    private static final MinimapSampler MINIMAP = new MinimapSampler();

    private static final int WHITE  = 0xFFFFFFFF;
    private static final int YELLOW = 0xFFFFFF55;
    private static final int CYAN   = 0xFF55FFFF;
    private static final int GREEN  = 0xFF55FF55;
    private static final int RED    = 0xFFFF5555;
    private static final int BLACK  = 0xFF000000;

    private static final int MAP_SIZE = 81;

    public static void onTick(MinecraftClient client) {
        CPS.decay();
        MINIMAP.tick(client);
        QolMod.FULLBRIGHT.onTick(client);
    }

    @Override
    public void onHudRender(DrawContext ctx, RenderTickCounter tickCounter) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.options.hudHidden) return;

        TextRenderer tr = mc.textRenderer;
        PlayerEntity player = mc.player;
        int screenW = mc.getWindow().getScaledWidth();
        int y = 2;

        // FPS
        int fps = mc.getCurrentFps();
        int fpsColor = fps >= 60 ? GREEN : fps >= 30 ? YELLOW : RED;
        ctx.drawText(tr, "FPS: " + fps, 2, y, fpsColor, true);
        y += 10;

        // CPS
        ctx.drawText(tr, "CPS: " + CPS.getLeftCps() + "  R: " + CPS.getRightCps(), 2, y, CYAN, true);
        y += 10;

        // XYZ
        int px = (int) Math.floor(player.getX());
        int py = (int) Math.floor(player.getY());
        int pz = (int) Math.floor(player.getZ());
        ctx.drawText(tr, "XYZ: " + px + " / " + py + " / " + pz, 2, y, WHITE, true);
        y += 10;

        // Direction
        Direction facing = player.getHorizontalFacing();
        float yaw = MathHelper.wrapDegrees(player.getYaw());
        ctx.drawText(tr, "Facing: " + facing.getName().toUpperCase() + String.format(" (%.1f°)", yaw), 2, y, YELLOW, true);
        y += 10;

        // Reach
        double reach = mc.interactionManager != null ? mc.interactionManager.getReachDistance() : 4.5;
        ctx.drawText(tr, String.format("Reach: %.1f", reach), 2, y, WHITE, true);
        y += 10;

        // Fullbright indicator
        if (QolMod.FULLBRIGHT.isEnabled()) {
            ctx.drawText(tr, "Fullbright: ON", 2, y, YELLOW, true);
        }

        // Minimap (top-right)
        renderMinimap(ctx, tr, screenW - MAP_SIZE - 4, 4);
    }

    private void renderMinimap(DrawContext ctx, TextRenderer tr, int ox, int oy) {
        int[][] pixels = MINIMAP.getPixels();

        // Border
        ctx.fill(ox - 1, oy - 1, ox + MAP_SIZE + 1, oy + MAP_SIZE + 1, BLACK);

        // Blocks
        for (int row = 0; row < MinimapSampler.SIZE; row++) {
            for (int col = 0; col < MinimapSampler.SIZE; col++) {
                int color = pixels[row][col];
                if (color == 0) continue;
                ctx.fill(ox + col, oy + row, ox + col + 1, oy + row + 1, color);
            }
        }

        // Player dot
        int c = MAP_SIZE / 2;
        ctx.fill(ox + c - 1, oy + c - 1, ox + c + 2, oy + c + 2, WHITE);

        // Cardinal directions
        ctx.drawText(tr, "N", ox + MAP_SIZE / 2 - 2, oy - 9,  WHITE, true);
        ctx.drawText(tr, "S", ox + MAP_SIZE / 2 - 2, oy + MAP_SIZE + 1, WHITE, true);
        ctx.drawText(tr, "W", ox - 7,                oy + MAP_SIZE / 2 - 4, WHITE, true);
        ctx.drawText(tr, "E", ox + MAP_SIZE + 2,     oy + MAP_SIZE / 2 - 4, WHITE, true);
    }
}
