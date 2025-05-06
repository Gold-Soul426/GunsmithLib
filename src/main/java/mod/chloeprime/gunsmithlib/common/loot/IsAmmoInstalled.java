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
public class IsAmmoInstalled implements LootItemCondition {
    private final ResourceLocation ammoId;

    public IsAmmoInstalled(ResourceLocation ammoId) {
        this.ammoId = ammoId;
    }

    public static IsAmmoInstalled.Builder isAmmoInstalled(ResourceLocation ammoId) {
        return new Builder().ammoId(ammoId);
    }

    public static class Builder implements LootItemCondition.Builder {
        private ResourceLocation ammoId;

        public Builder ammoId(ResourceLocation ammoId) {
            this.ammoId = ammoId;
            return this;
        }

        @Nonnull
        public IsAmmoInstalled build() {
            return new IsAmmoInstalled(this.ammoId);
        }
    }

    public ResourceLocation getAmmoId() {
        return this.ammoId;
    }

    @Override
    public boolean test(LootContext context) {
        return TimelessAPI.getCommonAmmoIndex(this.ammoId).isPresent();
    }

    @Override
    public @Nonnull LootItemConditionType getType() {
        return Objects.requireNonNull(GunLootFunctions.IS_AMMO_INSTALLED);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<IsAmmoInstalled> {
        @Override
        public void serialize(JsonObject json, IsAmmoInstalled instance, JsonSerializationContext serializationContext) {
            json.addProperty("ammo_id", instance.ammoId.toString());
        }

        @Override
        public @Nonnull IsAmmoInstalled deserialize(JsonObject json, JsonDeserializationContext deserializationContext) {
            ResourceLocation ammoId = new ResourceLocation(GsonHelper.getAsString(json, "ammo_id"));
            return new IsAmmoInstalled(ammoId);
        }
    }
}
