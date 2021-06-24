package minegame159.meteorclient.systems.modules.ArtikHack;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.DoubleSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.utils.player.InvUtils;
import net.minecraft.block.Material;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoTNT extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> maxDistance = sgGeneral.add(new DoubleSetting.Builder()
            .name("distance")
            .description("Maximum distance.")
            .defaultValue(5)
            .build());

    public AutoTNT() {
        super(Categories.ArtikHack, "auto-tnt", "Auto TNT.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
    	
    		if(mc.options.keySneak.isPressed()) return;

            HitResult hitResult = mc.player.raycast(maxDistance.get(), 1f / 20f, false);

            if (hitResult.getType() == HitResult.Type.ENTITY && mc.player.interact(((EntityHitResult) hitResult).getEntity(), Hand.MAIN_HAND) != ActionResult.PASS) return;

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
                
                if(mc.world.getBlockState(pos).getMaterial()==Material.TNT){
                	
            	    int fire = InvUtils.findItemInHotbar(Items.FLINT_AND_STEEL);
            	    if (fire == -1) {
            	    	InvUtils.findItemInHotbar(Items.FIRE_CHARGE);
            	    }
            	    
                	if(fire!=-1){
                		
                        int preSlot = mc.player.inventory.selectedSlot;
                        mc.player.inventory.selectedSlot = fire;
                        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND,
                        		new BlockHitResult(mc.player.getPos(), Direction.UP, pos, true));

                        mc.player.inventory.selectedSlot = preSlot;
                		
                		}
                	}
                }
    }
}
