package minegame159.meteorclient.systems.modules.ArtikHack;


import org.apache.logging.log4j.LogManager;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.packets.PacketEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.DoubleSetting;
import minegame159.meteorclient.settings.IntSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.utils.player.ChatUtils;
import minegame159.meteorclient.utils.player.CityUtils;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.player.PlayerUtils;
import minegame159.meteorclient.utils.player.Rotations;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoCrystalHead extends Module {

    public AutoCrystalHead() {
        super(Categories.ArtikHack, "AutoCrystalHead", "Prevent the player from losing the respawn point.");
    }
    
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The amount of delay in ticks before placing.")
        .defaultValue(4)
        .min(0)
        .sliderMax(20)
		.build());
    
    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
    	.name("range")
        .description("The break range.")
        .defaultValue(5)
        .min(0)
        .build());
    
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
        .name("rotate")
        .description("Automatically faces the blocks being mined.")
        .defaultValue(false)
        .build());
    
    
    BlockPos pos = null;
    int pause = 0;
    boolean isDone = false;
    boolean firtDone = false;
    
	

    @EventHandler
    private void  BlockUpdate(PacketEvent.Receive e) {
        if ( !(e.packet instanceof BlockUpdateS2CPacket)) return;
        BlockUpdateS2CPacket p = (BlockUpdateS2CPacket) e.packet;
        if(Ezz.equalsBlockPos(p.getPos(), pos) && p.getState().isAir()) isDone = true;
    }
    
    
    private void s(String s){
    	LogManager.getLogger().info(s);
    }
    
    @EventHandler
    private void  AntiClick(PacketEvent.Send e) {
        if (e.packet instanceof PlayerActionC2SPacket){
        	PlayerActionC2SPacket p = (PlayerActionC2SPacket) e.packet;
        	s(p.getAction()+" "+p.getPos());
        	if(
        			(
        					p.getAction() == PlayerActionC2SPacket.Action.START_DESTROY_BLOCK
        					|| p.getAction() == PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK
        					|| p.getAction() == PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK
        			)
        			&& !Ezz.equalsBlockPos(p.getPos(), pos)) {
        		s("cancel!");
        		e.cancel();
        	}
        }
    }    
    
    
    
	@EventHandler
    private void onTick(TickEvent.Pre e) {
        if (mc.world == null || mc.player == null) return;
        
        if(pause > 0) {pause--; return;}
        
        pause = delay.get();
        PlayerEntity player = CityUtils.getPlayerTarget(7);
		if(player == null) return;
		BlockPos obsidianPos = new BlockPos(player.getBlockPos().getX(),player.getBlockPos().getY() + 2, player.getBlockPos().getZ());
    	if(Ezz.DistanceTo(obsidianPos) > range.get()) return;
    	BlockPos head = new BlockPos(player.getBlockPos().getX(),player.getBlockPos().getY() + 1, player.getBlockPos().getZ());
    	if(mc.world.getBlockState(head).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(head).getBlock() == Blocks.BEDROCK) return;
    	
        BlockPos crystalPos = new BlockPos(player.getBlockPos().getX(),player.getBlockPos().getY() + 3, player.getBlockPos().getZ());
    	
    	if( (!mc.world.getBlockState(obsidianPos).isAir() && mc.world.getBlockState(obsidianPos).getBlock() != Blocks.OBSIDIAN) ) return;
        
    	int pickaxe = InvUtils.findItemInHotbar(Items.IRON_PICKAXE);
    	if(pickaxe == -1) pickaxe = InvUtils.findItemInHotbar(Items.NETHERITE_PICKAXE);
    	if(pickaxe == -1) pickaxe = InvUtils.findItemInHotbar(Items.DIAMOND_PICKAXE);
    	if(pickaxe == -1) {
    		ChatUtils.moduleError(this, "В панеле быстрого доступа нет кирки!");
    		toggle();
    		return;
    	}
    	
        if(mc.world.getBlockState(obsidianPos).getBlock() != Blocks.OBSIDIAN) Ezz.BlockPlace(obsidianPos, InvUtils.findItemInHotbar(Items.OBSIDIAN), rotate.get());
        
        if(!Ezz.equalsBlockPos(pos, obsidianPos)){
        	pos = obsidianPos;
        	Ezz.swap(pickaxe);
        	mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, Direction.UP));
        	mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
        	
        	isDone = false;
        	return;
        }
        
        if(!isDone) return;
        
		EndCrystalEntity crystal = null;
        for (Entity findcrystal : mc.world.getEntities()) {
            if ( findcrystal instanceof EndCrystalEntity && Ezz.equalsBlockPos(findcrystal.getBlockPos(), crystalPos)) {
            	crystal = (EndCrystalEntity) findcrystal;
            	break;
            }
        }
        if(crystal != null){
        	if(rotate.get()){
	        	float[] rotation = PlayerUtils.calculateAngle(crystal.getPos());
	        	Rotations.rotate(rotation[0], rotation[1]);
        	}
        	
        	int preSlot = mc.player.inventory.selectedSlot;
        	Ezz.swap(pickaxe);
        	mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));
        	Ezz.swap(preSlot);
        	
        	Ezz.attackEntity(crystal);
        	return;
        }
        
        	
        placeCrystal(player, obsidianPos);
        
        
    }
	
	
	
	private boolean placeCrystal(PlayerEntity player, BlockPos obsidianPos){
        BlockPos crystalPos = new BlockPos(player.getBlockPos().getX(),player.getBlockPos().getY() + 3, player.getBlockPos().getZ());
    	if(!BlockUtils.canPlace(crystalPos, true)) return false;
    	if(!mc.world.getBlockState(crystalPos).isAir()) return false;
    	int crystalSlot = InvUtils.findItemInHotbar(Items.END_CRYSTAL);
    	if(crystalSlot == -1) {
    		ChatUtils.moduleError(this, "В панеле быстрого доступа нет кристалов!");
    		toggle();
    		return false;
    	}
    	Ezz.interact(obsidianPos, crystalSlot, Direction.DOWN);
    	return true;
	}
	
    
    
}