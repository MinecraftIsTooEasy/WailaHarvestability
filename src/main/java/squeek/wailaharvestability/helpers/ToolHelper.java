package squeek.wailaharvestability.helpers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import net.minecraft.*;
import net.xiaoyu233.fml.FishModLoader;

public class ToolHelper {
	// tinkers construct
	private static Class<?> HarvestTool = null;
	private static Class<?> DualHarvestTool = null;
	private static Method getHarvestType = null;
	private static Method getSecondHarvestType = null;
	public static boolean tinkersConstructLoaded = false;

	// forge
	static HashMap<Item, List<Object>> toolClasses = null;

	@SuppressWarnings({"unchecked"})
	public static void init() {
//		try {
//			Field toolClassesField = ForgeHooks.class.getDeclaredField("toolClasses");
//			toolClassesField.setAccessible(true);
//			toolClasses = (HashMap<Item, List<Object>>) (toolClassesField.get(null));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		if (FishModLoader.hasMod("TConstruct")) {
			try {
				HarvestTool = Class.forName("tconstruct.library.tools.HarvestTool");
				DualHarvestTool = Class.forName("tconstruct.library.tools.DualHarvestTool");
				getHarvestType = HarvestTool.getDeclaredMethod("getHarvestType");
				getSecondHarvestType = DualHarvestTool.getDeclaredMethod("getSecondHarvestType");
				getHarvestType.setAccessible(true);
				getSecondHarvestType.setAccessible(true);
				tinkersConstructLoaded = true;
			} catch (ClassNotFoundException e) {
				return;
			} catch (NoSuchMethodException e) {
				return;
			}
		}
	}

	public static boolean hasToolTag(ItemStack itemStack) {
		return itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("InfiTool");
	}

	public static NBTTagCompound getToolTag(ItemStack tool) {
		NBTTagCompound tag = null;
		if (tool.hasTagCompound())
			tag = tool.getTagCompound().getCompoundTag("InfiTool");
		return tag;
	}

	public static int getPrimaryHarvestLevel(NBTTagCompound toolTag) {
		return toolTag.getInteger("HarvestLevel");
	}

	public static int getSecondaryHarvestLevel(NBTTagCompound toolTag) {
		return toolTag.getInteger("HarvestLevel2");
	}

	public static String getToolClassOf(Item tool) {
		List<Object> toolClass = toolClasses.get(tool);
		return toolClass != null ? (String) toolClass.get(0) : null;
	}

	public static String getToolClassOf(ItemStack tool) {
		return getToolClassOf(tool.getItem());
	}

	public static boolean isToolEffectiveAgainst(EntityPlayer player, RaycastCollision rc, ItemStack tool, Block block, int metadata, String effectiveToolClass) {
		Item item = tool.getItem();
		if (tinkersConstructLoaded && HarvestTool.isInstance(tool.getItem())) {
			List<String> harvestTypes = new ArrayList<String>();
			try {
				harvestTypes.add((String) getHarvestType.invoke(item));
			} catch (Exception e) {
				e.printStackTrace();
				tinkersConstructLoaded = false;
			}

			if (DualHarvestTool.isInstance(item)) {
				try {
					harvestTypes.add((String) getSecondHarvestType.invoke(item));
				} catch (Exception e) {
					e.printStackTrace();
					tinkersConstructLoaded = false;
				}
			}

			return harvestTypes.contains(effectiveToolClass);
		}
		String toolClass = null;
		return
//				tool.getItemAsTool().isEffectiveAgainstBlock(block, metadata) ||
//				Objects.equals(item.getAsTool().getToolType(), effectiveToolClass) ||
				tool.getStrVsBlock(block, metadata) > 1.5f;
	}

	public static boolean canToolHarvestLevel(ItemStack tool, Block block, int metadata, int harvestLevel) {
		boolean canTinkersToolHarvestBlock = false;

		NBTTagCompound toolTag = ToolHelper.getToolTag(tool);
		if (toolTag != null) {
			int toolHarvestLevel = Math.max(ToolHelper.getPrimaryHarvestLevel(toolTag), ToolHelper.getSecondaryHarvestLevel(toolTag));
			canTinkersToolHarvestBlock = toolHarvestLevel >= harvestLevel;
		}

		return canTinkersToolHarvestBlock || tool.getItem().canItemEditBlocks();
	}

	public static boolean canToolHarvestBlock(EntityPlayer player, RaycastCollision rc) {
		return player.getCurrentPlayerStrVsBlock(rc.block_hit_x, rc.block_hit_y, rc.block_hit_z, true) >= 0.0F;
	}

}
