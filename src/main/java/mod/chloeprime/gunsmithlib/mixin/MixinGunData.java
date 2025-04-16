package mod.chloeprime.gunsmithlib.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.pojo.data.gun.GunData;
import mod.chloeprime.gunsmithlib.common.MiscAttributeAdapter;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = GunData.class, remap = false)
public class MixinGunData {
    @ModifyExpressionValue(
            method = "getShootInterval",
            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/resource/modifier/AttachmentCacheProperty;getCache(Ljava/lang/String;)Ljava/lang/Object;")
    )
    private Object adjustRpmByAttribute(Object baseRpm, LivingEntity shooter, FireMode fireMode) {
        return (int) MiscAttributeAdapter.rpm(shooter);
    }
}
