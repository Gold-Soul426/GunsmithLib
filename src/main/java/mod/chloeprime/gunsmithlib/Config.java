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

    static final ForgeConfigSpec SPEC = BUILDER.build();
}
