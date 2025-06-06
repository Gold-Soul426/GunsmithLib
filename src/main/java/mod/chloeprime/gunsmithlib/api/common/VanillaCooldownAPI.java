package mod.chloeprime.gunsmithlib.api.common;

import mod.chloeprime.gunsmithlib.mixin.ItemCooldownsAccessor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import org.jetbrains.annotations.ApiStatus;

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

    @ApiStatus.Internal
    static float gunsmithlib$getCooldownDuration(ItemCooldowns cooldowns, Item item) {
        var instance = ((ItemCooldownsAccessor) cooldowns).getCooldowns().get(item);
        if (instance == null) {
            return 0;
        }
        return instance.endTime - instance.startTime;
    }
}
