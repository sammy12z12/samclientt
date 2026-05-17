package net.qolmod.features;

import net.minecraft.client.MinecraftClient;

public class Fullbright {

    private boolean enabled = false;
    private double savedGamma = 1.0;

    public void toggle(MinecraftClient client) {
        enabled = !enabled;
        if (enabled) {
            savedGamma = client.options.getGamma().getValue();
        } else {
            client.options.getGamma().setValue(savedGamma);
            client.options.write();
        }
    }

    public void onTick(MinecraftClient client) {
        if (!enabled || client.options == null) return;
        client.options.getGamma().setValue(16.0);
    }

    public boolean isEnabled() { return enabled; }
}
