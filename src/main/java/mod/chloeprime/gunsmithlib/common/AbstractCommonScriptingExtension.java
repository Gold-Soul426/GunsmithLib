package mod.chloeprime.gunsmithlib.common;

import com.tacz.guns.api.GunProperties;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.item.IGun;
import mod.chloeprime.gunsmithlib.api.common.CommonScriptingExtension;
import mod.chloeprime.gunsmithlib.api.util.Rangefinder;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

@ApiStatus.Internal
public interface AbstractCommonScriptingExtension extends CommonScriptingExtension {
    ItemStack gunsmithlib$getCurrentItem();
    IGun gunsmithlib$getGunItemInterface();
    Optional<LivingEntity> gunsmithlib$getShooter();

    @Override
    @SuppressWarnings("UnstableApiUsage")
    default double getEstimatedRange() {
        int pierce = gunsmithlib$getShooter()
                .map(IGunOperator::fromLivingEntity)
                .map(IGunOperator::getCacheProperty)
                .map(cache -> cache.getCache(GunProperties.PIERCE))
                .orElse(0);
        return getEstimatedRange(pierce);
    }

    @Override
    default double getEstimatedRange(int pierce) {
        Entity shooter = gunsmithlib$getShooter().orElse(null);
        if (shooter == null) {
            return 0;
        }

        ItemStack gunStack = gunsmithlib$getCurrentItem();
        IGun gunItem = gunsmithlib$getGunItemInterface();
        if (gunStack == null || gunItem == null) {
            return 0;
        }

        double range = getEstimatedMaxRange(shooter, gunStack, gunItem);
        return Rangefinder.clip(shooter, shooter.getEyePosition(), shooter.getLookAngle(), pierce, range).getLength();
    }

    @SuppressWarnings("UnstableApiUsage")
    static double getEstimatedMaxRange(@Nullable Entity entity, @Nonnull ItemStack gunStack, @Nonnull IGun gunItem) {
        if (!(entity instanceof LivingEntity shooter)) {
            return 0;
        }
        var gi = GsHelper.unpack(gunItem, gunStack).orElse(null);
        if (gi == null) {
            return 0;
        }
        return Optional.ofNullable(IGunOperator.fromLivingEntity(shooter).getCacheProperty())
                .map(cache -> {
                    float speed = cache.getCache(GunProperties.AMMO_SPEED);
                    float life = gi.index().getBulletData().getLifeSecond();
                    return speed * life;
                })
                .orElse(0F);
    }
}
