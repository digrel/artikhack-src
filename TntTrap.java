package minegame159.meteorclient.systems.modules.ArtikHack;

import java.util.ArrayList;
import java.util.List;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.render.RenderEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.rendering.Renderer;
import minegame159.meteorclient.rendering.ShapeMode;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.ColorSetting;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.IntSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.utils.player.CityUtils;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.render.color.SettingColor;
import minegame159.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class TntTrap extends Module {



    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");



    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("range")
            .description("The radius players can be in to be targeted.")
            .defaultValue(5)
            .sliderMin(0)
            .sliderMax(10)
            .build()
    		);


    private final Setting<Boolean> head2 = sgGeneral.add(new BoolSetting.Builder()
	    .name("head +1")
	    .description("Tnt head place.")
	    .defaultValue(false)
	    .build()
    		);
    
    private final Setting<Boolean> head = sgGeneral.add(new BoolSetting.Builder()
	    .name("head")
	    .description("Tnt head place.")
	    .defaultValue(true)
	    .build()
	);
    
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Sends rotation packets to the server when placing.")
            .defaultValue(false)
            .build()
    		);


    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("Renders a block overlay where the obsidian will be placed.")
            .defaultValue(true)
            .build()
	);

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build()
	);

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The color of the sides of the blocks being rendered.")
            .defaultValue(new SettingColor(204, 0, 0, 10))
            .build()
	);

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The color of the lines of the blocks being rendered.")
            .defaultValue(new SettingColor(204, 0, 0, 255))
            .build()
	);

    private PlayerEntity target = null;
    private List<BlockPos> placePositions = new ArrayList<>();
    private boolean placed;

    public TntTrap(){
        super(Categories.ArtikHack, "tnt-trap", "Anti Surround.");
    }


    @EventHandler
    private void onTick(TickEvent.Pre event) {
    	
        target = CityUtils.getPlayerTarget(range.get());
        
        
        if (target == null || mc.player.distanceTo(target) > range.get()) return;
        

        placed = false;

        placePositions.clear();
        

    	int tnt = -1,fire = -1;
    	
    	tnt = InvUtils.findItemInHotbar(Items.TNT);
	    fire = InvUtils.findItemInHotbar(Items.FLINT_AND_STEEL);
	
	    if (fire == -1) fire = InvUtils.findItemInHotbar(Items.FIRE_CHARGE);
	    
        if (tnt == -1 || fire == -1) return;
        
        
        
        findPlacePos(target);

        for (int x = 0; x < placePositions.size(); x++) {
            BlockPos blockPos = placePositions.get(placePositions.size() - 1);

            if (BlockUtils.place(blockPos, Hand.MAIN_HAND, tnt, rotate.get(), 50,true)) {
                placePositions.remove(blockPos);
                placed = true;
            }

            
            //fire
            if(placed && fire!=-1){
                int preSlot = mc.player.inventory.selectedSlot;
                mc.player.inventory.selectedSlot = fire;
                mc.interactionManager.interactBlock(mc.player, mc.world, Hand.MAIN_HAND,
                		new BlockHitResult(mc.player.getPos(), Direction.UP, blockPos, true));

                mc.player.inventory.selectedSlot = preSlot;
            }
            
            
            
        }
        
        target = null;
        
 
    }

    @EventHandler
    private void onRender(RenderEvent event) {
        if (!render.get() || placePositions.isEmpty()) return;
        for (BlockPos pos : placePositions) Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }




    private void add(BlockPos blockPos) {
        if (!placePositions.contains(blockPos)
        	&& mc.world.getBlockState(blockPos).getMaterial().isReplaceable()
        	&& mc.world.canPlace(Blocks.TNT.getDefaultState(), blockPos, ShapeContext.absent())
//        	&& mc.world.getBlockState(
//        			new BlockPos(blockPos.getX(), blockPos.getY()-1, blockPos.getZ())).isSolidBlock(mc.world, 
//        					new BlockPos(blockPos.getX(), blockPos.getY()-1, blockPos.getZ()))
        	) {
        	placePositions.add(blockPos);
        	
        }
    }
    
    
    
    
    
    
    
    private void findPlacePos(PlayerEntity target) {
        placePositions.clear();
        BlockPos targetPos = target.getBlockPos();

        add(targetPos.add(1, 0, 0));
        add(targetPos.add(0, 0, 1));
        add(targetPos.add(-1, 0, 0));
        add(targetPos.add(0, 0, -1));
        if(head.get())
        	add(targetPos.add(0, 2, 0));
        if(head2.get())
        	add(targetPos.add(0, 3, 0));
    }
    
    
}
