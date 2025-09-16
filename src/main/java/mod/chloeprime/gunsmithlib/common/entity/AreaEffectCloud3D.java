package mod.chloeprime.gunsmithlib.common.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.joml.Vector3d;

import javax.annotation.Nonnull;

public class AreaEffectCloud3D extends AreaEffectCloud {
    public static final EntityType<AreaEffectCloud3D> TYPE = EntityType.Builder
            .<AreaEffectCloud3D>of(AreaEffectCloud3D::new, MobCategory.MISC)
            .fireImmune()
            .sized(6, 6)
            .clientTrackingRange(10)
            .updateInterval(Integer.MAX_VALUE)
            .build("area_effect_cloud_3d");

    public AreaEffectCloud3D(EntityType<? extends AreaEffectCloud> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.setWaitTime(0);
    }

    public AreaEffectCloud3D(Level level, double x, double y, double z) {
        this(TYPE, level);
        this.setPos(x, y, z);
    }

    public static AreaEffectCloud3D createAtCenter(Level level, double x, double y, double z, float radius) {
        var cloud = new AreaEffectCloud3D(level, x, y - radius, z);
        cloud.setRadius(radius);
        return cloud;
    }

    @Override
    public @Nonnull EntityDimensions getDimensions(@Nonnull Pose pPose) {
        var length = getRadius() * 2;
        return EntityDimensions.scalable(length, length);
    }

    public RandomSource getRandom() {
        return this.random;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide) {
            return;
        }

        var waiting = this.isWaiting();
        var dataRadius = this.getRadius();
        if (waiting && this.random.nextBoolean()) {
            return;
        }

        var particle = this.getParticle();
        int count;
        float radius;
        if (waiting) {
            count = 2;
            radius = 0.2F;
        } else {
            // 球体的公式是(4/3)πr³，但是这个公式计算出来的粒子数量太多了，
            // 所以这里把前面的系数改小点（除以6），让半径为6时2D公式的粒子数量和3D公式的粒子数量相等
            count = Mth.ceil((1.0 / 6) * Math.PI * dataRadius * dataRadius * dataRadius);
            radius = dataRadius;
        }

        for(int i = 0; i < count; ++i) {
            Vector3d angle = randomUnitVector();
            double distance = Math.pow(this.random.nextFloat(), 0.7) * radius;
            double x = this.getX() + angle.x() * distance;
            double y = this.getY() + angle.y() * distance + this.getBbHeight() / 2;
            double z = this.getZ() + angle.z() * distance;
            double dx;
            double dy;
            double dz;
            if (particle.getType() == ParticleTypes.ENTITY_EFFECT) {
                int color = waiting && this.random.nextBoolean() ? 0xFFFFFFFF : this.getColor();
                dx = (color >> 16 & 255) / 255.0F;
                dy = (color >> 8 & 255) / 255.0F;
                dz = (color & 255) / 255.0F;
            } else if (waiting) {
                dx = 0;
                dy = 0;
                dz = 0;
            } else {
                dx = (0.5 - this.random.nextDouble()) * 0.15;
                dy = 0.01;
                dz = (0.5 - this.random.nextDouble()) * 0.15;
            }

            this.level().addAlwaysVisibleParticle(particle, x, y, z, dx, dy, dz);
        }
    }

    private static final Vector3d RANDOM_UNIT_BUFFER = new Vector3d();

    private Vector3d randomUnitVector() {
        RANDOM_UNIT_BUFFER.x = this.random.nextGaussian();
        RANDOM_UNIT_BUFFER.y = this.random.nextGaussian();
        RANDOM_UNIT_BUFFER.z = this.random.nextGaussian();
        RANDOM_UNIT_BUFFER.normalize();
        return RANDOM_UNIT_BUFFER;
    }
}
