package minegame159.meteorclient.systems.modules.ArtikHack;

import meteordevelopment.orbit.EventHandler;
import minegame159.meteorclient.events.packets.PacketEvent;
import minegame159.meteorclient.systems.modules.Categories;
import minegame159.meteorclient.systems.modules.Module;
import net.minecraft.block.BedBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.math.BlockPos;

public class AntiSetHome extends Module {

    public AntiSetHome() {
        super(Categories.ArtikHack, "anti-set-home", "Prevent the player from losing the respawn point.");
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (mc.world == null) return;
        if(!(event.packet instanceof PlayerInteractBlockC2SPacket)) return;

        BlockPos blockPos = ((PlayerInteractBlockC2SPacket) event.packet).getBlockHitResult().getBlockPos();
        boolean IsOverWorld = mc.world.getDimension().isBedWorking();
        boolean IsNetherWorld = mc.world.getDimension().isRespawnAnchorWorking();
        boolean BlockIsBed = mc.world.getBlockState(blockPos).getBlock() instanceof BedBlock;
        boolean BlockIsAnchor = mc.world.getBlockState(blockPos).getBlock() instanceof RespawnAnchorBlock;

        if((BlockIsBed && IsOverWorld)||(BlockIsAnchor && IsNetherWorld)) event.cancel();

    };
}