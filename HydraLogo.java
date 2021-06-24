package minegame159.meteorclient.systems.modules.ArtikHack;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.player.ChatUtils;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class HydraLogo extends Module {

    public HydraLogo() {
        super(Categories.ArtikHack, "hydra-logo", "Auto build Hydra Logo.");
    }

	byte[] x,z;
    BlockPos ppos = null;
    int prevslot = 0;
    
    @SuppressWarnings("incomplete-switch")
	@Override
    public void onActivate() {
        prevslot = mc.player.inventory.selectedSlot;
    	ppos = mc.player.getBlockPos();
    	byte[] vf = {-2,0,2, -1,0,1, -2,0,2, -2,-1,0,1,2, -1,1};
    	byte[] v1 = new byte[vf.length];
    	byte[] v_1 = new byte[vf.length];
    	for(byte x = 0; x < vf.length; x++){
    		v1[x] = 2;
    		v_1[x] = -2;
    	}
    	
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
    	if (ppos != mc.player.getBlockPos()) {
            toggle();
            return;
        }
    	
        if(p(x[0],  0, z[0])) return;
        if(p(x[1],  0, z[1])) return;
        if(p(x[2],  0, z[2])) return;
        
        if(p(x[3],  1, z[3])) return;
        if(p(x[4],  1, z[4])) return;
        if(p(x[5],  1, z[5])) return;
        
        if(p(x[6],  2, z[6])) return;
        if(p(x[7],  2, z[7])) return;
        if(p(x[8],  2, z[8])) return;
        
        if(p(x[9],  3, z[9])) return;
        if(p(x[10], 3, z[10])) return;
        if(p(x[11], 3, z[11])) return;
        if(p(x[12], 3, z[12])) return;
        if(p(x[13], 3, z[13])) return;
        
        if(p(x[14], 4, z[14])) return;
        if(p(x[15], 4, z[15])) return;

        mc.player.inventory.selectedSlot = prevslot;
        toggle();

    }

    private boolean p(int x, int y, int z) {
    	BlockPos pos = new BlockPos(mc.player.getX() + x, mc.player.getY() + y, mc.player.getZ() + z);

        int slot = InvUtils.findItemInHotbar(Items.OBSIDIAN);
        if(slot==-1){
	    	ChatUtils.moduleError(this, "В панеле быстрого доступа нет обсидиана!");
	    	toggle();
	    	return true;
        }
        if (!BlockUtils.canPlace(pos, true)) return false;
        mc.player.inventory.selectedSlot = slot;
        mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(mc.player.getPos(), Direction.UP, pos, true));
        
        return true;
    }
    
    
	
}
