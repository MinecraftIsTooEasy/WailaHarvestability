package squeek.wailaharvestability;

import fi.dy.masa.malilib.config.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.xiaoyu233.fml.FishModLoader;
import net.xiaoyu233.fml.ModResourceManager;
import squeek.wailaharvestability.helpers.ToolHelper;
import squeek.wailaharvestability.proxy.ProxyIguanaTweaks;
import squeek.wailaharvestability.setup.MissingHarvestInfo;

//@Mod(modid = ModInfo.MODID, version = ModInfo.VERSION, dependencies = "after:TConstruct;after:ExtraTiC;after:TSteelworks;after:Mariculture")
public class ModWailaHarvestability implements ClientModInitializer {
	public static boolean hasIguanaTweaks;

	@Override
	public void onInitializeClient() {
		ModResourceManager.addResourcePackDomain("wailaharvestability");
		WailaHarvestabilityEvent.register();
		WailaHarvestabilityConfig.getInstance().load();
		ConfigManager.getInstance().registerConfig(WailaHarvestabilityConfig.getInstance());
		MissingHarvestInfo.init();
		if (FishModLoader.hasMod("IguanaTweaksTConstruct")) {
			hasIguanaTweaks = true;
			ProxyIguanaTweaks.init();
		}
		ToolHelper.init();
	}

}
