package minegame159.meteorclient.systems.modules.ArtikHack;

import java.math.BigDecimal;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.EnumSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.utils.player.InvUtils;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ExtraSurround extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public enum ecenter {
        fast,
        legit,
        disable
    }
  
	private final Setting<ecenter> center = sgGeneral.add(new EnumSetting.Builder<ecenter>()
		.name("center")
		.description("Teleport to center block.")
		.defaultValue(ecenter.legit)
		.build()
	);
	
	private final Setting<Boolean> doubleHeight = sgGeneral.add(new BoolSetting.Builder()
		.name("double-height")
		.description("Places obsidian on top of the original surround blocks to prevent people from face-placing you.")
		.defaultValue(false)
		.build()
	);
	
	private final Setting<Boolean> wideDown = sgGeneral.add(new BoolSetting.Builder()
		.name("wide-down")
		.description("Big Down")
		.defaultValue(false)
		.build()
	);
	
	private final Setting<Boolean> upDown = sgGeneral.add(new BoolSetting.Builder()
		.name("up-down")
		.description("Up Down")
		.defaultValue(false)
		.build()
	);
	
	private final Setting<Boolean> helpUP = sgGeneral.add(new BoolSetting.Builder()
		.name("help-up")
		.description("Help head block place. Only working if Up Down is enabled.")
		.defaultValue(false)
		.build()
	);
	
	private final Setting<Boolean> head1 = sgGeneral.add(new BoolSetting.Builder()
		.name("head-+1")
		.description("2 blocks per head.")
		.defaultValue(false)
		.build()
	);
	
	
	private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder()
		.name("only-on-ground")
		.description("Works only when you standing on blocks.")
		.defaultValue(true)
		.build()
	);
	
	private final Setting<Boolean> disableOnJump = sgGeneral.add(new BoolSetting.Builder()
		.name("disable-on-jump")
		.description("Automatically disables when you jump.")
		.defaultValue(true)
		.build()
	);
	
	private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
		.name("rotate")
		.description("Automatically faces towards the obsidian being placed.")
		.defaultValue(false)
		.build());

	public ExtraSurround() {
		super(Categories.ArtikHack, "extra-surround", "Surrounds you in blocks to prevent you from taking lots of damage.");
	}
	

	BlockPos pos = null;
    
    @Override
    public void onActivate() {
    	
        if(center.get() == ecenter.fast){
	    	double tx=0,tz=0;
	
	    	Vec3d p = mc.player.getPos(); 
	    	
		   	 if (p.x>0 && gp(p.x)<3) tx=0.3;
			 if (p.x>0 && gp(p.x)>6) tx=-0.3;
			 if (p.x<0 && gp(p.x)<3) tx=-0.3;
			 if (p.x<0 && gp(p.x)>6) tx=0.3;
		
			 if (p.z>0 && gp(p.z)<3) tz=0.3;
			 if (p.z>0 && gp(p.z)>6) tz=-0.3;
			 if (p.z<0 && gp(p.z)<3) tz=-0.3;
			 if (p.z<0 && gp(p.z)>6) tz=0.3;
		
			 if(tx!=0 || tz!=0){
		    	 double posx = mc.player.getX() + tx;
		         double posz = mc.player.getZ() + tz;
		         mc.player.updatePosition(posx, mc.player.getY(), posz);
		         mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
		    }
        }
    	 
    }


    private long gp(double v) {
    	   BigDecimal v1 = BigDecimal.valueOf(v);
	       BigDecimal v2 = v1.remainder(BigDecimal.ONE);
	       return Byte.valueOf(String.valueOf(String.valueOf(v2).replace("0.", "").replace("-", "").charAt(0)));
    }
    
    @EventHandler
    private void onTick(TickEvent.Pre event) {

        if(center.get() == ecenter.legit){
        	
	    	double tx=0,tz=0;
	    	Vec3d p = mc.player.getPos(); 
		   	 if (p.x>0 && gp(p.x)<3) tx=0.185;
			 if (p.x>0 && gp(p.x)>6) tx=-0.185;
			 if (p.x<0 && gp(p.x)<3) tx=-0.185;
			 if (p.x<0 && gp(p.x)>6) tx=0.185;
		
			 if (p.z>0 && gp(p.z)<3) tz=0.185;
			 if (p.z>0 && gp(p.z)>6) tz=-0.185;
			 if (p.z<0 && gp(p.z)<3) tz=-0.185;
			 if (p.z<0 && gp(p.z)>6) tz=0.185;	

		
			 if(tx!=0 || tz!=0){
		    	 double posx = mc.player.getX() + tx;
		         double posz = mc.player.getZ() + tz;
		         mc.player.updatePosition(posx, mc.player.getY(), posz);
		         mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionOnly(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround()));
		         return;
		    }
        }
    	
    	
        if (disableOnJump.get() && mc.options.keyJump.isPressed()) {
            toggle();
            return;
        }

        if (onlyOnGround.get() && !mc.player.isOnGround()) return;


        if(p(0, -1, 0)) return;

        if(p(1, 0, 0)) return;
        if(p(-1, 0, 0)) return;
        if(p(0, 0, 1)) return;
        if(p(0, 0, -1)) return;

        if(p(1, -1, 0)) return;
        if(p(-1, -1, 0)) return;
        if(p(0, -1, 1)) return;
        if(p(0, -1, -1)) return;
        
        
		// Big Down
        if (wideDown.get()) {
            if(p(1, 0, 1)) return;
            if(p(-1, 0, -1)) return;
            if(p(-1, 0, 1)) return;
            if(p(1, 0, -1)) return;
            if(p(2, 0, 0)) return;
            if(p(-2, 0, 0)) return;
            if(p(0, 0, 2)) return;
            if(p(0, 0, -2)) return;

        }
        

        // Sides up
        boolean doubleHeightPlaced = false;
        if (doubleHeight.get()) {
            if(p(1, 1, 0)) return;
            if(p(-1, 1, 0)) return;
            if(p(0, 1, 1)) return;
            if(p(0, 1, -1)) return;

        }
        
        
        //fix up
        if(gp(mc.player.getPos().y)>1){
            if(p(1, 2, 0)) return;
            if(p(0, 2, 1)) return;
            if(p(-1, 2, 0)) return;
            if(p(0, 2, -1)) return;
            if(p(0, 3, 0)) return;
        }
        
        
        //help up air place
        boolean helpUPPlaced = false;
        if (helpUP.get() && doubleHeightPlaced) {
        	if(p(1, 2, 0)){
        		helpUPPlaced = true;
        		return;
        	};
        }
        
        // Up Down
        if (upDown.get()) {
        	if( (helpUP.get() && helpUPPlaced) || !helpUP.get() ) {
        		if(p(0, 2, 0)) return;
        	}
            if(p(0, -2, 0)) return;      
        }
        
		//head +1
        if(head1.get() && p(0, 3, 0)) return;


    };
    
    
    
    private boolean p(int x, int y, int z) {
    	return Ezz.BlockPlace(Ezz.SetRelative(x, y, z), InvUtils.findItemInHotbar(Items.OBSIDIAN), rotate.get());
    }


}
