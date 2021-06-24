package minegame159.meteorclient.systems.modules.ArtikHack;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.packets.PacketEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.systems.modules.Modules;
import minegame159.meteorclient.systems.modules.combat.AutoTotem;
import minegame159.meteorclient.systems.modules.combat.OffhandExtra;
import minegame159.meteorclient.systems.modules.misc.OffhandCrash;
import minegame159.meteorclient.systems.modules.player.AutoMend;
import minegame159.meteorclient.utils.player.InvUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotem1488 extends Module {
	
    private String totemCountString = "0";


    public AutoTotem1488() {
        super(Categories.ArtikHack, "auto-totem+", "Automatically equips totems in your offhand.");
    }
    
    @Override
    public void onActivate() {
    	if(Modules.get().get(AutoMend.class).isActive()) Modules.get().get(AutoMend.class).toggle();
    	if(Modules.get().get(OffhandExtra.class).isActive()) Modules.get().get(OffhandExtra.class).toggle();
    	if(Modules.get().get(AutoTotem.class).isActive()) Modules.get().get(AutoTotem.class).toggle();
    	if(Modules.get().get(OffhandCrash.class).isActive()) Modules.get().get(OffhandCrash.class).toggle();
	}
    
    
    @EventHandler (priority = Integer.MAX_VALUE)
    private void POPS(PacketEvent.Receive e) {
        if (e.packet instanceof EntityStatusS2CPacket) {
	        EntityStatusS2CPacket p = (EntityStatusS2CPacket) e.packet;
	        if (p.getStatus() != 35) return;
	        Entity entity = p.getEntity(mc.world);
	        if (entity == null || !entity.equals(mc.player)) return;
	        
	        
	        
	        if(mc.currentScreen instanceof GenericContainerScreen) SET_CHEST_TOTEM();
	        if(mc.currentScreen instanceof ShulkerBoxScreen) SET_SHULKER_TOTEM();
	        if(mc.currentScreen instanceof CraftingScreen) SET_CRAFTINGTABLE_TOTEM();
	    	if(mc.currentScreen instanceof AnvilScreen) SET_ANVIL_TOTEM();
        }
    }
    
    

    private void SET_CHEST_TOTEM() {
    	GenericContainerScreenHandler container = ((GenericContainerScreen) mc.currentScreen).getScreenHandler();
    	if (container == null) return;
    	int slot = -1;
    	for (int i = 0; i < container.slots.size(); i++) {
    		if(container.slots.get(i).getStack().getItem() == Items.TOTEM_OF_UNDYING) {
    			slot = i;
    			break;
    		}
    	}
    	if(slot > -1) {
    		MOVE_TOTEM(slot);
    		container.slots.get(slot).setStack(new ItemStack(Items.AIR));
    	}
    }
    
    
    
    private void SET_SHULKER_TOTEM() {
    	ShulkerBoxScreenHandler container = ((ShulkerBoxScreen) mc.currentScreen).getScreenHandler();
    	if (container == null) return;
    	int slot = -1;
    	for (int i = 0; i < container.slots.size(); i++) {
    		if(container.slots.get(i).getStack().getItem() == Items.TOTEM_OF_UNDYING) {
    			slot = i;
    			break;
    		}
    	}
    	if(slot > -1) {
    		MOVE_TOTEM(slot);
    		container.slots.get(slot).setStack(new ItemStack(Items.AIR));
    	}
    }
    
    
    
    private void SET_ANVIL_TOTEM() {
    	AnvilScreenHandler container = ((AnvilScreen) mc.currentScreen).getScreenHandler();
    	if (container == null) return;
    	int slot = -1;
    	for (int i = 0; i < container.slots.size(); i++) {
    		if(container.slots.get(i).getStack().getItem() == Items.TOTEM_OF_UNDYING) {
    			slot = i;
    			break;
    		}
    	}
    	if(slot > -1) {
    		MOVE_TOTEM(slot);
    		container.slots.get(slot).setStack(new ItemStack(Items.AIR));
    	}
    }
    
    
    
    private void SET_CRAFTINGTABLE_TOTEM() {
    	CraftingScreenHandler container = ((CraftingScreen) mc.currentScreen).getScreenHandler();
    	if (container == null) return;
    	int slot = -1;
    	for (int i = 0; i < container.slots.size(); i++) {
    		if(container.slots.get(i).getStack().getItem() == Items.TOTEM_OF_UNDYING) {
    			slot = i;
    			break;
    		}
    	}
    	if(slot > -1) {
    		MOVE_TOTEM(slot);
    		container.slots.get(slot).setStack(new ItemStack(Items.AIR));
    	}
    }
    
    
    
    private void MOVE_TOTEM(int slot) {
    	mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId,slot,40,SlotActionType.SWAP,mc.player);
    }
    
    
    
    private void SET_INV_TOTEM() {
    	InvUtils.FindItemResult RESULT = InvUtils.findItemWithCount(Items.TOTEM_OF_UNDYING);
	    if(RESULT.count > 0) {
	    	int slot = Ezz.invIndexToSlotId(RESULT.slot); 
    		MOVE_TOTEM(slot);
	      }
    }
    
    
    
    @EventHandler (priority = Integer.MAX_VALUE-1)
    private void onTick(TickEvent.Pre e) {
    	Screen s = mc.currentScreen;
    	if(mc.player == null || mc.world == null) return;
    	
    	SET_TOTEM_COUNT();
    	
    	if(mc.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) return;
    	
    	if(s instanceof GenericContainerScreen) {SET_CHEST_TOTEM(); return;}
    	if(s instanceof ShulkerBoxScreen) {SET_SHULKER_TOTEM(); return;}
    	if(s instanceof CraftingScreen) {SET_CRAFTINGTABLE_TOTEM(); return;}
    	
	    SET_INV_TOTEM();
   	}
    
    

    private void SET_TOTEM_COUNT(){
        InvUtils.FindItemResult slot = InvUtils.findItemWithCount(Items.TOTEM_OF_UNDYING);
	    totemCountString = Integer.toString(slot.count);
    }
    
    

    @Override
    public String getInfoString() {
        return totemCountString;
    }

    

}
