package nightkosh.gravestone_extended.structures.village.undertaker;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.List;
import java.util.Random;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
//TODO
//public class VillageHandlerGSUndertaker implements VillagerRegistry.IVillageCreationHandler, VillagerRegistry.IVillageTradeHandler {
public class VillageHandlerGSUndertaker implements VillagerRegistry.IVillageCreationHandler {

    public static final int UNDERTAKER_ID = 385;

//TODO
//    @Override
//    public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
//        // chisel
//        recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), new ItemStack(GSItem.chisel, 1, 0)));
//        // candle
//        recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), new ItemStack(GSBlock.candle, 10, 0)));
//        // skulls
//        recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 15), new ItemStack(Items.skull, 1, 0))); // skeleton
//        recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 15), new ItemStack(Items.skull, 1, 2))); // zombie
//        recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 20), new ItemStack(Items.skull, 1, 3))); // steve
//        recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 20), new ItemStack(Items.skull, 1, 4))); // creeper
//        recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 30), new ItemStack(Items.skull, 1, 1))); // wither
//    }

    @Override
    public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int size) {
        return new StructureVillagePieces.PieceWeight(ComponentVillageUndertaker.class, 3, MathHelper.getRandomIntegerInRange(random, 0, 1));
    }

    @Override
    public Class getComponentClass() {
        return ComponentVillageUndertaker.class;
    }

    @Override
    public Object buildComponent(StructureVillagePieces.PieceWeight villagePiece, StructureVillagePieces.Start startPiece, List pieces, Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
        return ComponentVillageUndertaker.buildComponent(startPiece, pieces, random, p1, p2, p3, facing, p5);
    }
}
