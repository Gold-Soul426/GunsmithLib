package mod.chloeprime.gunsmithlib;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.DoubleValue CROSSFIRE_BUFF_POWER = BUILDER
            .comment("Amount of damage scale boost per level of Crossfire buff gives")
            .defineInRange("crossfire_buff_power", 0.3, Double.MIN_NORMAL, Double.MAX_VALUE);

    public static final ForgeConfigSpec.BooleanValue ENABLE_SPECIAL_HURT = BUILDER
            .comment("Enable calling hurt function with invokespecial on entities with a specific tag")
            .define("enable_special_hurt", true);

    /**
     * @since 3.2.1
     */
    public static final ForgeConfigSpec.BooleanValue USE_ATTACK_DAMAGE = BUILDER
            .comment("If true, gun damage will be affect by `minecraft:generic.attack_damage` attribute, then use the result as base value for `gunsmithlib:bullet_damage` attribute")
            .define("use_attack_damage", true);

    /**
     * @since 4.4.2
     */
    public static final ForgeConfigSpec.BooleanValue ALTERNATIVE_ARMOR_PIERCING_FORMULA = BUILDER
            .comment("""
                    If true, guns' armor piercing mechanic will have only one hit with target armor reduced, instead of dealing two hits.
                    This can fix the bug that Apotheosis's magic armor can reduce armor-piercing damage,
                    and make Cataclysm's dynamic damage reduction work properly for guns.
                    
                    Added in version 4.4.2""")
            .define("alt_ap_formula", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
