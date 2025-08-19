package mod.chloeprime.gunsmithlib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.tooltip.ClientGunTooltip;
import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientGunTooltip.class, remap = false)
public class MixinClientGunTooltip {
    @ModifyExpressionValue(
            method = "getText",
            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/AttachmentDataUtils;getDamageWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)D"))
    private double makeDisplayedDamageConsiderAttributeModifiers(double original) {
        var player = Minecraft.getInstance().player;
        return player != null ? GsHelper.getAttributeValueWithBase(player, GunAttributes.BULLET_DAMAGE.get(), original) : original;
    }
}
