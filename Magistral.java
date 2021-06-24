package minegame159.meteorclient.systems.modules.ArtikHack;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.game.GameJoinedEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.utils.Utils;
import minegame159.meteorclient.utils.player.ChatUtils;
import minegame159.meteorclient.utils.player.InvUtils;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

public class Magistral extends Module {
	
    private enum Dir {
        XP,
        XM,
        ZP,
        ZM,
        XPZP,
        XPZM,
        XMZP,
        XMZM,
    }
	
    public enum etype {
        x3,
        x5,
        x7
    }
    
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
  
    private final Setting<etype> type = sgGeneral.add(new EnumSetting.Builder<etype>()
            .name("size")
            .description("Which positions to place on your top half.")
            .defaultValue(etype.x3)
            .onChanged(b -> setsize())
            .build());

    private final Setting<Boolean> disableOnY = sgGeneral.add(new BoolSetting.Builder()
            .name("disable-on-y-change")
            .description("Automatically disables when you move.")
            .defaultValue(true)
            .build());
    
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .description("Automatically faces towards the obsidian being placed.")
            .defaultValue(false)
            .build());
    

    public Magistral() {
        super(Categories.ArtikHack, "magistral", "magistral.");
    }
    
    BlockPos ppos = null;
	int x,y,z,posy,pX,pY,pZ;
	byte size;
    Dir dir;
    
    
    @EventHandler
    private void onGameJoin(GameJoinedEvent event) {
    	toggle();
    }

	@Override
    public void onActivate() {
		
		setsize();
		
		dir = getDir();
		
		x = mc.player.getBlockPos().getX();
		y = mc.player.getBlockPos().getY();
		z = mc.player.getBlockPos().getZ();
		
    	posy = mc.player.getBlockPos().getY();
    }
	
	private Dir getDir(){
		float yaw = Math.round((mc.player.yaw + 1) / 45) * 45;
		while(true){
			if(yaw>=360) yaw = yaw-360;
			if(yaw<0) yaw = yaw+360;
			if(yaw<360) break;
		}
		if(yaw == 45) return Dir.XMZP;
		if(yaw == 90) return Dir.XM;
		if(yaw == 135) return Dir.XMZM;
		if(yaw == 180) return Dir.ZM;
		if(yaw == 225) return Dir.XPZM;
		if(yaw == 270) return Dir.XP;
		if(yaw == 315) return Dir.XPZP;
		return Dir.ZP;
	}
	
	private void setsize(){
		
		switch(type.get()){
			case x3:
				size = 0;
				break;
			case x5:
				size = 1;
				break;
			case x7:
				size = 2;
				break;
		}
	}
	
    @EventHandler
    private void onTick(TickEvent.Pre event) {
    	
    	if (disableOnY.get() && posy != mc.player.getBlockPos().getY()) {
            toggle();
            return;
        }
    	
		pX = mc.player.getBlockPos().getX();
		pY = mc.player.getBlockPos().getY();
		pZ = mc.player.getBlockPos().getZ();
		
    	
        if(dir == Dir.XP || dir == Dir.XM){
        	for(int e = -4; e <= 4; e++){
        		int q = e;
        		if(getDir() == Dir.XM) q=e-e*2;
        		
        		int k = -1 - size;
        		int k2 = k-k-k;
        		for(; k <= k2; k++){
        			if(p(pX + q, y - 1, z + k)) return;
        		}
        		if(p(pX + q, y, z + k2 + 1)) return;
        		if(p(pX + q, y, z - k2 - 1)) return;
        	}
        }
        
        if(dir == Dir.ZP || dir == Dir.ZM){
        	for(int e = -4; e <= 4; e++){
        		int q = e;
        		if(getDir() == Dir.ZM) q=e-e*2;
        		
        		int k = -1 - size;
        		int k2 = k-k-k;
        		for(; k <= k2; k++){
        			if(p(x + k, y - 1, pZ + q)) return;
        		}
        		if(p(x + k2 + 1, y, pZ + q)) return;
        		if(p(x - k2 - 1, y, pZ + q)) return;
        	}
        }
        
        if(dir == Dir.XPZP || dir == Dir.XMZM){
        	for(int e = -4; e <= 4; e++){
        		int q = e;
        		if(getDir() == Dir.XMZM) q=e-e*2;
        		
        		int k = -2 - size;
        		int k2 = k-k-k;
        		for(; k <= k2; k++){
	        		if(p(x + (pX-x) + q + k, y - 1, z + (pX-x) + q)) return;
        		}
        		if(p(x + (pX-x) + q - k2 - 1, y, z + (pX-x) + q)) return;
        		if(p(x + (pX-x) + q + k2 + 1, y, z + (pX-x) + q)) return;
        	}
        }
        
        if(dir == Dir.XPZM || dir == Dir.XMZP){
        	for(int e = -4; e <= 4; e++){
        		int qx = e-e*2;
        		int qz = e;
        		if(getDir() == Dir.XPZM) {
        			qx = e;
        			qz = e-e*2;
        		}
        		
        		int k = -2 - size;
        		int k2 = k-k-k;
        		for(; k <= k2; k++){
	        		if(p(x - (pZ-z) + qx + k, y - 1, z + (pZ-z) + qz)) return;
        		}
        		if(p(x - (pZ-z) + qx - k2 - 1, y, z + (pZ-z) + qz)) return;
        		if(p(x - (pZ-z) + qx + k2 + 1, y, z + (pZ-z) + qz)) return;
        	}
        }
        
    }
    
    
    private boolean p(int x, int y, int z) {
    	
    	if (Utils.squaredDistance(mc.player.getX(), mc.player.getY(), mc.player.getZ(), x, y, z) > 25) return false;
    	
        int slot = InvUtils.findItemInHotbar(Items.OBSIDIAN);
        if(slot == -1){
	    	ChatUtils.moduleError(this, "В панеле быстрого доступа нет обсидиана!");
	    	toggle();
	    	return true;
        }
        return Ezz.BlockPlace(x,y,z, slot, rotate.get());
    }

}
