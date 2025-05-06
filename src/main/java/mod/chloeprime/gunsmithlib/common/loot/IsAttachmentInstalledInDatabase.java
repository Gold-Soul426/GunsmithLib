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
public class IsAttachmentInstalledInDatabase implements LootItemCondition {
    private final ResourceLocation attachmentId;

    public IsAttachmentInstalledInDatabase(ResourceLocation attachmentId) {
        this.attachmentId = attachmentId;
    }

    public static IsAttachmentInstalledInDatabase.Builder isAttachmentInstalledInDatabase(ResourceLocation attachmentId) {
        return new Builder().attachmentId(attachmentId);
    }

    public static class Builder implements LootItemCondition.Builder {
        private ResourceLocation attachmentId;

        public Builder attachmentId(ResourceLocation attachmentId) {
            this.attachmentId = attachmentId;
            return this;
        }

        @Nonnull
        public IsAttachmentInstalledInDatabase build() {
            return new IsAttachmentInstalledInDatabase(this.attachmentId);
        }
    }

    public ResourceLocation getAttachmentId() {
        return this.attachmentId;
    }

    @Override
    public boolean test(LootContext context) {
        return TimelessAPI.getCommonAttachmentIndex(this.attachmentId).isPresent();
    }

    @Override
    public @Nonnull LootItemConditionType getType() {
        return Objects.requireNonNull(GunLootFunctions.IS_ATTACHMENT_INSTALLED_IN_DATABASE);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<IsAttachmentInstalledInDatabase> {
        @Override
        public void serialize(JsonObject json, IsAttachmentInstalledInDatabase instance, JsonSerializationContext serializationContext) {
            json.addProperty("attachment_id", instance.attachmentId.toString());
        }

        @Override
        public @Nonnull IsAttachmentInstalledInDatabase deserialize(JsonObject json, JsonDeserializationContext deserializationContext) {
            ResourceLocation attachmentId = new ResourceLocation(GsonHelper.getAsString(json, "attachment_id"));
            return new IsAttachmentInstalledInDatabase(attachmentId);
        }
    }
}
