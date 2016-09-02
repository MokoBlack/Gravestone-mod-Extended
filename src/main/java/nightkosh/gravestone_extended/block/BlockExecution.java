package nightkosh.gravestone_extended.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nightkosh.gravestone.inventory.GraveInventory;
import nightkosh.gravestone_extended.block.enums.EnumExecution;
import nightkosh.gravestone_extended.block.enums.EnumHangedMobs;
import nightkosh.gravestone_extended.core.Tabs;
import nightkosh.gravestone_extended.item.ItemCorpse;
import nightkosh.gravestone_extended.item.corpse.VillagerCorpseHelper;
import nightkosh.gravestone_extended.item.enums.EnumCorpse;
import nightkosh.gravestone_extended.particle.EntityBigFlameFX;
import nightkosh.gravestone_extended.tileentity.TileEntityExecution;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class BlockExecution extends BlockContainer {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockExecution() {
        super(Material.rock);
        this.isBlockContainer = true;
        this.setStepSound(Block.soundTypeWood);
        this.setHardness(1);
        this.setResistance(5);
        this.setCreativeTab(Tabs.memorialsTab);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityExecution();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess access, BlockPos pos) {
        EnumFacing facing = (EnumFacing) access.getBlockState(pos).getValue(FACING);
        EnumExecution executionBlockType;
        TileEntityExecution tileEntity = (TileEntityExecution) access.getTileEntity(pos);

        if (tileEntity != null) {
            executionBlockType = tileEntity.getExecutionType();
        } else {
            executionBlockType = EnumExecution.GIBBET;
        }

        switch (executionBlockType) {
            case GIBBET:
            case BURNING_STAKE:
                this.setBlockBounds(0, 0, 0, 1, 2.5F, 1);
                break;
            case STOCKS:
                switch (facing) {
                    case SOUTH:
                    case NORTH:
                        this.setBlockBounds(-0.5F, 0, 0, 1.5F, 2, 1);
                        break;
                    case EAST:
                    case WEST:
                        this.setBlockBounds(0, 0, -0.5F, 1, 2, 1.5F);
                        break;
                }
                break;
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0, 0, 0, 1, 1, 2);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntityExecution te = (TileEntityExecution) world.getTileEntity(pos);

        if (te != null) {
            ItemStack item = player.inventory.getCurrentItem();
            if (item != null && item.getItem() instanceof ItemCorpse && EnumCorpse.getById((byte) item.getItemDamage()).equals(EnumCorpse.VILLAGER) && te.getHangedMob() == EnumHangedMobs.NONE) {
                te.setHangedMob(EnumHangedMobs.VILLAGER);
                te.setHangedVillagerProfession(VillagerCorpseHelper.getVillagerType(item.getTagCompound()));
                item.stackSize--;
                return true;
            }
        }

        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        for (EnumExecution executionBlock : EnumExecution.values()) {
            for (byte mobType = 0; mobType < EnumHangedMobs.values().length; mobType++) {
                ItemStack stack = new ItemStack(item, 1, executionBlock.ordinal());
                if (!stack.hasTagCompound()) {
                    stack.setTagCompound(new NBTTagCompound());
                }
                stack.getTagCompound().setByte("HangedMob", mobType);
                switch (EnumHangedMobs.values()[mobType]) {
                    case VILLAGER:
                        ItemStack villagerStack;
                        for (byte villagerProfession = 0; villagerProfession <= 4; villagerProfession++) {
                            villagerStack = stack.copy();
                            villagerStack.getTagCompound().setInteger("HangedVillagerProfession", villagerProfession);
                            list.add(villagerStack);
                        }

                        Collection<Integer> villagerIds = VillagerRegistry.getRegisteredVillagers();
                        for (Integer villagerId : villagerIds) {
                            villagerStack = stack.copy();
                            villagerStack.getTagCompound().setInteger("HangedVillagerProfession", villagerId);
                            list.add(villagerStack);
                        }
                        break;
                    default:
                        list.add(stack);
                }

            }
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        player.addExhaustion(0.025F);

        ItemStack itemStack;
        if (EnchantmentHelper.getSilkTouchModifier(player)) {
            itemStack = getBlockItemStack(world, pos);
        } else {
            itemStack = getBlockItemStackWithoutInfo(world, pos);
        }

        if (itemStack != null) {
            GraveInventory.dropItem(itemStack, world, pos);
        }
    }

    private ItemStack getBlockItemStack(World world, BlockPos pos) {
        ItemStack itemStack = this.createStackedBlock(this.getDefaultState());
        TileEntityExecution tileEntity = (TileEntityExecution) world.getTileEntity(pos);

        if (tileEntity != null) {
            itemStack.setItemDamage(tileEntity.getExecutionTypeNum());
            NBTTagCompound nbt = new NBTTagCompound();

            nbt.setByte("HangedMob", (byte) tileEntity.getHangedMob().ordinal());
            nbt.setInteger("HangedVillagerProfession", tileEntity.getHangedVillagerProfession());

            itemStack.setTagCompound(nbt);
        }

        return itemStack;
    }

    private ItemStack getBlockItemStackWithoutInfo(World world, BlockPos pos) {
        ItemStack itemStack = this.createStackedBlock(this.getDefaultState());
        TileEntityExecution tileEntity = (TileEntityExecution) world.getTileEntity(pos);

        if (tileEntity != null) {
            itemStack.setItemDamage(tileEntity.getExecutionTypeNum());
        }

        return itemStack;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player) {
        ItemStack itemStack = this.createStackedBlock(this.getDefaultState());
        TileEntityExecution tileEntity = (TileEntityExecution) world.getTileEntity(pos);

        if (tileEntity != null) {
            if (itemStack != null) {
                itemStack.setItemDamage(tileEntity.getExecutionTypeNum());
                NBTTagCompound nbt = new NBTTagCompound();

                nbt.setByte("HangedMob", (byte) tileEntity.getHangedMob().ordinal());
                nbt.setInteger("HangedVillagerProfession", tileEntity.getHangedVillagerProfession());

                itemStack.setTagCompound(nbt);
            }
        }
        return itemStack;
    }

    @Override
    public int getLightValue(IBlockAccess access, BlockPos pos) {
        TileEntityExecution tileEntity = (TileEntityExecution) access.getTileEntity(pos);

        if (tileEntity != null && tileEntity.getExecutionType() == EnumExecution.BURNING_STAKE && tileEntity.getHangedMob() != EnumHangedMobs.NONE) {
            return 15;
        } else {
            return super.getLightValue(access, pos);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random random) {
        TileEntityExecution tileEntity = (TileEntityExecution) world.getTileEntity(pos);
        if (tileEntity != null && tileEntity.getExecutionType() == EnumExecution.BURNING_STAKE && tileEntity.getHangedMob() != EnumHangedMobs.NONE) {
            double xPos, zPos, yPos;

            yPos = pos.getY() + 0.25;
            for (int angle = 0; angle < 20; angle++) {
                xPos = pos.getX() + 0.5 + Math.sin(angle * 0.2792) * 0.75;
                zPos = pos.getZ() + 0.5 + Math.cos(angle * 0.2792) * 0.75;

                EntityFX entityfx = new EntityBigFlameFX(world, xPos, yPos, zPos, 0, 0, 0);
                Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
            }

            yPos += 0.25;
            for (int angle = 0; angle < 11; angle++) {
                xPos = pos.getX() + 0.5 + Math.sin(angle * 0.5584) * 0.5;
                zPos = pos.getZ() + 0.5 + Math.cos(angle * 0.5584) * 0.5;

                EntityFX entityfx = new EntityBigFlameFX(world, xPos, yPos, zPos, 0, 0, 0);
                Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
            }

            yPos += 0.35;
            for (int angle = 0; angle < 5; angle++) {
                xPos = pos.getX() + 0.5 + Math.sin(angle * 1.1168) * 0.2;
                zPos = pos.getZ() + 0.5 + Math.cos(angle * 1.1168) * 0.2;

                EntityFX entityfx = new EntityBigFlameFX(world, xPos, yPos, zPos, 0, 0, 0);
                Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
                world.spawnParticle(EnumParticleTypes.LAVA, xPos, yPos, zPos, 0, 0, 0);
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, xPos, yPos, zPos, 0, 0, 0);
            }
        }
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumfacing = EnumFacing.getFront(meta);

        if (enumfacing.getAxis() == EnumFacing.Axis.Y) {
            enumfacing = EnumFacing.NORTH;
        }

        return this.getDefaultState().withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumFacing) state.getValue(FACING)).getIndex();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[]{FACING});
    }


    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack itemStack) {
        EnumFacing enumfacing = EnumFacing.getHorizontal(MathHelper.floor_double((double) (player.rotationYaw * 4 / 360F) + 0.5D) & 3).getOpposite();
        state = state.withProperty(FACING, enumfacing);
        world.setBlockState(pos, state, 2);

        TileEntityExecution tileEntity = (TileEntityExecution) world.getTileEntity(pos);

        if (tileEntity != null) {
            tileEntity.setExecutionType(itemStack.getItemDamage());

            if (itemStack.hasTagCompound()) {
                NBTTagCompound nbt = itemStack.getTagCompound();
                tileEntity.setExecutionType(itemStack.getItemDamage());

                tileEntity.setHangedMob(EnumHangedMobs.getById(nbt.getByte("HangedMob")));
                tileEntity.setHangedVillagerProfession(nbt.getInteger("HangedVillagerProfession"));

                placeWalls(world, pos);
            }
        }
    }

    //TODO !!!!!!!!!!!!!!!!!!!!!!!!
    public static void placeWalls(World world, BlockPos pos) {
//        TileEntityExecution tileEntity = (TileEntityExecution) world.getTileEntity(pos);
//
//        if (tileEntity != null) {
//            //TODO almost the same code in ItemBlockGSMemorial
//            byte maxY;
//            byte maxX = 1;
//            byte maxZ = 1;
//            byte startX = 0;
//            byte startZ = 0;
//
//            switch (tileEntity.getExecutionType()) {
//                case CROSS:
//                case OBELISK:
//                    maxY = 5;
//                    maxX = 2;
//                    maxZ = 2;
//                    startX = -1;
//                    startZ = -1;
//                    break;
//                case DOG_STATUE:
//                case CAT_STATUE:
//                    maxY = 2;
//                    break;
//            }
//            for (byte shiftY = 0; shiftY < maxY; shiftY++) {
//                for (byte shiftZ = startZ; shiftZ < maxZ; shiftZ++) {
//                    for (byte shiftX = startX; shiftX < maxX; shiftX++) {
//                        BlockPos newPos = new BlockPos(pos.getX() + shiftX, pos.getY() + shiftY, pos.getZ() + shiftZ);
//                        if (world.getBlockState(newPos).getBlock() == Blocks.air) {
//                            world.setBlockState(newPos, GSBlock.invisibleWall.getDefaultState());
//                        }
//                    }
//                }
//            }
//        }
    }

    @Override
    public void onBlockDestroyedByExplosion(World world, BlockPos pos, Explosion explosionIn) {
        removeWalls(world, pos);
        super.onBlockDestroyedByExplosion(world, pos, explosionIn);
    }

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        removeWalls(world, pos);
        return super.removedByPlayer(world, pos, player, willHarvest);
    }

    //TODO !!!!!!!!!!!!!!!!!!!!!!!!
    private static void removeWalls(World world, BlockPos pos) {
//        //TODO almost the same code in ItemBlockGSMemorial
//        byte maxY;
//        byte maxX = 1;
//        byte maxZ = 1;
//        byte startX = 0;
//        byte startZ = 0;
//
//        TileEntityExecution tileEntity = (TileEntityExecution) world.getTileEntity(pos);
//
//        if (tileEntity != null) {
//            switch (tileEntity.getExecutionType()) {
//                case CROSS:
//                case OBELISK:
//                    maxY = 5;
//                    maxX = 2;
//                    maxZ = 2;
//                    startX = -1;
//                    startZ = -1;
//                    break;
//                case DOG_STATUE:
//                case CAT_STATUE:
//                    maxY = 2;
//                    break;
//            }
//            for (byte shiftY = 0; shiftY < maxY; shiftY++) {
//                for (byte shiftZ = startZ; shiftZ < maxZ; shiftZ++) {
//                    for (byte shiftX = startX; shiftX < maxX; shiftX++) {
//                        BlockPos newPos = new BlockPos(pos.getX() + shiftX, pos.getY() + shiftY, pos.getZ() + shiftZ);
//                        if (world.getBlockState(newPos).getBlock() == GSBlock.invisibleWall) {
//                            world.setBlockState(new BlockPos(pos.getX() + shiftX, pos.getY() + shiftY, pos.getZ() + shiftZ), Blocks.air.getDefaultState());
//                        }
//                    }
//                }
//            }
//        }
    }
}
