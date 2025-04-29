package mod.chloeprime.gunsmithlib.common;

import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.EntityHurtByGunEvent;
import com.tacz.guns.api.event.common.GunDamageSourcePart;
import com.tacz.guns.resource.modifier.custom.AmmoSpeedModifier;
import com.tacz.guns.resource.modifier.custom.RpmModifier;
import com.tacz.guns.util.AttachmentDataUtils;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.common.internal.GunAttributeSyncState;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import mod.chloeprime.gunsmithlib.common.util.InternalBulletCreateEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;

import java.util.Optional;
import java.util.function.BiConsumer;

import static mod.chloeprime.gunsmithlib.api.common.GunAttributes.*;

@Mod.EventBusSubscriber
public class MiscAttributeAdapter {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void bulletDamage(EntityHurtByGunEvent.Pre event) {
        if (event.getLogicalSide().isClient()) {
            return;
        }
        // 近战武器不加射击伤害
        var src = event.getDamageSource(GunDamageSourcePart.NON_ARMOR_PIERCING);
        var isMelee = src.getEntity() == src.getDirectEntity();

        var attacker = event.getAttacker();
        if (attacker == null) {
            return;
        }
        var attribute = isMelee ? Attributes.ATTACK_DAMAGE : BULLET_DAMAGE.get();
        var oldDamage = event.getBaseAmount();
        var newDamage = GsHelper.getAttributeValueWithBase(attacker, attribute, oldDamage);
        event.setBaseAmount((float) newDamage);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void bulletSpeed(InternalBulletCreateEvent event) {
        var attacker = event.getImpl().getShooter();
        if (attacker.level().isClientSide) {
            return;
        }
        var bullet = event.getImpl().getBullet();

        var oldMotion = bullet.getDeltaMovement();
        var attribute = BULLET_SPEED.get();
        var oldSpeed = oldMotion.length();
        var newSpeed = GsHelper.getAttributeValueWithBase(attacker, attribute, oldSpeed);
        // 速度沒有被Attribute修改的情況
        if (Math.abs(newSpeed - oldSpeed) < 1e-4) {
            return;
        }
        var direction = oldSpeed == 0 ? bullet.getLookAngle() : oldMotion.scale(1 / oldSpeed);
        bullet.setDeltaMovement(direction.scale(newSpeed));
    }

    public static double rpm(LivingEntity attacker) {
        var attribute = RPM.get();
        return attacker.getAttributeValue(attribute);
    }

    @SubscribeEvent
    public static void defaultValues(LivingEvent.LivingTickEvent event) {
        var user = event.getEntity();
        if (user.level().isClientSide) {
            return;
        }
        final var interval = 5;
        if ((user.level().getGameTime() + user.hashCode()) % interval != 0) {
            return;
        }
        var newMH = user.getMainHandItem();
        var syncState = (GunAttributeSyncState) user;
        Gunsmith.getGunInfo(newMH).ifPresentOrElse(gun -> {
            syncState.gunsmith$setInGunMode(true);
            var cache = IGunOperator.fromLivingEntity(user).getCacheProperty();

            double damage = AttachmentDataUtils.getDamageWithAttachment(newMH, gun.index().getGunData()) / gun.index().getBulletData().getBulletAmount();
            float speed = (cache == null
                    ? gun.index().getBulletData().getSpeed()
                    : cache.<Float>getCache(AmmoSpeedModifier.ID)) / 20;
            int rpm = cache == null
                    ? gun.index().getGunData().getRoundsPerMinute(gun.getFireMode())
                    : cache.<Integer>getCache(RpmModifier.ID);

            setBaseValue(user, BULLET_DAMAGE.get(), damage);
            setBaseValue(user, BULLET_SPEED.get(), speed);
            setBaseValue(user, RPM.get(), rpm);
        }, () -> {
            if (!syncState.gunsmith$isInGunMode()) {
                return;
            }
            syncState.gunsmith$setInGunMode(false);
            setBaseValue(user, BULLET_DAMAGE.get(), BULLET_DAMAGE.get().getDefaultValue());
            setBaseValue(user, BULLET_SPEED.get(), BULLET_SPEED.get().getDefaultValue());
            setBaseValue(user, RPM.get(), RPM.get().getDefaultValue());
        });
    }

    private static void setBaseValue(LivingEntity owner, Attribute attribute, double value) {
        Optional.ofNullable(owner.getAttribute(attribute))
                .ifPresent(ai -> ai.setBaseValue(value));
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class AttributeAttacher {
        @SubscribeEvent
        public static void onAttachAttributes(EntityAttributeModificationEvent event) {
            event.getTypes().forEach(et -> addAll(et, event::add,
                    BULLET_DAMAGE,
                    BULLET_SPEED,
                    V_RECOIL,
                    H_RECOIL,
                    RPM));
        }

        @SafeVarargs
        private static void addAll(EntityType<? extends LivingEntity> type, BiConsumer<EntityType<? extends LivingEntity>, Attribute> add, RegistryObject<? extends Attribute>... attribs) {
            for (RegistryObject<? extends Attribute> a : attribs)
                add.accept(type, a.get());
        }
    }
}
