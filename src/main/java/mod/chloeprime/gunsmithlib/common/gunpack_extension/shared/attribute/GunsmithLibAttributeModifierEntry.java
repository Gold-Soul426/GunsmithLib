package mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.attribute;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * 参见 <a href="https://zh.minecraft.wiki/w/%E5%B1%9E%E6%80%A7">Minecraft Wiki 上的 Attribute</a>
 */
public class GunsmithLibAttributeModifierEntry {
    /**
     * 要作用在什么属性上。<p>
     * 必填
     */
    @SuppressWarnings("unused")
    private ResourceLocation attribute;

    /**
     * 属性修饰器 id。<p>
     * 必填
     */
    @SuppressWarnings("unused")
    private UUID id;

    /**
     * 属性修饰器名称，该名称可能被神化模组和一些其他的调试功能看到。<p>
     * 可选
     */
    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    private String name = "Gun / Attachment";

    /**
     * 属性修饰器的值，<p>
     * 必填
     */
    @SuppressWarnings("unused")
    private double amount;

    /**
     * 属性修饰器的运算模式 <p>
     * 可选，默认为加法
     */
    @SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
    private AttributeModifier.Operation operation = AttributeModifier.Operation.ADDITION;

    // 下面是代码
    private transient Pair<Attribute, AttributeModifier> bakedResult;
    private transient int valid = 0;

    public final ResourceLocation getAttributeId() {
        return attribute;
    }

    public final UUID getModifierId() {
        return id;
    }

    public final String getModifierName() {
        return name;
    }

    public final double getAmount() {
        return amount;
    }

    public final AttributeModifier.Operation getOperation() {
        return operation;
    }

    @SuppressWarnings("deprecation")
    public final Optional<Attribute> getAttribute() {
        return BuiltInRegistries.ATTRIBUTE.getOptional(getAttributeId());
    }

    public Optional<Pair<Attribute, AttributeModifier>> getModifier() {
        if (valid == -1) {
            return Optional.empty();
        }
        if (valid == 1) {
            return Optional.of(Objects.requireNonNull(bakedResult));
        }
        bakedResult = bake();
        valid = bakedResult != null ? 1 : -1;
        return Optional.ofNullable(bakedResult);
    }

    private @Nullable Pair<Attribute, AttributeModifier> bake() {
        if (id == null) {
            return null;
        }
        var attribute = getAttribute().orElse(null);
        if (attribute == null) {
            return null;
        }
        return Pair.of(attribute, new AttributeModifier(getModifierId(), getModifierName(), getAmount(), getOperation()));
    }
}
