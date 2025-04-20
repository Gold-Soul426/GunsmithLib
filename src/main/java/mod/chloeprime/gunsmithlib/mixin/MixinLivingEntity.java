package mod.chloeprime.gunsmithlib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import mod.chloeprime.gunsmithlib.common.internal.GunAttributeSyncState;
import mod.chloeprime.gunsmithlib.common.util.FloatConsumer;
import mod.chloeprime.gunsmithlib.common.util.HurtFunction1;
import mod.chloeprime.gunsmithlib.common.util.HurtFunction2;
import mod.chloeprime.gunsmithlib.common.util.SpecialHurtable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity implements SpecialHurtable, GunAttributeSyncState {
    @Shadow public abstract void setHealth(float p_21154_);

    private @Unique boolean gunsmith$isInGunAttributeMode;
    private @Unique boolean gunsmith$isDoingSpecialHurtProcedure;

    @WrapOperation(
            method = "hurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
    private void useSpecialActuallyHurtWhenNeeded(LivingEntity entity, DamageSource source, float amount, Operation<Void> original) {
        var injected = (SpecialHurtable) entity;
        HurtFunction2 method = gunsmith$isDoingSpecialHurtProcedure
                ? injected.getSpecialHurtFunction2()
                : ((source1, amount1) -> original.call(entity, source1, amount1));
        method.invoke(source, amount);
    }

    @WrapOperation(
            method = "actuallyHurt",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setHealth(F)V"))
    private void useSpecialSetHealthWhenNeeded(LivingEntity entity, float value, Operation<Void> original) {
        var injected = (SpecialHurtable) entity;
        FloatConsumer method = gunsmith$isDoingSpecialHurtProcedure
                ? injected.getSpecialSetHealthFunction()
                : ((v) -> original.call(entity, v));
        method.accept(value);
    }

    @Override
    public boolean gunsmith$usingSpecialHurt() {
        return false;
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public HurtFunction1 getSpecialHurtFunction1() {
        return this::hurt;
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public HurtFunction2 getSpecialHurtFunction2() {
        return this::actuallyHurt;
    }

    @Override
    @SuppressWarnings("AddedMixinMembersNamePattern")
    public FloatConsumer getSpecialSetHealthFunction() {
        return this::setHealth;
    }

    @Override
    public void gunsmith$beginSpecialHurt() {
        gunsmith$isDoingSpecialHurtProcedure = true;
    }

    @Override
    public void gunsmith$endSpecialHurt() {
        gunsmith$isDoingSpecialHurtProcedure = false;
    }

    @Override
    public boolean gunsmith$isInGunMode() {
        return gunsmith$isInGunAttributeMode;
    }

    @Override
    public void gunsmith$setInGunMode(boolean value) {
        gunsmith$isInGunAttributeMode = value;
    }

    @Shadow public abstract boolean hurt(@NotNull DamageSource p_21016_, float p_21017_);
    @Shadow protected abstract void actuallyHurt(DamageSource p_21240_, float p_21241_);
}
