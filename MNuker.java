package minegame159.meteorclient.systems.modules.ArtikHack;

import java.util.ArrayList;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.packets.PacketEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.DoubleSetting;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.IntSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.utils.player.ChatUtils;
import minegame159.meteorclient.utils.world.TickRate;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShapes;

public class MNuker extends Module {

    public MNuker() {
        super(Categories.ArtikHack, "m-nuker", "Auto Highway nuker.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> disableony = sgGeneral.add(new BoolSetting.Builder()
	    .name("disable-on-y-change")
	    .defaultValue(true)
	    .build()
	);
    
    public enum eType {
        None,
        Save,
        Replace
    }
  
    private final Setting<eType> itemsaver = sgGeneral.add(new EnumSetting.Builder<eType>()
            .name("item-saver")
            .description("Prevent destruction of tools.")
            .defaultValue(eType.Replace)
            .build()
    );
    
    
    private final Setting<Boolean> sword = sgGeneral.add(new BoolSetting.Builder()
	    .name("stop-on-sword")
	    .description("Pause nuker if sword in main hand.")
	    .defaultValue(true)
	    .build()
	);
    
    private final Setting<Integer> spamlimit = sgGeneral.add(new IntSetting.Builder()
	    .name("speed")
	    .description("Block break speed.")
	    .defaultValue(29)
	    .min(1)
	    .sliderMin(1)
	    .sliderMax(100)
	    .build()
	);
    
    private final Setting<Double> lagg = sgGeneral.add(new DoubleSetting.Builder()
	    .name("stop-on-lags")
	    .description("Pause on server lagging. (Time since last tick)")
	    .defaultValue(0.8)
	    .min(0.1)
	    .max(5)
	    .sliderMin(0.1)
	    .sliderMax(5)
	    .build()
	);

    private final Setting<Boolean> expand = sgGeneral.add(new BoolSetting.Builder()
	    .name("expand")
	    .description("Expand selection.")
	    .defaultValue(true)
	    .build()
    );
    
    
    int limit = 0;
	byte pause = 0;
	Direction dir = null;
	int ypos = 0;
	
    @Override
    public void onActivate() {
    	limit = 0;
    	pause = 0;
    	dir = mc.player.getHorizontalFacing();
    	ypos = mc.player.getBlockPos().getY();
	}
    

    @EventHandler (priority = Integer.MIN_VALUE)
    private void ADD_LIMIT(PacketEvent.Send e) {
    	if(!e.isCancelled()) limit++;
    }
    
    
    @EventHandler
    private void onTick(TickEvent.Pre event) {
    	
        try{
        	
        	if(pause > 0) {
        		pause--;
        		return;
        	}
        	
	    	if(disableony.get() && ypos != mc.player.getBlockPos().getY()){
        		ChatUtils.moduleError(this,"Высота изменилась!");
	    		toggle();
	    		return;
	    	}
	    	if(TickRate.INSTANCE.getTimeSinceLastTick() >= lagg.get()) return;
	    	if(sword.get() && mc.player.getMainHandStack().getItem() instanceof SwordItem) return;
	    	
	    	limit = 0;

	    	ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
	    	
	        int px = mc.player.getBlockPos().getX();
	        int py = mc.player.getBlockPos().getY();
	        int pz = mc.player.getBlockPos().getZ();
	        
	        
	        int dx=0,dz=0;
	        

	        if(expand.get())
	        	if(dir == Direction.EAST || dir == Direction.WEST) {
		        	dx=2;
		        } else {
		        	dz=2;
		        }
	        
	
	        for (int x = px - 1 - dx; x <= px + 1 + dx; x++) {
	        	for (int z = pz - 1 - dz; z <= pz + 1 + dz; z++) {
	        		for (int y = py; y <= py + 3; y++) {
	        			blocks.add(new BlockPos(x, y, z));
	        		}
	        	}
	        }

	        if(dir == Direction.EAST || dir == Direction.WEST) {
	        	for(int q = -1 - dx - dz; q <= 1 + dx + dz; q++){
	        		blocks.add(new BlockPos(px + q, py + 1, pz + 2));	
		        	blocks.add(new BlockPos(px + q, py + 1, pz - 2));	
	        	}
	        } else {
	        	for(int q = -1 - dx - dz; q <= 1 + dx + dz; q++){
	        		blocks.add(new BlockPos(px + 2, py + 1, pz + q));
	        		blocks.add(new BlockPos(px - 2, py + 1, pz + q));
	        	}
	        }
	        
	        for(int q = 0; q < blocks.size(); q++){
	        	
	        	BlockPos pos = blocks.get(q);
	        	
	            if(mc.world.getBlockState(pos).getOutlineShape(mc.world, pos) == VoxelShapes.empty()) continue;
	            if(mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK) continue;
	
	        	if(limit > spamlimit.get()) return;
	        	
	        	switch (itemsaver.get()) {
	        		case None:
	        			break;
	        		case Save:
	        			if(isbreak()) {
	                		ChatUtils.moduleWarning(this,"Инструмент почти разрушен!");
	          		toggle();
	          		return;
	        			}
	        		case Replace:
	        			if(isbreak()) {
	        				if(swap_item()){
	         				pause = 5;
	         				return;
	        				} else {
		                		ChatUtils.moduleWarning(this,"Инструмент почти разрушен!");
	              		toggle();
	              		return;
	        				}
	        			}
	        	}
	        	
	        	mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
	        	mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
	        }
            
        } catch (Exception ignored) {}
    }
    
    private boolean isbreak(){
    	if(mc.player.getMainHandStack().getDamage()!=0
    			&& mc.player.getMainHandStack().getMaxDamage()-mc.player.getMainHandStack().getDamage()<31) return true;
    	return false;
    }

    
    private boolean swap_item(){
    	Item item = mc.player.getMainHandStack().getItem();
    	for(int x=0; x < mc.player.inventory.size(); x++){
    		if(mc.player.inventory.getStack(x).getItem() != item) continue;
    		if(mc.player.inventory.getStack(x).getMaxDamage() - mc.player.inventory.getStack(x).getDamage() < 31) continue;
    		Ezz.clickSlot(Ezz.invIndexToSlotId(x), mc.player.inventory.selectedSlot, SlotActionType.SWAP);
    		return true;
    	}
    	return false;
    }
    
}
