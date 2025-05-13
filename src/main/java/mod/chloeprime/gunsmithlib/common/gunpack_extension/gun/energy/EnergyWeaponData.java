package mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.energy;

import com.google.gson.annotations.SerializedName;
import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.GunsmithLibGunDataExtension;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public record EnergyWeaponData(
        @SerializedName("energy_per_shot")
        int energyPerShot,
        @SerializedName("charge_power")
        int chargePower,
        @SerializedName("needs_reload_on_full_heat")
        boolean needsReloadOnFullHeat
) {
    public record Runtime(
            EnergyWeaponData energy,
            GunInfo gun
    ) {
    }

    public static Optional<Runtime> runtime(ItemStack stack) {
        return Gunsmith.getGunInfo(stack).flatMap(EnergyWeaponData::runtime);
    }

    public static Optional<Runtime> runtime(GunInfo gunInfo) {
        return GunsmithLibGunDataExtension.of(gunInfo)
                .map(GunsmithLibGunDataExtension::battery)
                .map(ewd -> new Runtime(ewd, gunInfo));
    }
}
