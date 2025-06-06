package mod.chloeprime.gunsmithlib.mixin.client;

import com.tacz.guns.api.vmlib.LuaAnimationConstant;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.api.client.GunsmithLibAnimationConstant;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(value = LuaAnimationConstant.class, remap = false)
public class MixinLuaAnimationConstant {
    @Shadow @Final private Map<String, Object> constantMap;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void injectGunsmithLibVersionInfo(CallbackInfo ci) {
        constantMap.putAll(GsHelper.parseStaticFields(GunsmithLibAnimationConstant.class));
    }
}
