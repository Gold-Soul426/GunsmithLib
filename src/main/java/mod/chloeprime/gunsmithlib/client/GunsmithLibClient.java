package mod.chloeprime.gunsmithlib.client;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.client.model.papi.PapiManager;
import com.tacz.guns.client.sound.SoundPlayManager;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.client.papi.RangefinderPapi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GunsmithLibClient {
    public static void initClient() {
        PapiManager.addPapi(RangefinderPapi.NAME, RangefinderPapi.INSTANCE);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(GunsmithLibClient::initClient);
    }

    @SubscribeEvent
    public static void registerEntityRenderersExcludingLaser(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(GunsmithLib.EntityTypes.RANGEFINDER_MARKER.get(), NoopRenderer::new);
        event.registerEntityRenderer(GunsmithLib.EntityTypes.AREA_EFFECT_CLOUD_3D.get(), NoopRenderer::new);
    }

    public static void playFireSelectSound() {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        playFireSelectSound(player.getMainHandItem());
    }

    public static void playFireSelectSound(ItemStack gun) {
        var player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        TimelessAPI
                .getGunDisplay(gun)
                .ifPresent(display -> SoundPlayManager.playFireSelectSound(player, display));
    }
}
