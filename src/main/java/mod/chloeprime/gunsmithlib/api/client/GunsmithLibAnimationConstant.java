package mod.chloeprime.gunsmithlib.api.client;

import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.common.util.GsHelper;

/**
 * @since 3.7.0
 */
public class GunsmithLibAnimationConstant {
    @SuppressWarnings("unused")
    public static final boolean GUNSMITHLIB_INSTALLED = true;

    public static final int GUNSMITHLIB_MAJOR_VERSION;
    public static final int GUNSMITHLIB_MINOR_VERSION;
    public static final int GUNSMITHLIB_PATCH_VERSION;

    /**
     * "gunsmithlib:cooldown_start"
     */
    public static final String GUNSMITHLIB_INPUT_COOLDOWN_START = GunsmithLib.loc("cooldown_start").toString();

    static {
        var version = GsHelper.getModVersion();
        GUNSMITHLIB_MAJOR_VERSION = version[0];
        GUNSMITHLIB_MINOR_VERSION = version[1];
        GUNSMITHLIB_PATCH_VERSION = version[2];
    }
}
