package mod.chloeprime.gunsmithlib.common.util;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.api.util.GunInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.ModList;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class GsHelper {
    /**
     * @return 增益倍率，永远不会为 0
     * @since 3.2.0
     */
    public static double getBuffCoefficient(ResourceLocation gunId, boolean isMelee) {
        if (!isMelee) {
            return 1;
        }
        return TimelessAPI.getCommonGunIndex(gunId)
                .map(index -> index.getGunData().getBulletData().getBulletAmount())
                .map(shrapnel -> shrapnel == 0 ? 1 : 1.0 / Math.abs(shrapnel))
                .orElse(1.0);
    }

    public static Optional<GunInfo> unpack(IGun gunItem, ItemStack gunStack) {
        var gunId = gunItem.getGunId(gunStack);
        return TimelessAPI.getCommonGunIndex(gunId).map(index -> new GunInfo(gunStack, gunItem, gunId, index));
    }

    public static double getAttributeValueWithBase(LivingEntity holder, Attribute attribute, double base) {
        var instance = holder.getAttribute(attribute);
        if (instance == null) {
            return base;
        }

        var oldBase = instance.getBaseValue();
        try {
            instance.setBaseValue(base);
            return instance.getValue();
        } finally {
            instance.setBaseValue(oldBase);
        }
    }

    public static int[] getModVersion() {
        if (version != null) {
            return version;
        }
        version = ModList.get().getModContainerById(GunsmithLib.MOD_ID)
                .map(container -> extractModVersion(container.getModInfo().getVersion()))
                .map(version -> new int[]{version.getMajorVersion(), version.getMinorVersion(), version.getIncrementalVersion()})
                .orElse(new int[]{0, 0, 0});
        return version;
    }

    private static ArtifactVersion extractModVersion(ArtifactVersion fullVersion) {
        if (fullVersion.getMajorVersion() > 0) {
            return fullVersion;
        }
        return Arrays.stream(fullVersion.getQualifier().split("\\+")).findFirst()
                .<ArtifactVersion>map(DefaultArtifactVersion::new)
                .orElse(fullVersion);
    }

    public static Map<String, Object> parseStaticFields(Class<?> clazz) {
        Map<String, Object> constantMap = new LinkedHashMap<>();
        // 获取 GunAnimationConstant 的所有 public 字段
        Field[] fields = clazz.getFields();
        // 将 static final 的常量字段提取到 constantMap
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && Modifier.isFinal(field.getModifiers())) {
                try {
                    // 获取变量名和值
                    String name = field.getName();
                    Object value = field.get(null);
                    constantMap.put(name, value);
                } catch (IllegalAccessException ex) {
                    GunsmithLib.LOGGER.warn("Failed to parse static fields for class {}", clazz.getCanonicalName(), ex);
                }
            }
        }
        return constantMap;
    }

    private static int[] version = null;
    private GsHelper() {}
}
