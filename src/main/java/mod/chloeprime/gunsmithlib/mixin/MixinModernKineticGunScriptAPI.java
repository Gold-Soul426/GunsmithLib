package mod.chloeprime.gunsmithlib.mixin;

import com.tacz.guns.api.item.gun.AbstractGunItem;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import mod.chloeprime.gunsmithlib.api.common.GunScriptAPIExtension;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.OverheatFeedback;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModernKineticGunScriptAPI.class, remap = false)
public class MixinModernKineticGunScriptAPI implements GunScriptAPIExtension {
    @Override
    public void gunsmith_playOverheatSound() {
        if (shooter != null) {
            OverheatFeedback.playCooldownSound(shooter);
        }
    }

    @Inject(method = "handleShootHeat", at = @At(value = "INVOKE", target = "Lcom/tacz/guns/api/item/gun/AbstractGunItem;setOverheatLocked(Lnet/minecraft/world/item/ItemStack;Z)V"))
    private void sfxFeedback(CallbackInfo ci) {
        if (!abstractGunItem.isOverheatLocked(itemStack)) {
            OverheatFeedback.tryPlayCooldownSound(shooter, itemStack);
        }
    }

    @Inject(method = "setOverheatLocked", at = @At("HEAD"))
    private void sfxFeedback(boolean locked, CallbackInfo ci) {
        if (shooter == null) {
            return;
        }
        if (!abstractGunItem.isOverheatLocked(itemStack) && locked) {
            OverheatFeedback.tryPlayCooldownSound(shooter, itemStack);
        }
    }

    @Shadow private AbstractGunItem abstractGunItem;
    @Shadow private ItemStack itemStack;
    @Shadow private LivingEntity shooter;
}
