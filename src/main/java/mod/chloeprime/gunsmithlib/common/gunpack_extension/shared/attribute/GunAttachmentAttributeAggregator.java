package mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.attribute;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.resource.index.CommonAttachmentIndex;
import com.tacz.guns.resource.index.CommonGunIndex;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.attachment.EnhancedAttachmentData;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.EnhancedGunData;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.GunsmithLibSharedDataExtension;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static net.minecraft.world.item.ItemStack.ATTRIBUTE_MODIFIER_FORMAT;

@Mod.EventBusSubscriber
public class GunAttachmentAttributeAggregator {
    public static Multimap<Attribute, AttributeModifier> getGunIndexAttributeModifiers(CommonGunIndex index) {
        return ((EnhancedGunData) index.getGunData())
                .gunsmith$getGunsmithLibExtension()
                .map(GunsmithLibSharedDataExtension::getBakedAttributeModifiers)
                .orElse(ImmutableMultimap.of());
    }

    public static Multimap<Attribute, AttributeModifier> getAttachmentIndexAttributeModifiers(CommonAttachmentIndex index) {
        return ((EnhancedAttachmentData) index.getData())
                .gunsmith$getGunsmithLibExtension()
                .map(GunsmithLibSharedDataExtension::getBakedAttributeModifiers)
                .orElse(ImmutableMultimap.of());
    }

    @SuppressWarnings("deprecation")
    public static Multimap<Attribute, AttributeModifier> getAttachmentAttributeModifiers(ItemStack stack) {
        var fallback = ImmutableMultimap.<Attribute, AttributeModifier>of();
        if (!stack.hasTag()) {
            return fallback;
        }
        CompoundTag tag = Objects.requireNonNull(stack.getTag());
        if (!tag.contains("AttachmentAttributeModifiers", Tag.TAG_LIST)) {
            var ati = Gunsmith.getAttachmentInfo(stack).orElse(null);
            return ati != null ? getAttachmentIndexAttributeModifiers(ati.index()) : fallback;
        }

        var result = HashMultimap.<Attribute, AttributeModifier>create();
        var modifierTagList = tag.getList("AttachmentAttributeModifiers", Tag.TAG_COMPOUND);

        for (int i = 0; i < modifierTagList.size(); ++i) {
            CompoundTag modifierTag = modifierTagList.getCompound(i);
            Attribute attribute = BuiltInRegistries.ATTRIBUTE
                    .getOptional(ResourceLocation.tryParse(modifierTag.getString("AttributeName")))
                    .orElse(null);
            if (attribute != null) {
                var modifier = AttributeModifier.load(modifierTag);
                if (modifier != null && modifier.getId().getLeastSignificantBits() != 0L && modifier.getId().getMostSignificantBits() != 0L) {
                    result.put(attribute, modifier);
                }
            }
        }

        return result;
    }

    private static final AttachmentType[] ATTACHMENT_TYPE_REGISTRY = AttachmentType.values();
    private static final ThreadLocal<Map<Pair<Attribute, AttributeModifier.Operation>, AttributeModifier>> MERGE_BUFFER = ThreadLocal.withInitial(HashMap::new);

    @SubscribeEvent
    public static void onGunAttribute(ItemAttributeModifierEvent event) {
        var slot = EquipmentSlot.MAINHAND;
        if (event.getSlotType() != slot) {
            return;
        }

        var stack = event.getItemStack();
        var gun = Gunsmith.getGunInfo(stack).orElse(null);
        if (gun == null) {
            return;
        }
        var buffer = MERGE_BUFFER.get();
        try {
            buffer.clear();
            // 使用 AttributeModifiers 标签覆盖的情况
            var hasOverride = stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("AttributeModifiers", Tag.TAG_LIST);
            if (!hasOverride) {
                getGunIndexAttributeModifiers(gun.index()).forEach((a, am) -> putMerge(buffer, a, am));
            }
            // 叠加配件的modifiers
            for (var attachmentType : ATTACHMENT_TYPE_REGISTRY) {
                ItemStack attachment = gun.gunItem().getAttachment(gun.gunStack(), attachmentType);
                getAttachmentAttributeModifiers(attachment).forEach((a, am) -> putMerge(buffer, a, am));
            }
            buffer.forEach((key, modifier) -> event.addModifier(key.getLeft(), modifier));
        } finally {
            buffer.clear();
        }
    }

    private static void putMerge(
            Map<Pair<Attribute, AttributeModifier.Operation>, AttributeModifier> buffer,
            Attribute attribute,
            AttributeModifier modifier
    ) {
        var operation = modifier.getOperation();
        var key = Pair.of(attribute, operation);
        var currentModifier = buffer.get(key);
        if (currentModifier == null) {
            buffer.put(key, modifier);
            return;
        }
        var newAmount = switch (operation) {
            case ADDITION, MULTIPLY_BASE -> currentModifier.getAmount() + modifier.getAmount();
            case MULTIPLY_TOTAL -> (1 + currentModifier.getAmount()) * (1 + modifier.getAmount()) - 1;
        };
        var newModifier = new AttributeModifier(currentModifier.getId(), currentModifier.getName(), newAmount, operation);
        buffer.put(key, newModifier);
    }

    public static void attachmentAttributeModifierTooltip(ItemStack attachment, List<Component> tooltip) {
        var ati = Gunsmith.getAttachmentInfo(attachment).orElse(null);
        if (ati == null) {
            return;
        }
        var modifiers = getAttachmentAttributeModifiers(attachment);
        if (modifiers.isEmpty()) {
            return;
        }

        tooltip.add(CommonComponents.EMPTY);
        tooltip.add(Component.translatable("gunsmithlib.item.modifiers.attachment").withStyle(ChatFormatting.GRAY));

        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers.entries()) {
            AttributeModifier attributemodifier = entry.getValue();
            double amount = attributemodifier.getAmount();

            double amountDisplay;
            if (attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_BASE && attributemodifier.getOperation() != AttributeModifier.Operation.MULTIPLY_TOTAL) {
                if (entry.getKey().equals(Attributes.KNOCKBACK_RESISTANCE)) {
                    amountDisplay = amount * 10;
                } else {
                    amountDisplay = amount;
                }
            } else {
                amountDisplay = amount * 100;
            }

            if (amount > 0.0D) {
                tooltip.add(Component.translatable("attribute.modifier.plus." + attributemodifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(amountDisplay), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.BLUE));
            } else if (amount < 0.0D) {
                amountDisplay *= -1;
                tooltip.add(Component.translatable("attribute.modifier.take." + attributemodifier.getOperation().toValue(), ATTRIBUTE_MODIFIER_FORMAT.format(amountDisplay), Component.translatable(entry.getKey().getDescriptionId())).withStyle(ChatFormatting.RED));
            }
        }
    }
}
