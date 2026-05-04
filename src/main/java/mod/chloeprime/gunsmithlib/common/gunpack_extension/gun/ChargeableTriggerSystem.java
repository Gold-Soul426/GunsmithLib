package mod.chloeprime.gunsmithlib.common.gunpack_extension.gun;

import cn.chloeprime.commons.rpc.RPCContext;
import cn.chloeprime.commons.rpc.RPCFlow;
import cn.chloeprime.commons.rpc.RemoteCallable;
import com.tacz.guns.api.entity.IGunOperator;
import com.tacz.guns.api.event.common.GunShootEvent;
import com.tacz.guns.item.ModernKineticGunScriptAPI;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

import java.util.Objects;
import java.util.Optional;

@Mod.EventBusSubscriber
public class ChargeableTriggerSystem {
    /**
     * @since 6.0.0
     */
    public static final String DEPRECATION_MESSAGE =
            "Trying to get GunsmithLib's charge system, which is deprecated and not functional. " +
            "Please migrate to TaCZ 1.1.8's charge system.";

    public static final String PDK_CHARGING = GunsmithLib.loc("charging").toString();
    public static final String PDK_CHARGE_BEGAN_TIME = GunsmithLib.loc("charge_begin_time").toString();

    @RemoteCallable(flow = RPCFlow.CLIENT_TO_SERVER)
    public static void beginCharging() {
        var sender = RPCContext.isCalledThroughRPC() ? RPCContext.getSenderPlayer() : null;
        if (sender == null) {
            return;
        }
        beginCharging(sender, Gunsmith.getGunInfo(sender.getMainHandItem()).orElse(null));
    }

    public static void beginCharging(Player user, GunInfo gun) {
        if (user == null || gun == null || user.level().isClientSide()) {
            return;
        }

        setChargeBeginTime(gun.gunStack(), user.level().getGameTime());

        var api = new ModernKineticGunScriptAPI();
        api.setItemStack(gun.gunStack());
        api.setShooter(user);
        api.setDataHolder(IGunOperator.fromLivingEntity(user).getDataHolder());

        Optional.ofNullable(gun.index().getScript())
                .map(script -> GsHelper.checkFunction(script.get("gunsmithlib_begin_charging")))
                .ifPresent(func -> func.call(CoerceJavaToLua.coerce(api)));
    }

    @SubscribeEvent
    public static void onPlayerShoot(GunShootEvent event) {
        var shooter = event.getShooter();
        if (shooter == null || shooter.level().isClientSide()) {
            return;
        }
        setChargeFinished(event.getGunItemStack());
    }

    public static boolean isCharging(ItemStack gun) {
        return gun.hasTag() && Objects.requireNonNull(gun.getTag()).getBoolean(PDK_CHARGING);
    }

    public static boolean isInChargingSequence(ItemStack gun) {
        return gun.hasTag() && Objects.requireNonNull(gun.getTag()).contains(PDK_CHARGE_BEGAN_TIME);
    }

    public static long getChargeBeginTime(ItemStack gun) {
        return (gun.hasTag() && isInChargingSequence(gun))
                ? Objects.requireNonNull(gun.getTag()).getLong(PDK_CHARGE_BEGAN_TIME)
                : 0;
    }

    /**
     * 单位为刻
     */
    public static long getChargeTime(ItemStack gun, long now) {
        return isInChargingSequence(gun) ? Math.max(0, now - getChargeBeginTime(gun)) : 0;
    }

    public static void setChargeBeginTime(ItemStack gun, long value) {
        var nbt = gun.getOrCreateTag();
        nbt.putBoolean(PDK_CHARGING, true);
        nbt.putLong(PDK_CHARGE_BEGAN_TIME, value);
    }

    public static void setChargeFinished(ItemStack gun) {
        if (gun.hasTag()) {
            Objects.requireNonNull(gun.getTag()).remove(PDK_CHARGING);
        }
    }

    public static void removeChargeBeginTimeIfNeeded(ItemStack gun) {
        if (gun.hasTag() && !isCharging(gun)) {
            Objects.requireNonNull(gun.getTag()).remove(PDK_CHARGE_BEGAN_TIME);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            return;
        }
        if (event.player.level().isClientSide()) {
            return;
        }
        removeChargeBeginTimeIfNeeded(event.player.getMainHandItem());
    }
}
