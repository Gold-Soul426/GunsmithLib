package mod.chloeprime.gunsmithlib.common.gunpack_extension.gun.ammo_variant;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.tacz.guns.api.item.gun.FireMode;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public record AmmoVariantStorage(
        Map<String, OfSinglePart> byPartStorage
) {
    public static final String PDK = GunsmithLib.loc("ammo_variant_storage").toString();
    public static final AmmoVariantStorage DEFAULT = new AmmoVariantStorage(Collections.emptyMap());

    public static final Codec<AmmoVariantStorage> CODEC = Codec
            .unboundedMap(Codec.STRING, OfSinglePart.CODEC)
            .xmap(AmmoVariantStorage::new, AmmoVariantStorage::byPartStorage);

    public static AmmoVariantStorage of(ItemStack stack) {
        if (!stack.hasTag()) {
            return DEFAULT;
        }
        var tag = Objects.requireNonNull(stack.getTag());
        if (tag.contains(PDK)) {
            return CODEC.decode(NbtOps.INSTANCE, tag.get(PDK)).result().map(Pair::getFirst).orElse(DEFAULT);
        } else {
            return DEFAULT;
        }
    }

    public static void set(ItemStack stack, AmmoVariantStorage value) {
        var cleaned = cleanup(value);
        if (cleaned.isEmpty()) {
            if (stack.hasTag()) {
                Objects.requireNonNull(stack.getTag()).remove(PDK);
            }
            return;
        }
        var encoded = CODEC.encodeStart(NbtOps.INSTANCE, cleaned.get());
        if (encoded.error().isPresent()) {
            GunsmithLib.LOGGER.error("Failed to set ammo variant storage: {}", encoded.error().get().message());
        }
        encoded.result().ifPresentOrElse(
                tag -> stack.getOrCreateTag().put(PDK, tag),
                () -> GunsmithLib.LOGGER.error("Failed to set ammo variant storage")
        );
    }

    public static Optional<AmmoVariantStorage> cleanup(AmmoVariantStorage original) {
        var table = new HashMap<>(original.byPartStorage());
        table.entrySet().removeIf(entry -> OfSinglePart.DEFAULT.equals(entry.getValue()));
        return table.isEmpty() ? Optional.empty() : Optional.of(new AmmoVariantStorage(Collections.unmodifiableMap(table)));
    }

    public record OfSinglePart(
            int selectedVariant,
            int storedAmmo,
            FireMode fireMode
    ) {
        public static final OfSinglePart DEFAULT = new OfSinglePart(0, 0, FireMode.UNKNOWN);

        public static final Codec<OfSinglePart> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("selected_variant").forGetter(OfSinglePart::selectedVariant),
                Codec.INT.fieldOf("stored_ammo").forGetter(OfSinglePart::storedAmmo),
                GsHelper.enumCodec(FireMode.class).optionalFieldOf("fire_mode", FireMode.UNKNOWN).forGetter(OfSinglePart::fireMode)
        ).apply(instance, OfSinglePart::new));
    }
}
