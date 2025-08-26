package mod.chloeprime.gunsmithlib.api.common;

import mod.chloeprime.gunsmithlib.GunsmithLib;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.registries.RegistryObject;

public class GunAttributes {
    /**
     * 射击伤害
     */
    public static final RegistryObject<Attribute> BULLET_DAMAGE = GunsmithLib.Attributes.BULLET_DAMAGE;/**

     * 穿甲倍率
     * @since 4.6.0
     */
    public static final RegistryObject<Attribute> ARMOR_PIERCING_RATIO = GunsmithLib.Attributes.ARMOR_PIERCING_RATIO;

    /**
     * 爆头倍率
     * @since 4.6.0
     */
    public static final RegistryObject<Attribute> HEADSHOT_MULTIPLIER = GunsmithLib.Attributes.HEADSHOT_MULTIPLIER;

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

    /**
     * 弹匣容量，只有放在物品上的时候有效
     * @since 3.1.0
     */
    public static final RegistryObject<Attribute> AMMO_CAPACITY = GunsmithLib.Attributes.AMMO_CAPACITY;

    /**
     * 换弹速度
     * @since 4.1.0
     */
    public static final RegistryObject<Attribute> RELOAD_SPEED = GunsmithLib.Attributes.RELOAD_SPEED;
}
