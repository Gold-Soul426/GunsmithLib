package mod.chloeprime.gunsmithlib.client.eventhandler;

import com.tacz.guns.api.client.gameplay.IClientPlayerGunOperator;
import com.tacz.guns.client.input.AimKey;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.tacz.guns.util.InputExtraCheck.isInGame;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class CantAimWhenGunItemIsInCooldown {
    private static final Minecraft MC = Minecraft.getInstance();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.ClientTickEvent event) {
        tryCancelAiming();
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onAimPress(InputEvent.MouseButton.Post event) {
        if (isInGame() && AimKey.AIM_KEY.matchesMouse(event.getButton())) {
            tryCancelAiming();
        }
    }

    private static void tryCancelAiming() {
        var player = MC.player;
        if (player == null || player.isSpectator() || !(player instanceof IClientPlayerGunOperator operator)) {
            return;
        }
        if (operator.isAim()) {
            if (player.getCooldowns().isOnCooldown(player.getMainHandItem().getItem())) {
                operator.aim(false);
            }
        }
    }
}
