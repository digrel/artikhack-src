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
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;

public class ButtonTrap extends Module {



    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    // General



    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("range")
            .description("The radius players can be in to be targeted.")
            .defaultValue(5)
            .sliderMin(0)
            .sliderMax(10)
            .build()
    );

    private final Setting<Integer> delaySetting = sgGeneral.add(new IntSetting.Builder()
            .name("place-delay")
            .description("How many ticks between block placements.")
            .defaultValue(1)
            .sliderMin(0)
            .sliderMax(10)
            .build()
    );


    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Sends rotation packets to the server when placing.")
            .defaultValue(false)
            .build()
    );

    // Render

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

    private PlayerEntity target;
    private List<BlockPos> placePositions = new ArrayList<>();
    private int delay;

    public ButtonTrap(){
        super(Categories.ArtikHack, "button-trap", "Anti Surround.");
    }

    @Override
    public void onActivate() {
        target = null;
        if (!placePositions.isEmpty()) placePositions.clear();
        delay = 0;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
    	
      target = CityUtils.getPlayerTarget(range.get());
        
        
        if (target == null || mc.player.distanceTo(target) > range.get()) {
            return;
        }
    	
    	

    	int slot = -1;
    	
	        slot = InvUtils.findItemInHotbar(Blocks.ACACIA_BUTTON.asItem());
	
	        if (slot == -1) {
	        	slot = InvUtils.findItemInHotbar(Blocks.OAK_BUTTON.asItem());
	        }
	        if (slot == -1) {
	        	slot = InvUtils.findItemInHotbar(Blocks.SPRUCE_BUTTON.asItem());
	        }
	        if (slot == -1) {
	        	slot = InvUtils.findItemInHotbar(Blocks.BIRCH_BUTTON.asItem());
	        }
	        if (slot == -1) {
	        	slot = InvUtils.findItemInHotbar(Blocks.JUNGLE_BUTTON.asItem());
	        }
	        if (slot == -1) {
	        	slot = InvUtils.findItemInHotbar(Blocks.DARK_OAK_BUTTON.asItem());
	        }
	        if (slot == -1) {
	        	slot = InvUtils.findItemInHotbar(Blocks.CRIMSON_BUTTON.asItem());
	        }
	        if (slot == -1) {
	        	slot = InvUtils.findItemInHotbar(Blocks.WARPED_BUTTON.asItem());
	        }
        
        if (slot == -1) return;

        placePositions.clear();
        
        
        

        findPlacePos(target);

        if (delay >= delaySetting.get() && placePositions.size() > 0) {
            BlockPos blockPos = placePositions.get(placePositions.size() - 1);

            if (BlockUtils.place(blockPos, Hand.MAIN_HAND, slot, rotate.get(), 50,true))
                	placePositions.remove(blockPos);

            delay = 0;
        } else delay++;
    }

    @EventHandler
    private void onRender(RenderEvent event) {
        if (!render.get() || placePositions.isEmpty()) return;
        for (BlockPos pos : placePositions) Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }




    private void add(BlockPos blockPos) {
        if (!placePositions.contains(blockPos)
        	&& mc.world.getBlockState(blockPos).getMaterial().isReplaceable()
        	&& mc.world.canPlace(Blocks.ACACIA_BUTTON.getDefaultState(), blockPos, ShapeContext.absent())
        	&& (   mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY()+1, blockPos.getZ())).isFullCube(mc.world, new BlockPos(blockPos.getX(), blockPos.getY()+1, blockPos.getZ()))
	        	|| mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY()-1, blockPos.getZ())).isFullCube(mc.world, new BlockPos(blockPos.getX(), blockPos.getY()-1, blockPos.getZ()))
	        	|| mc.world.getBlockState(new BlockPos(blockPos.getX()+1, blockPos.getY(), blockPos.getZ())).isFullCube(mc.world, new BlockPos(blockPos.getX()+1, blockPos.getY(), blockPos.getZ()))
	        	|| mc.world.getBlockState(new BlockPos(blockPos.getX()-1, blockPos.getY(), blockPos.getZ())).isFullCube(mc.world, new BlockPos(blockPos.getX()-1, blockPos.getY(), blockPos.getZ()))
	        	|| mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()+1)).isFullCube(mc.world, new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()+1))
	        	|| mc.world.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()-1)).isFullCube(mc.world, new BlockPos(blockPos.getX(), blockPos.getY(), blockPos.getZ()-1))
	        	)
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
        
    }
    
    

}
