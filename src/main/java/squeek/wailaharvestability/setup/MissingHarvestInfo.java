package squeek.wailaharvestability.setup;

import net.minecraft.Block;
public class MissingHarvestInfo {
	public static void init() {
		vanilla();
	}

	public static void vanilla() {
		Block.web.setMinHarvestLevel(0);
	}
}
