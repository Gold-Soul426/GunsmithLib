package mod.chloeprime.gunsmithlib.client.laser;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.client.model.BedrockGunModel;
import com.tacz.guns.client.model.bedrock.BedrockPart;
import com.tacz.guns.client.resource.GunDisplayInstance;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.api.util.Rangefinder;
import mod.chloeprime.gunsmithlib.common.entity.MagicLaser;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class MagicLaserUtils {
    public static final double NANO_TO_SECOND = 1e-9;
    private static final Minecraft MC = Minecraft.getInstance();
    private static MagicLaser theChosenOne;

    public static Optional<MagicLaser> getInstance() {
        var localLevel = MC.level;
        if (localLevel == null) {
            theChosenOne = null;
        } else {
            if (theChosenOne == null || theChosenOne.level() != localLevel) {
                theChosenOne = new MagicLaser(localLevel);
            }
        }
        return Optional.ofNullable(theChosenOne);
    }

    public static Optional<LaserInstance> clip(LaserType type, Entity shooter, Vec3 pos, Vec3 direction, double range) {
        int piercing = 0;
        var result = Rangefinder.clip(shooter, pos, direction, piercing, range);
        return Optional.of(new LaserInstance(type, result.asHitResult().getLocation(), result.getLength(), pos, shooter));
    }

    public static void render(
            LaserInstance instance, Camera camera, float partial, PoseStack pose
    ) {
        render0(instance, camera, partial, pose);
    }

    private static void render0(
            LaserInstance instance, Camera camera, float partial, PoseStack pose
    ) {
        MagicLaser marker = getInstance().orElse(null);
        if (marker == null) {
            return;
        }
        marker.beginRendering(instance);

        var dispatcher = MC.getEntityRenderDispatcher();
        Vec3 cameraPosition = camera.getPosition();
        double x = instance.startPos.x() - cameraPosition.x();
        double y = instance.startPos.y() - cameraPosition.y();
        double z = instance.startPos.z() - cameraPosition.z();
        float yaw = Mth.lerp(partial, marker.yRotO, marker.getYRot());
        var buffer = MC.renderBuffers().bufferSource();
        var light = dispatcher.getPackedLightCoords(marker, partial);
        dispatcher.render(marker, x, y, z, yaw, partial, pose, buffer, light);

        marker.endRendering();
    }

    public static void stickLaserToMuzzle(LaserInstance instance, float partialTicks, PoseStack poseStack) {
        Entity shooter = instance.getShooter().orElse(null);
        if (MC.options.getCameraType() == CameraType.FIRST_PERSON && MC.getCameraEntity() instanceof LivingEntity fpEntity && fpEntity == shooter) {
            stickLaserToMuzzleFPP(instance, partialTicks, fpEntity);
        } else {
            stickLaserToMuzzleTPP(instance, partialTicks);
        }
    }

    static void stickLaserToMuzzleFPP(LaserInstance instance, float partial, LivingEntity fpEntity) {
        stickLaserToMuzzleTPP(instance, partial);
//        instance.startPos = fpEntity.getPosition(partial);
//        instance.hitLocation = instance.startPos.add(100, 0, 0);
    }

    private static void stickLaserToMuzzleTPP(LaserInstance instance, float partialTicks) {
        var shooter = instance.getShooter().orElse(null);
        if (shooter instanceof LivingEntity shoter) {
            instance.startPos = Gunsmith.getProximityMuzzlePos(shoter, partialTicks);
        }
    }

    static void getPreciseMuzzleOffset(GunDisplayInstance display, PoseStack pose) {
        if (display == null) {
            return;
        }
        BedrockGunModel model = display.getGunModel();
        List<BedrockPart> muzzle = model.getMuzzleFlashPosPath();
        if (muzzle == null) {
            return;
        }
        muzzle.forEach(bone -> bone.translateAndRotateAndScale(pose));
    }
}
