package mod.chloeprime.gunsmithlib.api.common;

import mod.chloeprime.gunsmithlib.common.util.GsHelper;

/**
 * 本模组添加的
 * 双端脚本通用常量
 */
public class GunsmithLibScriptingConstant {
    @SuppressWarnings("unused")
    public static final boolean GUNSMITHLIB_INSTALLED = true;

    public static final int GUNSMITHLIB_MAJOR_VERSION;
    public static final int GUNSMITHLIB_MINOR_VERSION;
    public static final int GUNSMITHLIB_PATCH_VERSION;

    static {
        var version = GsHelper.getModVersion();
        GUNSMITHLIB_MAJOR_VERSION = version[0];
        GUNSMITHLIB_MINOR_VERSION = version[1];
        GUNSMITHLIB_PATCH_VERSION = version[2];
    }
}
