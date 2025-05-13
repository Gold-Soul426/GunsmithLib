package mod.chloeprime.gunsmithlib.api.util;

import com.tacz.guns.api.item.IAmmo;
import com.tacz.guns.resource.index.CommonAmmoIndex;
import com.tacz.guns.resource.index.CommonAttachmentIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record AmmoInfo(
        ItemStack ammoStack,
        IAmmo ammoItem,
        ResourceLocation ammoId,
        CommonAmmoIndex index
) {
}
