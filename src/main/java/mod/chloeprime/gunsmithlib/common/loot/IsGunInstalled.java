package mod.chloeprime.gunsmithlib.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.tacz.guns.api.TimelessAPI;
import mod.chloeprime.gunsmithlib.api.common.GunLootFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class IsGunInstalled implements LootItemCondition {
    private final ResourceLocation gunId;

    public IsGunInstalled(ResourceLocation gunId) {
        this.gunId = gunId;
    }

    public static IsGunInstalled.Builder isGunInstalled(ResourceLocation gunId) {
        return new Builder().gunId(gunId);
    }

    public static class Builder implements LootItemCondition.Builder {
        private ResourceLocation gunId;

        public Builder gunId(ResourceLocation gunId) {
            this.gunId = gunId;
            return this;
        }

        @Nonnull
        public IsGunInstalled build() {
            return new IsGunInstalled(this.gunId);
        }
    }

    public ResourceLocation getGunId() {
        return this.gunId;
    }

    @Override
    public boolean test(LootContext context) {
        return TimelessAPI.getCommonGunIndex(this.gunId).isPresent();
    }

    @Override
    public @Nonnull LootItemConditionType getType() {
        return Objects.requireNonNull(GunLootFunctions.IS_GUN_INSTALLED);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<IsGunInstalled> {
        @Override
        public void serialize(JsonObject json, IsGunInstalled instance, JsonSerializationContext serializationContext) {
            json.addProperty("gun_id", instance.gunId.toString());
        }

        @Override
        public @Nonnull IsGunInstalled deserialize(JsonObject json, JsonDeserializationContext deserializationContext) {
            ResourceLocation gunId = new ResourceLocation(GsonHelper.getAsString(json, "gun_id"));
            return new IsGunInstalled(gunId);
        }
    }
}
