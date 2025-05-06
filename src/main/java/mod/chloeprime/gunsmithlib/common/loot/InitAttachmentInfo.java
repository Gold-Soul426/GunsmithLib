package mod.chloeprime.gunsmithlib.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.tacz.guns.api.item.IAttachment;
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
public class InitAttachmentInfo extends LootItemConditionalFunction {
    private final ResourceLocation attachmentId;

    public InitAttachmentInfo(
            LootItemCondition[] conditions,
            ResourceLocation attachmentId) {
        super(conditions);
        this.attachmentId = attachmentId;
    }

    public static Builder<?> initAttachmentInfo(ResourceLocation attachmentId) {
        return simpleBuilder((conditions) -> new InitAttachmentInfo(conditions, attachmentId));
    }

    public ResourceLocation getAttachmentId() {
        return attachmentId;
    }

    @Override
    public @Nonnull ItemStack run(ItemStack stack, LootContext context) {
        if (stack.getItem() instanceof IAttachment attachmentItem) {
            attachmentItem.setAttachmentId(stack, this.attachmentId);
        }
        return stack;
    }

    @Override
    public @Nonnull LootItemFunctionType getType() {
        return Objects.requireNonNull(GunLootFunctions.INIT_ATTACHMENT_INFO);
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<InitAttachmentInfo> {
        public void serialize(JsonObject json, InitAttachmentInfo instance, JsonSerializationContext serializationContext) {
            super.serialize(json, instance, serializationContext);
            json.addProperty("attachment_id", instance.attachmentId.toString());
        }

        public @Nonnull InitAttachmentInfo deserialize(JsonObject json, JsonDeserializationContext deserializationContext, LootItemCondition[] conditions) {
            ResourceLocation attachmentId = new ResourceLocation(GsonHelper.getAsString(json, "attachment_id"));
            return new InitAttachmentInfo(conditions, attachmentId);
        }
    }
}
