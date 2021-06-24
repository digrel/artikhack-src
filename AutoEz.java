package minegame159.meteorclient.systems.modules.ArtikHack;

import java.util.ArrayList;
import java.util.List;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.entity.player.AttackEntityEvent;
import minegame159.meteorclient.events.world.TickEvent;
import minegame159.meteorclient.settings.BoolSetting;
import minegame159.meteorclient.settings.IntSetting;
import minegame159.meteorclient.settings.Setting;
import minegame159.meteorclient.settings.SettingGroup;
import minegame159.meteorclient.settings.StringSetting;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import minegame159.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Pair;
import minegame159.meteorclient.systems.modules.combat.TotemPopNotifier;
import minegame159.meteorclient.utils.player.ChatUtils;

public class AutoEz extends Module {


    public AutoEz() {
    	super(Categories.ArtikHack, "auto-ez", "Send a chat message after killing a player.");
    	}
    
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
	
    private final Setting<String> format = sgGeneral.add(new StringSetting.Builder()
		    .name("message")
		    .description("Send a chat message about killing a player.")
		    .defaultValue("EZ! %name%! Just died after popping (highlight)%d totem! He were killed by ArtikHack!")
		    .build());
    
    private final Setting<Integer> minArmor = sgGeneral
    		.add(new IntSetting.Builder()
    		.name("min-armor")
    		.description("Minimum number of armor elements.")
    		.defaultValue(2)
    		.min(0)
    		.max(4)
    		.sliderMin(0)
    		.sliderMax(4)
    		.build());

	private final Setting<Boolean> ignoreFriends = sgGeneral.add(new BoolSetting.Builder()
			.name("ignore-friends")
			.defaultValue(true)
			.build());
	
	@Override
	public void onActivate() {
		players.clear();
    	msgplayers.clear();
	}
	
    
    ArrayList<Pair<Integer, Long>> players = new ArrayList<Pair<Integer, Long>>();
    ArrayList<String> msgplayers = new ArrayList<String>();
    
    private boolean checkArmor(PlayerEntity p){
    	
    	int armor = 0;

    	if(p.getEquippedStack(EquipmentSlot.HEAD).getItem() != Items.AIR) armor++;
    	if(p.getEquippedStack(EquipmentSlot.CHEST).getItem() != Items.AIR) armor++;
    	if(p.getEquippedStack(EquipmentSlot.LEGS).getItem() != Items.AIR) armor++;
    	if(p.getEquippedStack(EquipmentSlot.FEET).getItem() != Items.AIR) armor++;

    	if(armor < minArmor.get()) {
    		return true;
    	} else {
    		return false;
    		}
    }
    
    
    
    private boolean checkFriend(PlayerEntity p){
    	return (ignoreFriends.get() && Ezz.isFriend(p));
    }
    
    @EventHandler
    private void AttackEntity(AttackEntityEvent e){
    	
    	if(e.entity instanceof EndCrystalEntity){
    		List<AbstractClientPlayerEntity> worldplayers = mc.world.getPlayers();
    		
    		for(int x = 0; x < worldplayers.size(); x++){
	    		PlayerEntity p = (PlayerEntity) worldplayers.get(x);
	    		if(!p.isSpectator() && !p.isCreative() && !p.isInvulnerable() && !mc.player.equals(p) && !checkArmor(p)	&& !checkFriend(p) && p.distanceTo(e.entity) < 12){
	    			
	    			Pair<Integer, Long> pair = new Pair<Integer, Long>(p.getEntityId(), System.currentTimeMillis());
	    			int index = -1;
	    			for(int w = 0; w < players.size(); w++){
	    				if(players.get(w).getLeft().equals(p.getEntityId())){
	    					index = w;
	    					break;
	    				}
	    			}
	    			if(index == -1){
	    				players.add(pair);
	    			} else {
	    				players.set(index, pair);
	    			}
	    			
	    		}
    		}
    		
    	}
    	
    	if(e.entity instanceof PlayerEntity){
    		PlayerEntity p = (PlayerEntity) e.entity;
    		if(!p.isSpectator() && !p.isCreative() && !p.isInvulnerable() && !mc.player.equals(p) && !checkArmor(p) && !checkFriend(p)){
    			
    			Pair<Integer, Long> pair = new Pair<Integer, Long>(p.getEntityId(), System.currentTimeMillis());
    			int index = -1;
    			for(int w = 0; w < players.size(); w++){
    				if(players.get(w).getLeft().equals(p.getEntityId())){
    					index = w;
    					break;
    				}
    			}
    			if(index == -1){
    				players.add(pair);
    			} else {
    				players.set(index, pair);
    			}
    			
    		}
    	}
    }
    
    
	@EventHandler
    private void onTick(TickEvent.Pre e) {
		
		
		if(players.size() == 0) return;
		
    	ArrayList<Pair<Integer, Long>> rem = players;
		
		for(int x = 0; x < players.size(); x++){
			Pair<Integer, Long> w = players.get(x);
			int id = (int) w.getLeft();
			long time = (long) w.getRight();
			
			PlayerEntity p = (PlayerEntity) mc.world.getEntityById(id);
			
			if(System.currentTimeMillis() - time > 2000 || p == null) {
				rem.remove(x);
				continue;
			}
			
			if(p.isDead()){
				if(!msgplayers.contains(p.getName().asString())) msgplayers.add(p.getName().asString());
				rem.remove(x);
				resend();
			}
		}
		
		players = rem;
		
	}
	
	private void resend(){
		MeteorExecutor.execute(() -> send());
	}
	
	private void send(){
		int size = msgplayers.size();
		try {Thread.sleep(500);} catch (Exception e) {}
		if(size != msgplayers.size()){
			resend();
			return;
		}
		
		if(msgplayers.size() == 0) return;
		mc.player.sendChatMessage(format.get().replace("%name%", String.join(", ", msgplayers)));
		msgplayers.clear();
	}
	
	
}
