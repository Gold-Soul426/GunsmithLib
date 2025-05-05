package mod.chloeprime.gunsmithlib.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.gun.FireMode;
import mod.chloeprime.gunsmithlib.api.common.GunLootFunctions;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class InitGunInfoFunction extends LootItemConditionalFunction {
    private final ResourceLocation gunId;
    private final NumberProvider ammo;

    protected InitGunInfoFunction(
            LootItemCondition[] conditions,
            ResourceLocation gunId,
            NumberProvider ammoCount) {
        super(conditions);
        this.gunId = gunId;
        this.ammo = ammoCount;
    }

    @Override
    protected @Nonnull ItemStack run(ItemStack stack, LootContext context) {
        if (stack.getItem() instanceof IGun gunItem) {
            gunItem.setGunId(stack, this.gunId);
        }
        Gunsmith.getGunInfo(stack).ifPresent(gun -> {
            // 初始化开火模式
            gun.gunItem().setFireMode(gun.gunStack(), gun.index().getGunData().getFireModeSet()
                    .stream().findFirst()
                    .orElse(FireMode.UNKNOWN));
            // 填充弹药
            gun.setTotalAmmo(ammo.getInt(context));
            // 初始化热量
            if (gun.index().getGunData().hasHeatData()) {
                gun.gunItem().setHeatAmount(gun.gunStack(), 0);
            }
        });
        return stack;
    }

    public static LootItemConditionalFunction.Builder<?> initGunInfo(ResourceLocation gunId) {
        return initGunInfo(gunId, ConstantValue.exactly(0));
    }

    public static LootItemConditionalFunction.Builder<?> initGunInfo(ResourceLocation gunId, NumberProvider ammo) {
        return simpleBuilder((conditions) -> new InitGunInfoFunction(conditions, gunId, ammo));
    }

    @Override
    public @Nonnull LootItemFunctionType getType() {
        return Objects.requireNonNull(GunLootFunctions.INIT_GUN_INFO);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<InitGunInfoFunction> {
        public void serialize(JsonObject json, InitGunInfoFunction instance, JsonSerializationContext serializationContext) {
            super.serialize(json, instance, serializationContext);
            json.addProperty("gun_id", instance.gunId.toString());
            json.add("ammo", serializationContext.serialize(instance.ammo));
        }

        public @Nonnull InitGunInfoFunction deserialize(JsonObject json, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            ResourceLocation gunId = new ResourceLocation(GsonHelper.getAsString(json, "gun_id"));
            NumberProvider ammoCount = GsonHelper.getAsObject(json, "ammo", ConstantValue.exactly(0), deserializationContext, NumberProvider.class);
            return new InitGunInfoFunction(conditions, gunId, ammoCount);
        }
    }
}
