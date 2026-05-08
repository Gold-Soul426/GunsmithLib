package mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.ammo_variant;

import cn.chloeprime.commons.async.TaskScheduler;
import cn.chloeprime.commons.rpc.RPC;
import cn.chloeprime.commons.rpc.RPCFlow;
import cn.chloeprime.commons.rpc.RPCTarget;
import cn.chloeprime.commons.rpc.RemoteCallable;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.common.util.SimpleCodecResourceReloadListener;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@Mod.EventBusSubscriber
public class GunAmmoVariantSetLoader extends SimpleCodecResourceReloadListener<GunAmmoVariantSet> {
    public static final GunAmmoVariantSetLoader INSTANCE = new GunAmmoVariantSetLoader();

    private GunAmmoVariantSetLoader() {
        super(GunAmmoVariantSet.CODEC, GSON, GunsmithLib.MOD_ID + "/gun_ammo_variant_sets");
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void apply(Map<ResourceLocation, GunAmmoVariantSet> data, ResourceManager resourceManager, ProfilerFiller profiler) {
        receiveData(data);
    }

    @RemoteCallable(flow = RPCFlow.SERVER_TO_CLIENT)
    private static void receiveData(CompoundTag data) {
        decodeJsonFromNBT(data)
                .map(INSTANCE::decodeFromJson)
                .ifPresent(GunAmmoVariantSetLoader::receiveData);
    }

    private static void receiveData(Map<ResourceLocation, GunAmmoVariantSet> data) {
        var lock = GunVariantRegistry.SINGLEPLAYER_LOCK.writeLock();
        try {
            lock.lock();
            GunVariantRegistry.mergeByGunId(data.values());
        } finally {
            lock.unlock();
        }
    }

    @SubscribeEvent
    public static void onRegisteringReloadListeners(AddReloadListenerEvent event) {
        event.addListener(GunAmmoVariantSetLoader.INSTANCE);
    }

    private static final TaskScheduler DELAYER = TaskScheduler.createTickBased(LogicalSide.SERVER);

    @SubscribeEvent
    public static void syncRegistryDataOnDatapackSync(OnDatapackSyncEvent event) {
        encodeJsonToNBT(INSTANCE.raw).ifPresent(tag -> {
            for (ServerPlayer player : event.getPlayers()) {
                DELAYER.withCondition(player::isAlive).delay(1, task -> {
                    RPC.call(RPCTarget.to(player), GunAmmoVariantSetLoader::receiveData, tag);
                    GunVariantRegistry.injectGunDisplayInstanceRedirectingDataToClient(player);
                });
            }
        });
    }
}
