package mod.chloeprime.gunsmithlib.common.gunpack_extension.gun;

import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.shield.ShieldData;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.energy.EnergyWeaponData;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Optional;

public final class GunsmithLibGunDataExtension {
    /**
     * 电池数据，
     * 为非 null 时该武器可以充电。
     *
     * @since 3.3.0
     */
    private @Nullable EnergyWeaponData battery;

    /**
     * 是否开启过热的视听反馈。
     * 为 true 时，武器过热后会发出滋滋声并冒烟
     *
     * @since 3.3.0
     */
    private boolean enable_overheat_feedback;

    /**
     * 枪盾数据
     *
     * @since 3.4.0
     */
    private ShieldData shield;

    public static Optional<GunsmithLibGunDataExtension> of(ItemStack stack) {
        return Gunsmith.getGunInfo(stack).flatMap(GunsmithLibGunDataExtension::of);
    }

    public static Optional<GunsmithLibGunDataExtension> of(GunInfo gunInfo) {
        return ((EnhancedGunData) gunInfo.index().getGunData()).gunsmith$getGunsmithLibExtension();
    }

    @Nullable
    public EnergyWeaponData battery() {
        return battery;
    }

    public boolean enableOverheatFeedback() {
        return enable_overheat_feedback;
    }

    public ShieldData getShieldData() {
        return shield;
    }
}
