package minegame159.meteorclient.systems.modules.ArtikHack;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.utils.player.ChatUtils;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoPortal extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> disableOnMove = sgGeneral.add(new BoolSetting.Builder()
            .name("disable-on-move")
            .description("Automatically disables when you move.")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Automatically faces towards the obsidian being placed.")
            .defaultValue(false)
            .build()
    );


    public AutoPortal() {
        super(Categories.ArtikHack, "auto-portal", "Auto build nether portal.");
    }


	byte[] x,z;
    BlockPos ppos = null;
    
    @SuppressWarnings("incomplete-switch")
	@Override
    public void onActivate() {
    	ppos = mc.player.getBlockPos();
    	byte[] v1 = {2,2, 2,2, 2,2, 2,2, 2,2, 2};
    	byte[] v_1 = {-2,-2, -2,-2, -2,-2, -2,-2, -2,-2, -2};
    	byte[] vf = {0,-1, 1,-2, 1,-2, 1,-2, 0,-1, 0};
    	
    	switch (mc.player.getHorizontalFacing()) {
			case EAST:
				x = v1;
				z = vf;
				break;
			case SOUTH:
				x = vf;
				z = v1;
				break;
			case WEST:
				x = v_1;
				z = vf;
				break;
			case NORTH:
				x = vf;
				z = v_1;
				break;
			}
    	
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
    	if (disableOnMove.get() && ppos != mc.player.getBlockPos()) {
            toggle();
            return;
        }
    	
    	

	    int fire = InvUtils.findItemInHotbar(Items.FLINT_AND_STEEL);
	    if(fire == -1) fire = InvUtils.findItemInHotbar(Items.FIRE_CHARGE);
	    if(fire == -1){
	    	ChatUtils.moduleError(this, "В панеле быстрого доступа нет зажигалки!");
	    	toggle();
	    	return;
	    }
    	
    	
        if(p(x[0],  0, z[0])) return;
        if(p(x[1],  0, z[1])) return;
        if(p(x[2],  1, z[2])) return;
        if(p(x[3],  1, z[3])) return;
        if(p(x[4],  2, z[4])) return;
        if(p(x[5],  2, z[5])) return;
        if(p(x[6],  3, z[6])) return;
        if(p(x[7],  3, z[7])) return;
        if(p(x[8],  4, z[8])) return;
        if(p(x[9],  4, z[9])) return;



    	    
        BlockPos pos = new BlockPos(mc.player.getX()+x[10], mc.player.getY()+1, mc.player.getZ()+z[10]);
        int preSlot = mc.player.inventory.selectedSlot;
        mc.player.inventory.selectedSlot = fire;
        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND,
          		new BlockHitResult(mc.player.getPos(), Direction.UP, pos, true));
        mc.player.inventory.selectedSlot = preSlot;
        	
        toggle();
            
        
    };

    private boolean p(int x, int y, int z) {
    	BlockPos blockPos = new BlockPos(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);

        if( (mc.player.getY()+y)<0 || (mc.player.getY()+y)>254) return true;
        
        int slot = InvUtils.findItemInHotbar(Items.OBSIDIAN);
        if(slot==-1){
	    	ChatUtils.moduleWarning(this, "В панеле быстрого доступа нет обсидиана!");
	    	toggle();
	    	return true;
        }
        if (BlockUtils.place(blockPos, Hand.MAIN_HAND, slot, rotate.get(), 100,true)) {
            return true;
        }
        return false;
    }

    

}
