package minegame159.meteorclient.systems.modules.ArtikHack;

import java.util.Arrays;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.player.StartBreakingBlockEvent;
import minegame159.meteorclient.events.packets.PacketEvent;
import minegame159.meteorclient.events.render.RenderEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.rendering.Renderer;
import minegame159.meteorclient.rendering.ShapeMode;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.ColorSetting;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.IntSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.utils.player.InvUtils;
import minegame159.meteorclient.utils.player.Rotations;
import minegame159.meteorclient.utils.render.color.SettingColor;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class FastBreak extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    
    private final Setting<Boolean> autocity = sgGeneral.add(new BoolSetting.Builder()
	    .name("auto-city-break")
	    .defaultValue(true)
	    .build());
    
    private final Setting<Boolean> smash = sgGeneral.add(new BoolSetting.Builder()
	    .name("smash")
	    .description("Destroy the block instantly.")
	    .defaultValue(true)
	    .build());
    
    private final Setting<Boolean> obbyonly = sgGeneral.add(new BoolSetting.Builder()
	    .name("only-obsidian")
	    .description("Break obsidian only.")
	    .defaultValue(false)
	    .build());
    
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("Renders a block overlay where the obsidian will be placed.")
            .defaultValue(true)
            .build());

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .build());

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The color of the sides of the blocks being rendered.")
            .defaultValue(new SettingColor(152, 251, 152, 10))
            .build());
    
    private final Setting<Boolean> crystal = sgGeneral.add(new BoolSetting.Builder()
            .name("crystal")
            .description("Places an end crystal above the block getting mined.")
            .defaultValue(false)
            .build());

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The color of the lines of the blocks being rendered.")
            .defaultValue(new SettingColor(152, 251, 152, 255))
            .build());

    public FastBreak() {
        super(Categories.ArtikHack, "fast-break", "Fast block Break.");
    }

    BlockPos pos = null;
    boolean isBreak = false;
    
    @Override
    public void onActivate() {
    	pos = null;
    }
    	
    @EventHandler
    private void onRender(RenderEvent event) {
        if (!render.get() || pos == null) return;
        Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, pos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }

    @EventHandler
    private void AUTO_CITY(PacketEvent.Send e) {
        if (e.packet instanceof PlayerActionC2SPacket && autocity.get()) {
        	PlayerActionC2SPacket p = (PlayerActionC2SPacket) e.packet;
        	if(p.getAction().toString()=="START_DESTROY_BLOCK") pos = p.getPos();
        }
	}
    
    public class InstaMine extends Module {

        private final SettingGroup sgGeneral = settings.getDefaultGroup();
        private final SettingGroup sgRender = settings.createGroup("Render");

        private final Setting<Integer> tickDelay = sgGeneral.add(new IntSetting.Builder()
                .name("delay")
                .description("The delay between breaks.")
                .defaultValue(0)
                .min(0)
                .sliderMax(20)
                .build()
        );

        private final Setting<Boolean> pick = sgGeneral.add(new BoolSetting.Builder()
                .name("only-pick")
                .description("Only tries to mine the block if you are holding a pickaxe.")
                .defaultValue(true)
                .build()
        );

        private final Setting<Boolean> crystal = sgGeneral.add(new BoolSetting.Builder()
                .name("crystal")
                .description("Places an end crystal above the block getting mined.")
                .defaultValue(false)
                .build()
        );

        private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
                .name("rotate")
                .description("Faces the blocks being mined server side.")
                .defaultValue(true)
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

        private int ticks;
        private boolean shouldMine;

        private final BlockPos.Mutable blockPos = new BlockPos.Mutable(0, -1, 0);
        private Direction direction;

        public InstaMine() {
            super(Categories.World, "insta-mine", "Attempts to instantly mine blocks.");
        }

        @Override
        public void onActivate() {
            ticks = 0;
            blockPos.set(0, -1, 0);
            shouldMine = false;
        }

        @EventHandler
        private void onStartBreakingBlock(StartBreakingBlockEvent event) {
            direction = event.direction;
            blockPos.set(event.blockPos);
            shouldMine = mc.world.getBlockState(event.blockPos).getHardness(mc.world, event.blockPos) >= 0;
        }

        @EventHandler
        private void onTick(TickEvent.Pre event) {
            if (ticks >= tickDelay.get()) {
                ticks = 0;

                if (shouldMine() && shouldMine && !mc.world.getBlockState(blockPos).isAir()) {
                    if (crystal.get() && (mc.world.getBlockState(blockPos).getBlock().is(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).getBlock().is(Blocks.BEDROCK))) {
                        Hand crystalHand = InvUtils.getHand(Items.END_CRYSTAL);
                        int crystalSlot = InvUtils.findItemInHotbar(Items.END_CRYSTAL);
                        int prevSlot = mc.player.inventory.selectedSlot;

                        if (crystalHand != Hand.OFF_HAND && crystalSlot != -1) mc.player.inventory.selectedSlot = crystalSlot;
                        mc.interactionManager.interactBlock(mc.player, mc.world, crystalHand, new BlockHitResult(mc.player.getPos(), direction, blockPos, false));
                        if (crystalHand != Hand.OFF_HAND) mc.player.inventory.selectedSlot = prevSlot;
                    }

                    if (rotate.get()) {
                        Rotations.rotate(Rotations.getYaw(blockPos), Rotations.getPitch(blockPos), () -> mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction)));
                    } else {
                        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, blockPos, direction));
                    }
                    mc.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));

                }
            } else ticks++;
        }

        @EventHandler
        private void onRender(RenderEvent event) {
            if (!render.get() || !shouldMine() || !shouldMine || blockPos.getY() == -1) return;
            Renderer.boxWithLines(Renderer.NORMAL, Renderer.LINES, blockPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
        }

        private boolean shouldMine() {
            if (!pick.get()) return true;
            else return pick.get() && (mc.player.inventory.getMainHandStack().getItem() instanceof PickaxeItem && (mc.player.getMainHandStack().getItem() == Items.DIAMOND_PICKAXE || mc.player.getMainHandStack().getItem() == Items.NETHERITE_PICKAXE));
        }
    }
    
    
    @EventHandler
    public void StartBreakingBlockEvent(StartBreakingBlockEvent e) {
    	
    	pos = e.blockPos;

    	Block b = mc.world.getBlockState(pos).getBlock();
    	
    	if(obbyonly.get() && b != Blocks.OBSIDIAN) {
    		pos = null;
    		return;
    	}
    		
    	if(
    			b==Blocks.BEDROCK
    			|| b==Blocks.NETHER_PORTAL
    			|| b==Blocks.END_GATEWAY
    			|| b==Blocks.END_PORTAL
    			|| b==Blocks.END_PORTAL_FRAME
    			|| b==Blocks.BARRIER
    			) {
    		pos = null;
    		return;
    		};
    	
    	Block[] block_pickaxe = {
    			Blocks.STONE,
    			Blocks.COBBLESTONE,
    			Blocks.NETHERRACK,
    			Blocks.TERRACOTTA,
    			Blocks.BASALT,
    			Blocks.FURNACE,
    			Blocks.IRON_BLOCK,
    			Blocks.GOLD_BLOCK,
    			Blocks.BONE_BLOCK
    			};
    	
    	String[] block_axe = {
    			"acacia_", "oak_", "crimson_", "birch_", "warped_", "jungle_", "spruce_", "crafting_table"
    	};
    	
    	String[] block_pickaxe2 = {
    			"stone_",
    			"andesite",
    			"diorite",
    			"granite",
    			"cobblestone_",
    			"mossy_",
    			"_terracotta",
    			"basalt",
    			"blackstone",
    			"end_",
    			"purpur_",
    			"shulker_box"
    	};
    	
    	
    	boolean insta = false;
		   
		if (smash.get() && mc.player.isOnGround()){
			
			if(mc.player.getMainHandStack().getItem() == Items.NETHERITE_PICKAXE){
				
				if(Arrays.asList(block_pickaxe).contains(b)) insta = true;
				
				for(int x=0; x < block_pickaxe2.length; x++){
					if(b.asItem().toString().contains(block_pickaxe2[x])){
						insta = true;
						break;
						}
					}
			}
			
			if(mc.player.getMainHandStack().getItem() == Items.NETHERITE_AXE){
				for(int x=0; x < block_axe.length; x++){
					if(b.asItem().toString().contains(block_axe[x])){
						insta = true;
						break;
						}
					}
			}
			
			if(b.asItem().toString().contains("_leaves")) insta = false;
			if(b.asItem().toString().contains("_wart")) insta = false;
			
			if(insta) mc.world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}

    	
    }
    


    @EventHandler
    private void onTick(TickEvent.Pre event) {
    	
    	if(pos==null) return;
    	
    	mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.UP));

    		
    }

}
