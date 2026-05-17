package net.qolmod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.qolmod.features.Fullbright;
import net.qolmod.hud.HudRenderer;
import org.lwjgl.glfw.GLFW;

public class QolMod implements ClientModInitializer {

    public static final Fullbright FULLBRIGHT = new Fullbright();
    private static KeyBinding fullbrightKey;

    @Override
    public void onInitializeClient() {
        // Register F4 keybind for fullbright
        fullbrightKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.qolmod.fullbright",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F4,
            "category.qolmod"
        ));

        // Register HUD renderer
        HudRenderCallback.EVENT.register(new HudRenderer());

        // Tick events
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Poll fullbright keybind
            while (fullbrightKey.wasPressed()) {
                FULLBRIGHT.toggle(client);
            }
            // Update CPS + minimap sampler
            HudRenderer.onTick(client);
        });
    }
}
