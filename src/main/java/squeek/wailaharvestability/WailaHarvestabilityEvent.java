package squeek.wailaharvestability;

import moddedmite.rustedironcore.api.event.Handlers;
import moddedmite.rustedironcore.api.event.listener.IInitializationListener;
import net.minecraft.Minecraft;

public class WailaHarvestabilityEvent extends Handlers {
    public static void register() {
        Initialization.register(new IInitializationListener() {
            @Override
            public void onClientStarted(Minecraft client) {
                //safer register waila plugin
                //you can also mixin Minecraft.class
                WailaHarvestabilityHandler.register();
            }
        });
    }
}
