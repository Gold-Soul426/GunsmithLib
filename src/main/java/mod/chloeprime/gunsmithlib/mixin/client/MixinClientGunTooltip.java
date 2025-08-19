package mod.chloeprime.gunsmithlib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.tacz.guns.client.tooltip.ClientGunTooltip;
import mod.chloeprime.gunsmithlib.api.common.GunAttributes;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientGunTooltip.class, remap = false)
public class MixinClientGunTooltip {
    @Shadow
    @Final
    private ItemStack gun;

    @ModifyExpressionValue(
            method = "getText",
            at = @At(value = "INVOKE", target = "Lcom/tacz/guns/util/AttachmentDataUtils;getDamageWithAttachment(Lnet/minecraft/world/item/ItemStack;Lcom/tacz/guns/resource/pojo/data/gun/GunData;)D"))
    private double makeDisplayedDamageConsiderAttributeModifiers(double original) {
        var item = this.gun;
        return gun != null ? GsHelper.evaluateItemAttribute(item, GunAttributes.BULLET_DAMAGE, original) : original;
    }
}
