package mod.chloeprime.gunsmithlib.common.gunpack_extension.shared;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.attribute.GunsmithLibAttributeModifierEntry;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

/**
 * 枪械和配件共通的扩展功能
 * @since 4.2.0
 */
public class GunsmithLibSharedDataExtension {
    /**
     * 枪械/配件的属性修饰器
     */
    @SuppressWarnings("unused")
    private @Nullable GunsmithLibAttributeModifierEntry[] attribute_modifiers;

    // 下面是具体实现

    public GunsmithLibAttributeModifierEntry[] getAttributeModifiers() {
        return MoreObjects.firstNonNull(attribute_modifiers,  EMPTY_MODIFIER_POJO_ARRAY);
    }

    private static final GunsmithLibAttributeModifierEntry[] EMPTY_MODIFIER_POJO_ARRAY = new GunsmithLibAttributeModifierEntry[0];
    private transient Multimap<Attribute, AttributeModifier> bakedAttributeModifiers;

    @NotNull
    public Multimap<Attribute, AttributeModifier> getBakedAttributeModifiers() {
        if (bakedAttributeModifiers == null) {
            var builder = ImmutableMultimap.<Attribute, AttributeModifier>builder();
            for (GunsmithLibAttributeModifierEntry pojo : getAttributeModifiers()) {
                pojo.getModifier().ifPresent(builder::put);
            }
            bakedAttributeModifiers = builder.build();
        }
        return bakedAttributeModifiers;
    }
}
