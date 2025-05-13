package mod.chloeprime.gunsmithlib.api.util;

import com.tacz.guns.api.item.IAttachment;
import com.tacz.guns.resource.index.CommonAttachmentIndex;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * @see Gunsmith#getAttachmentInfo(ItemStack)
 */
public record AttachmentInfo(
        ItemStack attachmentStack,
        IAttachment attachmentItem,
        ResourceLocation attachmentId,
        CommonAttachmentIndex index
) {
}
