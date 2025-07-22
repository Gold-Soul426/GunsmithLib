package mod.chloeprime.gunsmithlib.api.common;

import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;

/**
 * @since 3.7.0
 */
@SuppressWarnings("unused")
public interface VanillaCooldownAPI {
    /**
     * 获取当前物品的冷却时间
     * @return 当前物品的冷却时间，单位为秒
     */
    float gunsmith_getCooldownSeconds();

    /**
     * 获取当前物品的冷却时间百分比
     * @return 当前物品的冷却时间百分比，0 为冷却完毕可以使用，1 为刚开始冷却
     */
    float gunsmith_getCooldownPercent();

    /**
     * @deprecated 请使用 {@link GsHelper#getCooldownDuration}，枪包脚本中不应该使用这个方法
     */
    @Deprecated(since = "4.4.0", forRemoval = true)
    static float gunsmithlib$getCooldownDuration(ItemCooldowns cooldowns, Item item) {
        return GsHelper.getCooldownDuration(cooldowns, item);
    }
}
