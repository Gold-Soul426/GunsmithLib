package mod.chloeprime.gunsmithlib.common.gunpack_extension.gun;

import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public interface EnhancedGunData {
    Optional<GunsmithLibGunDataExtension> gunsmith$getGunsmithLibExtension();
}
