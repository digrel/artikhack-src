/*
 * This file is part of the Meteor Client distribution (https://github.com/MeteorDevelopment/meteor-client/).
 * Copyright (c) 2020 Meteor Development.
 */

package minegame159.meteorclient.systems.modules.ArtikHack;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.mixin.MinecraftClientAccessor;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import net.minecraft.item.*;

public class CustomFastUse extends Module {
    public enum Item {
        All,
        Exp,
        Crystal,
        ExpAndCrystal
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<CustomFastUse.Item> itemChoose = sgGeneral.add(new EnumSetting.Builder<CustomFastUse.Item>()
            .name("Which item")
            .description(".")
            .defaultValue(CustomFastUse.Item.All)
            .build()
    );

    public CustomFastUse() {
        super(Categories.ArtikHack, "Custom-Fast-Use", "Removes item cooldown.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        switch(itemChoose.get()) {
            case All:
                setClickDelay();
                break;
            case Exp:
                assert mc.player != null;
                if(mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem || mc.player.getOffHandStack().getItem() instanceof ExperienceBottleItem)
                    setClickDelay();
                break;
            case Crystal:
                assert mc.player != null;
                if(mc.player.getMainHandStack().getItem() instanceof EndCrystalItem || mc.player.getOffHandStack().getItem() instanceof EndCrystalItem)
                    setClickDelay();
                break;
            case ExpAndCrystal:
                assert mc.player != null;
                if(mc.player.getMainHandStack().getItem() instanceof EndCrystalItem || mc.player.getMainHandStack().getItem() instanceof ExperienceBottleItem ||
                        mc.player.getOffHandStack().getItem() instanceof EndCrystalItem || mc.player.getOffHandStack().getItem() instanceof ExperienceBottleItem)
                    setClickDelay();
                break;
        }
    }

    private void setClickDelay() {
        ((MinecraftClientAccessor) mc).setItemUseCooldown(0);
    }
}
