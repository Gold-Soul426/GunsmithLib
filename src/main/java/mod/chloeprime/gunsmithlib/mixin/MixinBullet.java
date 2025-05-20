package mod.chloeprime.gunsmithlib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.tacz.guns.entity.EntityKineticBullet;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.fire_control.HomingProjectileBehavior;
import mod.chloeprime.gunsmithlib.common.util.HurtFunction1;
import mod.chloeprime.gunsmithlib.common.util.SpecialHurtable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityKineticBullet.class)
public abstract class MixinBullet extends Projectile {
    @WrapOperation(
            method = "<clinit>",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/EntityType$Builder;updateInterval(I)Lnet/minecraft/world/entity/EntityType$Builder;"))
    private static <T extends Entity> EntityType.Builder<T> adjustBulletUpdateIntervalToFixHomingBulletDrifting(
            EntityType.Builder<T> instance,
            int originalInterval,
            Operation<EntityType.Builder<T>> original
    ) {
        return original.call(instance, Math.min(originalInterval, 2));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickHoming(CallbackInfo ci) {
        if (!isRemoved()) {
            HomingProjectileBehavior.onBulletTick(this);
        }
    }

    @WrapOperation(
            method = "tacAttackEntity", remap = false,
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean useSpecialHurtByTag(Entity victim, DamageSource source, float amount, Operation<Boolean> original) {
        if (!(victim instanceof SpecialHurtable injected)){
            return original.call(victim, source, amount);
        }
        HurtFunction1 method = injected.gunsmith$usingSpecialHurt()
                ? injected.getSpecialHurtFunction1()
                : ((source1, amount1) -> original.call(victim, source1, amount1));
        return method.invoke(source, amount);
    }

    public MixinBullet(EntityType<? extends Projectile> type, Level level) {
        super(type, level);
    }
}
