package mod.chloeprime.gunsmithlib.mixin;

import com.tacz.guns.entity.shooter.ShooterDataHolder;
import com.tacz.guns.item.ModernKineticGunItem;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.energy.EnergyWeaponData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ModernKineticGunItem.class, remap = false)
public class MixinModernKineticGunItem {
    @Inject(method = "tickHeat", at = @At("HEAD"))
    private void configuredEnergyWeaponLockHeatForeverUntilReload(
            ShooterDataHolder dataHolder,
            ItemStack gunItem,
            LivingEntity shooter,
            CallbackInfo ci
    ) {
        EnergyWeaponData.runtime(gunItem).ifPresent(data -> {
            if (data.energy().needsReloadOnFullHeat()) {
                if (data.gun().gunItem().isOverheatLocked(data.gun().gunStack())) {
                    dataHolder.heatTimestamp = Long.MAX_VALUE;
                }
            }
        });
    }
}
