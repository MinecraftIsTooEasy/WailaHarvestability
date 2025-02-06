package squeek.wailaharvestability;

import java.util.*;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.*;
import squeek.wailaharvestability.helpers.BlockHelper;
import squeek.wailaharvestability.helpers.ColorHelper;
import squeek.wailaharvestability.helpers.OreHelper;
import squeek.wailaharvestability.helpers.StringHelper;
import squeek.wailaharvestability.helpers.ToolHelper;

public class WailaHandler implements IWailaDataProvider {
	@Override
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return null;
	}

	@Override
	public List<String> getWailaHead(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return toolTip;
	}

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		Block block = accessor.getBlock();
		int meta = accessor.getMetadata();
		EntityPlayer player = accessor.getPlayer();

		// for disguised blocks
		if (itemStack.getItem() instanceof ItemBlock) {
			block = Block.blocksList[itemStack.itemID];
			meta = itemStack.getItemDamage();
		}

		boolean minimalLayout = WailaHarvestabilityConfig.minimal.getBooleanValue();

		List<String> stringParts = new ArrayList<String>();
		getHarvestability(stringParts, player, block, meta, accessor.getPosition(), config, minimalLayout);

		if (!stringParts.isEmpty()) {
			if (minimalLayout)
				toolTip.add(StringHelper.concatenateStringList(stringParts, EnumChatFormatting.RESET + WailaHarvestabilityConfig.MINIMAL_SEPARATOR_STRING_NAME.getStringValue()));
			else
				toolTip.addAll(stringParts);
		}

		return toolTip;
	}

	public void getHarvestability(List<String> stringList, EntityPlayer player, Block block, int meta, RaycastCollision position, IWailaConfigHandler config, boolean minimalLayout) {
		boolean isSneaking = player.isSneaking();
		boolean showHarvestLevel = WailaHarvestabilityConfig.harvestlevel.getBooleanValue() && (!WailaHarvestabilityConfig.harvestlevel_sneakingonly.getBooleanValue() || isSneaking);
		boolean showEffectiveTool = WailaHarvestabilityConfig.effectivetool.getBooleanValue() && (!WailaHarvestabilityConfig.effectivetool_sneakingonly.getBooleanValue() || isSneaking);
		boolean showCurrentlyHarvestable = WailaHarvestabilityConfig.currentlyharvestable.getBooleanValue() && (!WailaHarvestabilityConfig.currentlyharvestable_sneakingonly.getBooleanValue() || isSneaking);
		boolean hideWhileHarvestable = WailaHarvestabilityConfig.unharvestableonly.getBooleanValue();
		boolean showOresOnly = WailaHarvestabilityConfig.oresonly.getBooleanValue();
		boolean toolRequiredOnly = WailaHarvestabilityConfig.toolrequiredonly.getBooleanValue();

		if (showHarvestLevel || showEffectiveTool || showCurrentlyHarvestable) {
			if (showOresOnly && !OreHelper.isBlockAnOre(block, meta)) {
				return;
			}

			if (BlockHelper.isBlockUnbreakable(block, player.worldObj, position.block_hit_x, position.block_hit_y, position.block_hit_z)) {
				String unbreakableString = ColorHelper.getBooleanColor(false) + WailaHarvestabilityConfig.NOT_CURRENTLY_HARVESTABLE_STRING_NAME.getStringValue() + (!minimalLayout ? EnumChatFormatting.RESET + StatCollector.translateToLocal("wailaharvestability.harvestable") : "");
				stringList.add(unbreakableString);
				return;
			}

			String[] toolClasses = new String[]{"pickaxe", "shovel", "axe", "sword"};
			int[] harvestLevels = new int[toolClasses.length];
			boolean blockHasEffectiveTools = BlockHelper.getHarvestLevelsOf(player.worldObj, position.block_hit_x, position.block_hit_y, position.block_hit_z, block, meta, toolClasses, harvestLevels);

			String shearability = getShearabilityString(player, block, meta, position, config);
			String silkTouchability = getSilkTouchabilityString(player, block, meta, position, config);

			if (toolRequiredOnly && block.blockMaterial.requiresTool(block, meta) && !blockHasEffectiveTools && shearability.isEmpty() && silkTouchability.isEmpty())
				return;

			int harvestLevel = -1;
			String effectiveTool = "All/None";
			int i = 0;
			for (String toolClass : toolClasses) {
				if (harvestLevels[i] >= 0) {
					harvestLevel = harvestLevels[i];
//					effectiveTool = toolClass;
					break;
				}
				i++;
			}

			for (int j = 0; j < Item.itemsList.length; ++j) {
				Item item = Item.getItem(j);
				if (item instanceof ItemTool tool && tool.isEffectiveAgainstBlock(block, meta)) {
					effectiveTool = tool.getToolType();
				}
			}

			boolean canHarvest = false;
			boolean isEffective = false;
			boolean isAboveMinHarvestLevel = false;
			boolean isHoldingTinkersTool = false;

			ItemStack itemHeld = player.getHeldItemStack();
			if (itemHeld != null) {
				isHoldingTinkersTool = ToolHelper.hasToolTag(itemHeld);
				canHarvest = ToolHelper.canToolHarvestBlock(itemHeld, block, meta) || (!isHoldingTinkersTool && this.canSilkHarvest(block));
				isAboveMinHarvestLevel = (showCurrentlyHarvestable || showHarvestLevel) && ToolHelper.canToolHarvestLevel(itemHeld, block, meta, 0);
				isEffective = showEffectiveTool && ToolHelper.isToolEffectiveAgainst(itemHeld, block, meta, effectiveTool);
			}

			boolean isCurrentlyHarvestable = (canHarvest && isAboveMinHarvestLevel);

			if (hideWhileHarvestable && isCurrentlyHarvestable)
				return;

			String currentlyHarvestable = showCurrentlyHarvestable ? ColorHelper.getBooleanColor(isCurrentlyHarvestable) + (isCurrentlyHarvestable ? WailaHarvestabilityConfig.CURRENTLY_HARVESTABLE_STRING_NAME.getStringValue() : WailaHarvestabilityConfig.NOT_CURRENTLY_HARVESTABLE_STRING_NAME.getStringValue()) + (!minimalLayout ? EnumChatFormatting.RESET + StatCollector.translateToLocal("wailaharvestability.currentlyharvestable") : "") : "";

			if (!currentlyHarvestable.isEmpty() || !shearability.isEmpty() || !silkTouchability.isEmpty()) {
				String separator = (!shearability.isEmpty() || !silkTouchability.isEmpty() ? " " : "");
				stringList.add(currentlyHarvestable + separator + silkTouchability + (!silkTouchability.isEmpty() ? separator : "") + shearability);
			}

			if (harvestLevel != -1 && showEffectiveTool) {
				String effectiveToolString;
				if (StatCollector.func_94522_b("wailaharvestability.toolclass." + effectiveTool))
					effectiveToolString = StatCollector.translateToLocal("wailaharvestability.toolclass." + effectiveTool);
				else
					effectiveToolString = effectiveTool.substring(0, 1).toUpperCase() + effectiveTool.substring(1);
				stringList.add((!minimalLayout ? StatCollector.translateToLocal("wailaharvestability.effectivetool") : "") + ColorHelper.getBooleanColor(isEffective && (!isHoldingTinkersTool || canHarvest), isHoldingTinkersTool && isEffective && !canHarvest) + effectiveToolString);
			}
			if (harvestLevel >= 1 && showHarvestLevel)
				stringList.add((!minimalLayout ? StatCollector.translateToLocal("wailaharvestability.harvestlevel") : "") + ColorHelper.getBooleanColor(isAboveMinHarvestLevel && canHarvest) + StringHelper.stripFormatting(StringHelper.getHarvestLevelName(harvestLevel)));
		}
	}

	public String getShearabilityString(EntityPlayer player, Block block, int meta, RaycastCollision position, IWailaConfigHandler config) {
		boolean isSneaking = player.isSneaking();
		boolean showShearability = WailaHarvestabilityConfig.shearability.getBooleanValue() && (!WailaHarvestabilityConfig.shearability_sneakingonly.getBooleanValue() || isSneaking);
		for (int j = 0; j < Item.itemsList.length; ++j) {
			Item item = Item.getItem(j);
			if (item instanceof ItemShears itemShears) {
				if (showShearability && (block.canSilkHarvest(position.block_hit_metadata) && itemShears.isEffectiveAgainstBlock(block, meta) || block == Block.deadBush)) {
					ItemStack itemHeld = player.getHeldItemStack();
					boolean isHoldingShears = itemHeld != null && itemHeld.getItem() instanceof ItemShears;
					boolean isShearable = isHoldingShears && block.canSilkHarvest(position.block_hit_metadata);
					return ColorHelper.getBooleanColor(isShearable, !isShearable && isHoldingShears) + WailaHarvestabilityConfig.SHEARABILITY_STRING_NAME.getStringValue();
				}
			}
		}
		return "";
	}

	public String getSilkTouchabilityString(EntityPlayer player, Block block, int meta, RaycastCollision position, IWailaConfigHandler config) {
		boolean isSneaking = player.isSneaking();
		boolean showSilkTouchability = WailaHarvestabilityConfig.silktouchability.getBooleanValue() && (!WailaHarvestabilityConfig.silktouchability_sneakingonly.getBooleanValue() || isSneaking);

		if (showSilkTouchability && this.canSilkHarvest(block)) {
			boolean hasSilkTouch = EnchantmentHelper.getSilkTouchModifier(player);
			return ColorHelper.getBooleanColor(hasSilkTouch) + WailaHarvestabilityConfig.SILK_TOUCHABILITY_STRING_NAME.getStringValue();
		}
		return "";
	}

	@Override
	public List<String> getWailaTail(ItemStack itemStack, List<String> toolTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return toolTip;
	}

	@Override
	public NBTTagCompound getNBTData(ServerPlayer serverPlayer, TileEntity tileEntity, NBTTagCompound nbtTagCompound, World world, int i, int i1, int i2) {
		return null;
	}

	private boolean canSilkHarvest(Block block) {
		return block.renderAsNormalBlock() && !block.hasTileEntity();
	}

	public static void callbackRegister(IWailaRegistrar registrar) {
		WailaHandler instance = new WailaHandler();
		registrar.registerBodyProvider(instance, Block.class);
	}
}