package squeek.wailaharvestability;

import java.util.ArrayList;
import java.util.List;

import fi.dy.masa.malilib.config.ConfigTab;
import fi.dy.masa.malilib.config.SimpleConfigs;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigString;

public class WailaHarvestabilityConfig extends SimpleConfigs {

	public static final ConfigString MINIMAL_SEPARATOR_STRING_NAME = new ConfigString("minimal.mode.separator", " : ");
	public static final ConfigString CURRENTLY_HARVESTABLE_STRING_NAME = new ConfigString("is.currently.harvestable.string", "\u2714");
	public static final ConfigString NOT_CURRENTLY_HARVESTABLE_STRING_NAME = new ConfigString("not.currently.harvestable.string", "\u2718");
	public static final ConfigString SHEARABILITY_STRING_NAME = new ConfigString("shearability.string", "\u2702");
	public static final ConfigString SILK_TOUCHABILITY_STRING_NAME = new ConfigString("silk.touchability.string", "\u2712");

	public static final ConfigBoolean harvestlevel = new ConfigBoolean("harvestability.harvestlevel", true);
	public static final ConfigBoolean effectivetool = new ConfigBoolean("harvestability.effectivetool", true);
	public static final ConfigBoolean currentlyharvestable = new ConfigBoolean("harvestability.currentlyharvestable", true);
	public static final ConfigBoolean harvestlevel_sneakingonly = new ConfigBoolean("harvestability.harvestlevel.sneakingonly", false);
	public static final ConfigBoolean effectivetool_sneakingonly = new ConfigBoolean("harvestability.effectivetool.sneakingonly", false);
	public static final ConfigBoolean currentlyharvestable_sneakingonly = new ConfigBoolean("harvestability.currentlyharvestable.sneakingonly", false);
	public static final ConfigBoolean oresonly = new ConfigBoolean("harvestability.oresonly", false);
	public static final ConfigBoolean minimal = new ConfigBoolean("harvestability.minimal", true);
	public static final ConfigBoolean unharvestableonly = new ConfigBoolean("harvestability.unharvestableonly", false);
	public static final ConfigBoolean toolrequiredonly = new ConfigBoolean("harvestability.toolrequiredonly", true);
	public static final ConfigBoolean shearability = new ConfigBoolean("harvestability.shearability", true);
	public static final ConfigBoolean shearability_sneakingonly = new ConfigBoolean("harvestability.shearability.sneakingonly", true);
	public static final ConfigBoolean silktouchability = new ConfigBoolean("harvestability.silktouchability", true);
	public static final ConfigBoolean silktouchability_sneakingonly = new ConfigBoolean("harvestability.silktouchability.sneakingonly", true);


	private static WailaHarvestabilityConfig Instance;
	public static List<ConfigBase> string;
	public static List<ConfigBase> main;

	public static final List<ConfigTab> tabs = new ArrayList<>();

	public WailaHarvestabilityConfig() {
		super("Waila Harvestability", null, main);
	}

	static {
		main = List.of(harvestlevel, effectivetool, currentlyharvestable, harvestlevel_sneakingonly, effectivetool_sneakingonly,
				currentlyharvestable_sneakingonly, oresonly, minimal, unharvestableonly, toolrequiredonly, shearability, shearability_sneakingonly,
				silktouchability, silktouchability_sneakingonly);
		string = List.of(MINIMAL_SEPARATOR_STRING_NAME, CURRENTLY_HARVESTABLE_STRING_NAME, NOT_CURRENTLY_HARVESTABLE_STRING_NAME, SHEARABILITY_STRING_NAME, SILK_TOUCHABILITY_STRING_NAME);
		ArrayList<ConfigBase> values = new ArrayList<>();
		values.addAll(string);
		values.addAll(main);
		tabs.add(new ConfigTab("wailaharvestability.main", main));
		tabs.add(new ConfigTab("wailaharvestability.string", string));
		Instance = new WailaHarvestabilityConfig();
	}

	@Override
	public List<ConfigTab> getConfigTabs() {
		return tabs;
	}

	public static WailaHarvestabilityConfig getInstance() {
		return Instance;
	}
}
