package squeek.wailaharvestability.mixin;

import mcp.mobius.waila.api.impl.ModuleRegistrar;
import mcp.mobius.waila.handlers.HUDHandlerEntities;
import net.minecraft.Block;
import net.minecraft.Entity;
import net.minecraft.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import squeek.wailaharvestability.WailaHandler;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/ReferenceFileWriter;write()V"))
    private void onWorldUnload(CallbackInfo ci) {
//        WailaHandler.register();
        WailaHandler.callbackRegister(ModuleRegistrar.instance());
    }
}
