package mod.chloeprime.gunsmithlib.api.common;

import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.common.loot.InitGunInfoFunction;
import mod.chloeprime.gunsmithlib.common.loot.IsGunInstalled;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.ApiStatus;

/**
 * @since 3.2.0
 */
public final class GunLootFunctions {
    /**
     * 设置枪械 id
     */
    public static final LootItemFunctionType INIT_GUN_INFO = registerFunction("init_gun_info", new InitGunInfoFunction.Serializer());

    /**
     * 检测某个枪械 id 是否存在（是否有对应的 index）
     */
    public static final LootItemConditionType IS_GUN_INSTALLED = registerCondition("is_gun_installed", new IsGunInstalled.Serializer());

    /**
     * 设置枪械 id，默认枪内没有子弹
     * @param gunId 枪械 id
     */
    public static LootItemConditionalFunction.Builder<?> initGunInfo(ResourceLocation gunId) {
        return InitGunInfoFunction.initGunInfo(gunId);
    }

    /**
     * 设置枪械 id
     * @param gunId 枪械 id
     * @param ammo 枪内总子弹数量（包含弹匣和膛内）
     */
    public static LootItemConditionalFunction.Builder<?> initGunInfo(ResourceLocation gunId, NumberProvider ammo) {
        return InitGunInfoFunction.initGunInfo(gunId, ammo);
    }

    /**
     * 检测某个枪械 id 是否存在（是否有对应的 index）
     * @param gunId 待检测的枪械 id
     * @return 该枪械 id 对应的枪械是否存在
     */
    public static LootItemCondition.Builder isGunInstalled(ResourceLocation gunId) {
        return IsGunInstalled.isGunInstalled(gunId);
    }

    private GunLootFunctions() {
    }

    private static LootItemFunctionType registerFunction(String name, Serializer<? extends LootItemFunction> serializer) {
        return Registry.register(
                BuiltInRegistries.LOOT_FUNCTION_TYPE,
                new ResourceLocation(GunsmithLib.MOD_ID, name),
                new LootItemFunctionType(serializer));
    }

    private static LootItemConditionType registerCondition(String name, Serializer<? extends LootItemCondition> serializer) {
        return Registry.register(
                BuiltInRegistries.LOOT_CONDITION_TYPE,
                new ResourceLocation(GunsmithLib.MOD_ID, name),
                new LootItemConditionType(serializer));
    }

    @ApiStatus.Internal
    public static void init() {
    }
}
