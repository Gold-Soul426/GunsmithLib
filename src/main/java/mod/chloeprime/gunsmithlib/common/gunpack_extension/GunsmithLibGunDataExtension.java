package mod.chloeprime.gunsmithlib.common.gunpack_extension;

import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.energy.EnergyWeaponData;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public record GunsmithLibGunDataExtension(
        @Nullable EnergyWeaponData battery,
        boolean enable_overheat_feedback
) {
    public static Optional<GunsmithLibGunDataExtension> of(ItemStack stack) {
        return Gunsmith.getGunInfo(stack).flatMap(GunsmithLibGunDataExtension::of);
    }

    public static Optional<GunsmithLibGunDataExtension> of(GunInfo gunInfo) {
        return ((EnhancedGunData) gunInfo.index().getGunData()).gunsmith$getGunsmithLibExtension();
    }
}
