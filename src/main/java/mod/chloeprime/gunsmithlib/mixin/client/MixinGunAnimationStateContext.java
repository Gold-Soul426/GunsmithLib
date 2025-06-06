package mod.chloeprime.gunsmithlib.mixin.client;

import com.tacz.guns.client.animation.statemachine.GunAnimationStateContext;
import mod.chloeprime.gunsmithlib.api.client.GunAnimationStateContextExtension;
import mod.chloeprime.gunsmithlib.api.common.VanillaCooldownAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = GunAnimationStateContext.class, remap = false)
public class MixinGunAnimationStateContext implements GunAnimationStateContextExtension {
    @Shadow private ItemStack currentGunItem;

    @Override
    public float gunsmith_getCooldownSeconds() {
        if (currentGunItem == null || currentGunItem.isEmpty()) {
            return 0;
        }
        var MC = Minecraft.getInstance();
        var player = MC.player;
        if (player == null) {
            return 0;
        }
        var gunItem = currentGunItem.getItem();
        var cooldowns = player.getCooldowns();
        return cooldowns.getCooldownPercent(gunItem, MC.getPartialTick())
                * VanillaCooldownAPI.gunsmithlib$getCooldownDuration(cooldowns, gunItem)
                / 20;
    }

    @Override
    public float gunsmith_getCooldownPercent() {
        if (currentGunItem == null || currentGunItem.isEmpty()) {
            return 0;
        }
        var MC = Minecraft.getInstance();
        var player = MC.player;
        if (player == null) {
            return 0;
        }
        return player.getCooldowns().getCooldownPercent(currentGunItem.getItem(), MC.getPartialTick());
    }
}
