package mod.chloeprime.gunsmithlib.client.papi;

import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.api.util.Rangefinder;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class RangefinderPapi implements Function<ItemStack, String> {
    public static final String NAME = GunsmithLib.loc("rangefinder_result").toString();
    public static final RangefinderPapi INSTANCE = new RangefinderPapi();

    private static final String FALLBACK = String.valueOf(0);
    private static final Minecraft MC = Minecraft.getInstance();

    @Override
    public String apply(ItemStack stack) {
        var shooter = MC.player;
        if (shooter == null) {
            return FALLBACK;
        }
        var gun = Gunsmith.getGunInfo(stack).orElse(null);
        if (gun == null) {
            return FALLBACK;
        }
        double maxRange = GsHelper.getEstimatedMaxRange(shooter, shooter.getMainHandItem());
        double range = Rangefinder.clip(shooter, shooter.getEyePosition(), shooter.getLookAngle(), 0, maxRange).getLength();
        return "%.2f".formatted(range);
    }

    private RangefinderPapi() {
    }
}
