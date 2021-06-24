package minegame159.meteorclient.systems.modules.ArtikHack;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.packets.PacketEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import net.minecraft.network.packet.c2s.play.TeleportConfirmC2SPacket;

public class PortalGodMode extends Module {

    public PortalGodMode(){
        super(Categories.ArtikHack, "portal-god-mode", "Portal God Mode.");
    }


    @EventHandler
    private void POPS(PacketEvent.Send e) {
    	if(e.packet instanceof TeleportConfirmC2SPacket) e.cancel();
    }
    
    
}
