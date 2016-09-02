package nightkosh.gravestone_extended.structures.village.memorial;

import nightkosh.gravestone_extended.block.BlockMemorial;
import nightkosh.gravestone_extended.core.GSBlock;
import nightkosh.gravestone_extended.tileentity.TileEntityMemorial;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;

import java.util.List;
import java.util.Random;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class ComponentVillageMemorial extends StructureVillagePieces.Village {

    private int averageGroundLevel = -1;
    private static final int HEIGHT = 6;

    public ComponentVillageMemorial() {
    }

    public ComponentVillageMemorial(StructureVillagePieces.Start startPiece, int componentType, Random random, StructureBoundingBox structureBoundingBox, EnumFacing direction) {
        super(startPiece, componentType);
        this.coordBaseMode = direction;
        this.boundingBox = structureBoundingBox;
    }

    public static ComponentVillageMemorial buildComponent(StructureVillagePieces.Start startPiece, List list, Random random, int par3, int par4, int par5, EnumFacing direction, int componentType) {
        StructureBoundingBox structureBoundingBox = StructureBoundingBox.getComponentToAddBoundingBox(par3, par4, par5, 0, 0, 0, 5, HEIGHT, 5, direction);
        return canVillageGoDeeper(structureBoundingBox) && StructureComponent.findIntersecting(list, structureBoundingBox) == null ? new ComponentVillageMemorial(startPiece, componentType, random, structureBoundingBox, direction) : null;
    }

    /**
     * second Part of Structure generating, this for example places Spiderwebs,
     * Mob Spawners, it closes Mineshafts at the end, it adds Fences...
     */
    @Override
    public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, structureBoundingBox);

            if (this.averageGroundLevel < 0) {
                return true;
            }

            this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + HEIGHT - 2, 0);
        }

        IBlockState groundState;
        int biomeId = world.getBiomeGenForCoords(new BlockPos(this.getXWithOffset(0, 0), this.getYWithOffset(0), this.getZWithOffset(0, 0))).biomeID;
        if (biomeId == BiomeGenBase.desert.biomeID || biomeId == BiomeGenBase.desertHills.biomeID) {
            groundState = Blocks.sand.getDefaultState();
        } else {
            groundState = Blocks.grass.getDefaultState();
        }

        this.fillWithBlocks(world, structureBoundingBox, 0, -5, 0, 5, 0, 5, groundState, groundState, false);
        placeMemorial(world, random, 2, 1, 2);

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                this.clearCurrentPositionBlocksUpwards(world, x, HEIGHT, z, structureBoundingBox);
                this.setBlockState(world, groundState, x, -1, z, structureBoundingBox);
            }
        }

        return true;
    }

    protected void placeMemorial(World world, Random random, int x, int y, int z) {
        int memorialType;
//        boolean isTortureMemorial = random.nextInt(4) == 0;
        BlockPos pos = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));
//        if (isTortureMemorial) {//TODO !!!!!!!
//            memorialType = BlockMemorial.TORTURE_MEMORIALS[random.nextInt(BlockMemorial.TORTURE_MEMORIALS.length)].ordinal();
//        } else {
            memorialType = BlockMemorial.getMemorialType(world, pos, random, 0);
//        }

        IBlockState memorialState = GSBlock.memorial.getDefaultState().withProperty(BlockMemorial.FACING, this.coordBaseMode.getOpposite());
        this.setBlockState(world, memorialState, x, y, z, boundingBox);

        TileEntityMemorial tileEntity = (TileEntityMemorial) world.getTileEntity(pos);
        if (tileEntity != null) {
            tileEntity.setGraveType(memorialType);
//            if (isTortureMemorial) {
//                tileEntity.setRandomMob(random);
//            } else {
                tileEntity.setMemorialContent(random);
//            }
        }
    }

    @Override
    public NBTTagCompound createStructureBaseNBT() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("id", "GSVillageMemorial");
        nbttagcompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
        nbttagcompound.setInteger("O", this.coordBaseMode == null ? -1 : this.coordBaseMode.getHorizontalIndex());
        nbttagcompound.setInteger("GD", this.componentType);
        this.writeStructureToNBT(nbttagcompound);
        return nbttagcompound;
    }
}
