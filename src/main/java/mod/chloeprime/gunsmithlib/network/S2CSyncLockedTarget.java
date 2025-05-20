package mod.chloeprime.gunsmithlib.network;

import mod.chloeprime.gunsmithlib.client.ClientNetworkHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record S2CSyncLockedTarget(
        int bulletId,
        int targetId
) {
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(bulletId);
        buf.writeVarInt(targetId);
    }

    public static S2CSyncLockedTarget decode(FriendlyByteBuf buf) {
        var bulletId = buf.readVarInt();
        var targetId = buf.readVarInt();
        return new S2CSyncLockedTarget(bulletId, targetId);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> ClientNetworkHandler.handleSyncLockedTarget(this));
        context.get().setPacketHandled(true);
    }
}
