package mod.chloeprime.gunsmithlib.client.laser;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tacz.guns.entity.EntityKineticBullet;
import com.tacz.guns.util.EntityUtil;
import mod.chloeprime.gunsmithlib.GunsmithLib;
import mod.chloeprime.gunsmithlib.api.util.Gunsmith;
import mod.chloeprime.gunsmithlib.common.internal.MagicLaser;
import mod.chloeprime.gunsmithlib.proxies.ClientProxy;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

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
        Vec3 hitLocation;
        double length;

        var laser = getInstance().orElse(null);
        if (laser == null) {
            return Optional.empty();
        }
        var ctx = new ClipContext(pos, pos.add(direction.scale(range)), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, shooter);
        int piercing = 0;
        int entityLimit = piercing + 1;

        var block = shooter.level().clip(ctx);
        var blockLengthSqr = block.getLocation().distanceToSqr(ctx.getFrom());
        var entity = clipEntities(laser, shooter, pos, pos.add(direction.scale(Math.sqrt(blockLengthSqr) + 0.125)), entityLimit);

        // 什么也没打中
        if (block.getType() == HitResult.Type.MISS && entity.isEmpty()) {
            length = (float) range;
            hitLocation = pos.add(direction.scale(range));
            return Optional.of(new LaserInstance(type, hitLocation, length, pos, shooter));
        }
        // 伤害实体并统计数量
        var furthestHit = new MutableObject<HitResult>(null);
        var lengthSqrByEntity = new MutableDouble(0);
        long entityCount = entity.stream().mapToLong(stream -> stream
                .filter(result -> result.getEntity() != shooter)
                .map(result -> new EntityHitResult(result.getEntity(), result.getLocation()))
                .filter(hit -> {
                    var lengthSqr = hit.getLocation().distanceToSqr(ctx.getFrom());
                    // 防止穿墙射击
                    if (lengthSqr > blockLengthSqr + 0.125) {
                        return true;
                    }
                    if (lengthSqr > lengthSqrByEntity.doubleValue()) {
                        lengthSqrByEntity.setValue(lengthSqr);
                        furthestHit.setValue(hit);
                    }
                    return true;
                })
                .count()
        ).findAny().orElse(0);
        // 击中方块
        if (entityCount < entityLimit && block.getType() != HitResult.Type.MISS) {
            hitLocation = block.getLocation();
        } else {
            hitLocation = Optional.ofNullable(furthestHit.getValue())
                    .map(HitResult::getLocation)
                    .orElse(pos);
        }
        length = hitLocation.distanceTo(ctx.getFrom());
        return Optional.of(new LaserInstance(type, hitLocation, length, pos, shooter));
    }

    private static Optional<Stream<EntityHitResult>> clipEntities(Projectile projectile, Entity shooter, Vec3 from, Vec3 to, int limit) {
        projectile.setPos(from);
        if (limit == 1) {
            return Optional.ofNullable(EntityUtil.findEntityOnPath(projectile, from, to))
                    .map(MagicLaserUtils::mapResult)
                    .map(Stream::of);
        }
        return Optional.of(EntityUtil.findEntitiesOnPath(projectile, from, to)).map(list -> list.stream()
                // 阻止伤害到自己和自己的坐骑
                .filter(result -> result.getEntity() != shooter && result.getEntity() != shooter.getVehicle())
                // 按距离排序
                .sorted(Comparator.comparing(result -> result.getHitPos().distanceToSqr(from)))
                .limit(limit)
                .map(MagicLaserUtils::mapResult)
        );
    }

    private static EntityHitResult mapResult(EntityKineticBullet.EntityResult result) {
        return new EntityHitResult(result.getEntity(), result.getHitPos());
    }

    public static void render(
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

    public static void stickLaserToMuzzle(LaserInstance instance, float partialTicks) {
        var shooter = instance.getShooter().orElse(null);
        if (shooter instanceof LivingEntity shoter) {
            instance.startPos = Gunsmith.getProximityMuzzlePos(shoter, partialTicks);
        }
    }
}
