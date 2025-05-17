package mod.chloeprime.gunsmithlib.common.gunpack_extension.attachment;

import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;

@ApiStatus.Internal
public interface EnhancedAttachmentData {
    Optional<GunsmithLibAttachmentDataExtension> gunsmith$getGunsmithLibExtension();
}
