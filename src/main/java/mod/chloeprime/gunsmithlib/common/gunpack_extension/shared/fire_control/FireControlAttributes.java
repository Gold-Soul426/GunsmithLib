package mod.chloeprime.gunsmithlib.common.gunpack_extension.shared.fire_control;

import mod.chloeprime.gunsmithlib.GunsmithLib;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

@Mod.EventBusSubscriber
public class FireControlAttributes {
    private static final DeferredRegister<Attribute> DFR = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, GunsmithLib.MOD_ID);

    public static final RegistryObject<Attribute> AIM_LOCK_RANGE = DFR.register("aim_lock_range", () -> new RangedAttribute(
            "attribute.name.%s.aim_lock_range".formatted(GunsmithLib.MOD_ID), 0, 0, 1024
    ).setSyncable(true));

    public static final RegistryObject<Attribute> AIM_LOCK_ANGLE = DFR.register("aim_lock_angle", () -> new RangedAttribute(
            "attribute.name.%s.aim_lock_angle".formatted(GunsmithLib.MOD_ID), 0, 0, 360
    ).setSyncable(true));

    @ApiStatus.Internal
    public static void init(IEventBus modbus) {
        DFR.register(modbus);
        modbus.addListener(FireControlAttributes::onCreateAttributes);
    }

    private static void onCreateAttributes(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(type -> {
            event.add(type, AIM_LOCK_RANGE.get());
            event.add(type, AIM_LOCK_ANGLE.get());
        });
    }
}
