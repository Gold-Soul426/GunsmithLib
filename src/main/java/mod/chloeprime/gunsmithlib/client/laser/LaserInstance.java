package mod.chloeprime.gunsmithlib.client.laser;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.Random;
import java.util.random.RandomGenerator;

@ApiStatus.Internal
public class LaserInstance {
    public final LaserType type;
    public Vec3 hitLocation;
    public Vec3 startPos;
    public float length;
    public final float roll;
    public long localSpawnTime;
    public final WeakReference<Entity> shooter;

    private static final RandomGenerator RNG = new Random();

    public LaserInstance(LaserType type, Vec3 hitLocation, double length, Vec3 startPos, Entity shooter) {
        this.type = type;
        this.length = (float) length;
        this.hitLocation = hitLocation;
        this.startPos = startPos;
        this.roll = Mth.TWO_PI * RNG.nextFloat();
        this.localSpawnTime = System.nanoTime() + 900_000_000;
        this.shooter = new WeakReference<>(shooter);
    }

    public void refresh(float partial) {
        getShooter()
                .flatMap(shooter -> MagicLaserUtils.clip(type, shooter, shooter.getEyePosition(partial), shooter.getViewVector(partial), 512))
                .ifPresent(this::refresh0);
    }

    private void refresh0(LaserInstance newed) {
        this.hitLocation = newed.hitLocation;
        this.startPos = newed.startPos;
        this.length = newed.length;
    }

    public Optional<Entity> getShooter() {
        return Optional.ofNullable(shooter.get());
    }
}
