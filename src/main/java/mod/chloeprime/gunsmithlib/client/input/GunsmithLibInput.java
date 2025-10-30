package mod.chloeprime.gunsmithlib.client.input;

import mod.chloeprime.gunsmithlib.GunsmithLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class GunsmithLibInput {
    public static final String CATEGORY = "key.category.%s".formatted(GunsmithLib.MOD_ID);

    public enum KeyConflictContexts implements IKeyConflictContext {
        IN_GAME_CONCURRENT {
            @Override
            public boolean isActive() {
                return KeyConflictContext.IN_GAME.isActive();
            }

            @Override
            public boolean conflicts(IKeyConflictContext other) {
                return false;
            }
        }
    }

    @SubscribeEvent
    public static void onRegisteringKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(BallisticComputerKey.KEY_MAPPING);
    }
}
