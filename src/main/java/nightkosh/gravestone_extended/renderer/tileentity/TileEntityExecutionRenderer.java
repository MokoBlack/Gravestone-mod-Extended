package nightkosh.gravestone_extended.renderer.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import nightkosh.gravestone.renderer.tileentity.TileEntityRenderer;
import nightkosh.gravestone_extended.block.enums.EnumExecution;
import nightkosh.gravestone_extended.block.enums.EnumHangedMobs;
import nightkosh.gravestone_extended.models.block.ModelExecution;
import nightkosh.gravestone_extended.models.block.execution.ModelBurningStake;
import nightkosh.gravestone_extended.models.block.execution.ModelGibbet;
import nightkosh.gravestone_extended.models.block.execution.ModelStocks;
import nightkosh.gravestone_extended.tileentity.TileEntityExecution;
import org.lwjgl.opengl.GL11;

/**
 * GraveStone mod
 *
 * @author NightKosh
 * @license Lesser GNU Public License v3 (http://www.gnu.org/licenses/lgpl.html)
 */
public class TileEntityExecutionRenderer extends TileEntityRenderer {

    public static ModelExecution gallows = new ModelGibbet();//TODO
    public static ModelExecution gibbet = new ModelGibbet();
    public static ModelExecution stocks = new ModelStocks();
    public static ModelExecution burningStake = new ModelBurningStake();

    public static TileEntityExecutionRenderer instance;

    private static final TileEntityExecution EXECUTION_TE = new TileEntityExecution();

    static {
        EXECUTION_TE.setExecutionType(EnumExecution.GIBBET);
    }

    public TileEntityExecutionRenderer() {
        instance = this;
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float f, int par9) {
        TileEntityExecution tileEntity = (TileEntityExecution) te;

        if (tileEntity == null) {
            tileEntity = getDefaultTE();
        }
        EnumExecution execution = tileEntity.getExecutionType();
        int meta = 0;

        if (tileEntity.getWorld() != null) {
            meta = tileEntity.getBlockMetadata();
        }

        renderMemorial(x, y, z, tileEntity.getWorld(), execution, tileEntity.getHangedMob(), tileEntity.getHangedVillagerProfession(), EnumFacing.values()[meta]);
    }

    private void renderMemorial(double x, double y, double z, World world, EnumExecution execution,
                                EnumHangedMobs hangedMob, int hangedVillagerProfession, EnumFacing facing) {
        GL11.glPushMatrix();

        if (world != null) {
            GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
            GL11.glScalef(1, -1, -1);
        } else {
            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.8F, (float) z + 0.5F);
            GL11.glScalef(0.7F, -0.7F, -0.7F);
        }

        switch (facing) {
            case SOUTH:
                GL11.glRotatef(0, 0, 1, 0);
                break;
            case WEST:
                GL11.glRotatef(90, 0, 1, 0);
                break;
            case NORTH:
                GL11.glRotatef(180, 0, 1, 0);
                break;
            case EAST:
                GL11.glRotatef(270, 0, 1, 0);
                break;
        }

        renderMemorial(execution, hangedMob, hangedVillagerProfession);

        GL11.glPopMatrix();
    }

    private void renderMemorial(EnumExecution execution, EnumHangedMobs hangedMob, int hangedVillagerProfession) {
        ModelExecution model = getModel(execution);
        bindTextureByName(execution.getTexture());
        model.customRender(execution, hangedMob, hangedVillagerProfession);
    }

    private static ModelExecution getModel(EnumExecution execution) {
        switch (execution) {
            case GALLOWS:
            default:
                return gallows;
            case GIBBET:
                return gibbet;
            case STOCKS:
                return stocks;
            case BURNING_STAKE:
                return burningStake;
        }
    }

    protected TileEntityExecution getDefaultTE() {
        return EXECUTION_TE;
    }

    public static class Gibbet extends TileEntityExecutionRenderer {
        private static final TileEntityExecution EXECUTION_TE = new TileEntityExecution();

        static {
            EXECUTION_TE.setExecutionType(EnumExecution.GIBBET);
        }

        @Override
        protected TileEntityExecution getDefaultTE() {
            return EXECUTION_TE;
        }
    }

    public static class Stocks extends TileEntityExecutionRenderer {
        private static final TileEntityExecution EXECUTION_TE = new TileEntityExecution();

        static {
            EXECUTION_TE.setExecutionType(EnumExecution.STOCKS);
        }

        @Override
        protected TileEntityExecution getDefaultTE() {
            return EXECUTION_TE;
        }
    }

    public static class BurningStake extends TileEntityExecutionRenderer {
        private static final TileEntityExecution EXECUTION_TE = new TileEntityExecution();

        static {
            EXECUTION_TE.setExecutionType(EnumExecution.BURNING_STAKE);
        }

        @Override
        protected TileEntityExecution getDefaultTE() {
            return EXECUTION_TE;
        }
    }
}
