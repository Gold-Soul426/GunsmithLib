package mod.chloeprime.gunsmithlib.api.common;

import mod.chloeprime.gunsmithlib.GunsmithLib;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.RegistryObject;

public class GunAttributes {
    /**
     * 射击伤害
     */
    public static final RegistryObject<Attribute> BULLET_DAMAGE = GunsmithLib.Attributes.BULLET_DAMAGE;

    /**
     * 子弹速度
     */
    public static final RegistryObject<Attribute> BULLET_SPEED = GunsmithLib.Attributes.BULLET_SPEED;

    /**
     * 垂直后坐力
     */
    public static final RegistryObject<Attribute> V_RECOIL = GunsmithLib.Attributes.V_RECOIL;

    /**
     * 水平后坐力
     */
    public static final RegistryObject<Attribute> H_RECOIL = GunsmithLib.Attributes.H_RECOIL;

    /**
     * 水平后坐力
     * @since 2.1.0
     */
    public static final RegistryObject<Attribute> RPM = GunsmithLib.Attributes.RPM;
}
