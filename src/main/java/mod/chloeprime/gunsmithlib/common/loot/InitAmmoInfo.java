package mod.chloeprime.gunsmithlib.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.tacz.guns.api.item.IAmmo;
import mod.chloeprime.gunsmithlib.api.common.GunLootFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class InitAmmoInfo extends LootItemConditionalFunction {
    private final ResourceLocation ammoId;

    public InitAmmoInfo(
            LootItemCondition[] conditions,
            ResourceLocation ammoId) {
        super(conditions);
        this.ammoId = ammoId;
    }

    public static Builder<?> initAmmoInfo(ResourceLocation ammoId) {
        return simpleBuilder((conditions) -> new InitAmmoInfo(conditions, ammoId));
    }

    public ResourceLocation getAmmoId() {
        return ammoId;
    }

    @Override
    public @Nonnull ItemStack run(ItemStack stack, LootContext context) {
        if (stack.getItem() instanceof IAmmo ammoItem) {
            ammoItem.setAmmoId(stack, this.ammoId);
        }
        return stack;
    }

    @Override
    public @Nonnull LootItemFunctionType getType() {
        return Objects.requireNonNull(GunLootFunctions.INIT_AMMO_INFO);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<InitAmmoInfo> {
        public void serialize(JsonObject json, InitAmmoInfo instance, JsonSerializationContext serializationContext) {
            super.serialize(json, instance, serializationContext);
            json.addProperty("ammo_id", instance.ammoId.toString());
        }

        public @Nonnull InitAmmoInfo deserialize(JsonObject json, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            ResourceLocation ammoId = new ResourceLocation(GsonHelper.getAsString(json, "ammo_id"));
            return new InitAmmoInfo(conditions, ammoId);
        }
    }
}
