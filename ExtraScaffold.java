package minegame159.meteorclient.systems.modules.ArtikHack;

import java.util.HashSet;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.player.InteractItemEvent;
import minegame159.meteorclient.events.render.RenderEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.rendering.Renderer;
import minegame159.meteorclient.rendering.ShapeMode;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.ColorSetting;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.IntSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.render.color.SettingColor;
import net.minecraft.block.Block;
import net.minecraft.block.FallingBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;


public class ExtraScaffold extends Module {

    public ExtraScaffold(){
        super(Categories.ArtikHack, "extra-scaffold", "Automatically places blocks under you.");
    }
    
    public enum Dir{
        UP,
        DOWN
    }
    public enum esel {
    	Ignore, Only, Exclude
    }

    private final SettingGroup sgGeneral  = settings.getDefaultGroup();
    

    private final Setting<Dir> direction = sgGeneral.add(new EnumSetting.Builder<Dir>()
    		.name("Direction")
    		.defaultValue(Dir.DOWN)
    		.build());

    private final Setting<Integer> shift = sgGeneral.add(new IntSetting.Builder()
		    .name("shift")
		    .description("Shift your scaffold. (Up / Down)")
		    .defaultValue(0)
		    .min(-4)
		    .max(6)
		    .sliderMin(-4)
		    .sliderMax(6)
		    .build());

    private final Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
		    .name("radius")
		    .description("The radius of your scaffold.")
		    .defaultValue(0)
		    .min(0)
		    .sliderMax(6)
		    .build());
    

    private final Setting<Boolean> center = sgGeneral.add(new BoolSetting.Builder()
		    .name("center-first")
		    .description("Place center block first")
		    .defaultValue(true)
		    .build());
    
    private final Setting<Boolean> fall = sgGeneral.add(new BoolSetting.Builder()
		    .name("allow-falling-blocks")
		    .defaultValue(true)
		    .build());
    
	private final Setting<Boolean> checkchunk = sgGeneral
			.add(new BoolSetting.Builder()
			.name("chunk-border")
			.description("Break blocks in only current chunk.")
			.defaultValue(false)
			.build());
	
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Sends rotation packets to the server when placing.")
            .defaultValue(false)
            .build());
	
    private final Setting<esel> sel = sgGeneral.add(new EnumSetting.Builder<esel>()
            .name("selection")
            .description("Which positions to place on your top half.")
            .defaultValue(esel.Only)
            .build());

    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build());

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The color of the sides of the blocks being rendered.")
            .defaultValue(new SettingColor(106, 90, 205, 10))
            .build());

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The color of the lines of the blocks being rendered.")
            .defaultValue(new SettingColor(106, 90, 205, 255))
            .build());
    
    
	HashSet<BlockPos> selection = new HashSet<>();
	
    @EventHandler
    private void onRender(RenderEvent e) {
        for (BlockPos pos : selection) {
        	pos = new BlockPos(pos.getX(), mc.player.getBlockPos().getY()-1+shift.get(), pos.getZ());
        	Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        }
    }
	
	@EventHandler
	private void InteractItemEvent(InteractItemEvent e) {
		if(!mc.player.getMainHandStack().getItem().toString().contains("_sword")) return;
		
		BlockPos pos = ((BlockHitResult) mc.crosshairTarget).getBlockPos();
		pos = new BlockPos(pos.getX(),0,pos.getZ());
		if(mc.options.keyJump.isPressed()){
			selection.clear();
			return;
		}
		if(mc.options.keySneak.isPressed()){
			selection.remove(pos);
			return;
		}
		selection.add(pos);
	}

    @SuppressWarnings("incomplete-switch")
	@EventHandler
    private void ExtraScaffoldOnTick(TickEvent.Post event) {
    	
    	
    	if(!(mc.player.getMainHandStack().getItem() instanceof BlockItem)) return;
    	Block block = ((BlockItem) mc.player.getMainHandStack().getItem()).getBlock();
    	if(block instanceof ShulkerBoxBlock) return;
    	if(!fall.get() && block instanceof FallingBlock) return;
    	
    	int px = mc.player.getBlockPos().getX();
    	int py = mc.player.getBlockPos().getY()-1+shift.get();
    	int pz = mc.player.getBlockPos().getZ();
    	
    	
        for (int x = px - radius.get(); x <= px + radius.get(); x++) {
            for (int z = pz - radius.get(); z <= pz + radius.get(); z++) {

            	BlockPos pos = new BlockPos(x,py,z);
                if(!selection.isEmpty()){
                	switch (sel.get()) {
	                	case Only:
	                		if(!selection.contains(new BlockPos(x, 0, z))) continue;
	                		break;
	                	case Exclude:
	                		if(selection.contains(new BlockPos(x, 0, z))) continue;
                	}
                }
                
                if(center.get() && Ezz.BlockPlace(new BlockPos(px,py,pz), mc.player.inventory.selectedSlot, rotate.get())) return;
                if (checkchunk.get() && (mc.world.getChunk(pos).getPos() != mc.world.getChunk(mc.player.getBlockPos()).getPos()) ) continue;
                
                
                if (Ezz.distanceToBlockAnge(new BlockPos(x, py, z)) <= mc.interactionManager.getReachDistance() && Ezz.BlockPlace(pos, mc.player.inventory.selectedSlot, rotate.get())) return;
            }
        }
        
    }
    
    public Direction getDirection(){
    	return Direction.valueOf(direction.get().toString());
    }
    
//    private boolean place(BlockPos pos){
//    	Vec3d vec = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
//    	if(!BlockUtils.canPlace(pos, true)) return false;
//    	mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND, new BlockHitResult(vec, Direction.valueOf(direction.get().toString()), pos, false));
//    	return true;
//    	return Ezz.BlockPlace(pos, mc.player.inventory.selectedSlot, false);
//    }
    
    
}