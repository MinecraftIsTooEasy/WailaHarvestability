package squeek.wailaharvestability.helpers;

import java.util.HashMap;

import net.minecraft.*;
import squeek.wailaharvestability.proxy.ProxyIguanaTweaks;

public class BlockHelper {
    private static final HashMap<String, ItemStack> testTools = new HashMap<String, ItemStack>();

    static {
        testTools.put("pickaxe", new ItemStack(Item.pickaxeRustedIron));
        testTools.put("shovel", new ItemStack(Item.shovelWood));
        testTools.put("axe", new ItemStack(Item.axeRustedIron));
    }

    public static boolean getHarvestLevelsOf(World world, int x, int y, int z, Block block, int metadata, String[] toolClasses, int[] harvestLevels) {
        RaycastCollision raycastCollision = Minecraft.getMinecraft().objectMouseOver;
        if (raycastCollision == null)
            return false;
        int i = 0;
        boolean hasEffectiveTools = false;
        boolean isGravelOre = ProxyIguanaTweaks.isGravelOre(block);
        for (String toolClass : toolClasses) {
            harvestLevels[i] = isGravelOre && toolClass.equals("pickaxe") ? -1 : raycastCollision.getBlockHit().getMinHarvestLevel(metadata);

            if (harvestLevels[i] != -1)
                hasEffectiveTools = true;
            else {
                float hardness = block.getBlockHardness(metadata);
                if (hardness > 0f) {
                    ItemStack testTool = testTools.get(toolClass);
                    if (testTool != null && testTool.getItem() instanceof ItemTool && testTool.getStrVsBlock(block, metadata) >= ((ItemTool) testTool.getItem()).getMaterialHarvestEfficiency()) {
                        harvestLevels[i] = 0;
                        hasEffectiveTools = true;
                    }
                }
            }

            i++;
        }
        return hasEffectiveTools;
    }

    public static boolean isBlockUnbreakable(Block block, World world, int x, int y, int z) {
        return block.getBlockHardness(world.getBlockMetadata(x, y, z)) == -1.0f;
    }

}
