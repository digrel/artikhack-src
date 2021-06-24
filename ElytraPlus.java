package minegame159.meteorclient.systems.modules.ArtikHack;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class ElytraPlus extends Module {
    public enum Mode {
        Boost,
        Fly
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<ElytraPlus.Mode> mode = sgGeneral.add(new EnumSetting.Builder<ElytraPlus.Mode>()
            .name("mode")
            .description("The mode used to make you fly.")
            .defaultValue(ElytraPlus.Mode.Fly)
            .build()
    );

    public ElytraPlus() {
        super(Categories.ArtikHack, "elytra+", "Allows infinite elytra flying");
    }

    @Override
    public void onDeactivate() {
        assert mc.player != null;
        if (mc.player.abilities.creativeMode) return;
        mc.player.abilities.flying = false;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        ClientPlayerEntity player = mc.player;
        assert player != null;
        if (!player.isFallFlying()) return;
        switch (mode.get()) {
            case Boost:
                Vec3d vec3d = new Vec3d(0, 0, 0);
                if (player.isSubmergedInWater()) {
                    Optional.ofNullable(mc.getNetworkHandler()).ifPresent(handler -> handler.sendPacket(new ClientCommandC2SPacket(player, ClientCommandC2SPacket.Mode.START_FALL_FLYING)));
                    return;
                }

                if (mc.options.keyJump.isPressed()) {
                    vec3d.add(mc.player.getX(), mc.player.getY() + 0.08, mc.player.getZ());
                } else if (mc.options.keySneak.isPressed()) {
                    vec3d.add(mc.player.getX(), mc.player.getY() - 0.04, mc.player.getZ());
                }

                if (mc.options.keyForward.isPressed()) {
                    float yaw = (float) Math.toRadians(player.yaw);
                    player.addVelocity(MathHelper.sin(yaw) * -0.05F, 0, MathHelper.cos(yaw) * 0.05F);
                } else if (mc.options.keyBack.isPressed()) {
                    float yaw = (float) Math.toRadians(player.yaw);
                    player.addVelocity(MathHelper.sin(yaw) * 0.05F, 0, MathHelper.cos(yaw) * -0.05F);
                }
                break;
            case Fly:
                player.abilities.flying = true;
        }
    };
}
