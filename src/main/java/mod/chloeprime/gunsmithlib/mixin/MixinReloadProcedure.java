package mod.chloeprime.gunsmithlib.mixin;

import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import mod.chloeprime.gunsmithlib.common.util.GsHooks;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class MixinReloadProcedure {
    @Shadow private ItemStack itemStack;
    @Shadow private AbstractGunItem abstractGunItem;
    @Shadow private LivingEntity shooter;

    @Inject(
            method = "consumeAmmoFromPlayer",
            at = @At("HEAD"),
            cancellable = true)
    private void postFeedEvents(int amount, CallbackInfoReturnable<Integer> cir) {
        var gun = this.itemStack;
        var kun = this.abstractGunItem;
        var shooter = this.shooter;
        if (gun == null || kun == null || shooter == null) {
            return;
        }
        try {
            GsHooks.onReloadFeed(kun, shooter, gun, !kun.hasBulletInBarrel(gun), () -> cir.setReturnValue(0));
        } catch (NoClassDefFoundError error) {
            if (FMLLoader.isProduction()) {
                throw error;
            }
        }
    }
}
