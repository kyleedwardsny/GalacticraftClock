package us.kyleedwards.mods.galacticraftclock.screen;

import micdoodle8.mods.galacticraft.api.client.IGameScreen;
import micdoodle8.mods.galacticraft.api.client.IScreenManager;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.core.client.screen.DrawGameScreen;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDish;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

public class GameScreenClock implements IGameScreen
{
    public static final int DISH_DISTANCE = 700;

    private TextureManager renderEngine;

    private float frameA;

    public GameScreenClock()
    {
        if (GCCoreUtil.getEffectiveSide().isClient())
        {
            renderEngine = FMLClientHandler.instance().getClient().renderEngine;
        }
    }

    @Override
    public void setFrameSize(float frameSize)
    {
        this.frameA = frameSize;
    }

    @Override
    public void render(int type, float ticks, float scaleX, float scaleY, IScreenManager scr)
    {
        float frameBx = scaleX - frameA;
        float frameBy = scaleY - frameA;

        drawBlackBackground(frameBx, frameBy, 0.1f);

        DrawGameScreen screen = (DrawGameScreen) scr;
        World world = screen.driver.getWorld();
        BlockPos scrPos = screen.driver.getPos();

        boolean foundDish = false;
        for (TileEntity te : world.loadedTileEntityList)
        {
            if (te instanceof TileEntityDish)
            {
                BlockPos dishPos = te.getPos();
                if (scrPos.distanceSq(dishPos.getX(), dishPos.getY(), dishPos.getZ()) <= DISH_DISTANCE * DISH_DISTANCE)
                {
                    foundDish = true;
                    break;
                }
            }
        }

        if (foundDish)
        {
            for (Planet planet : GalaxyRegistry.getRegisteredPlanets().values())
            {
                //System.out.println(provider.getWorldTime());
            }
            //System.out.println();
        }
        else
        {
            // TODO Show communications error
        }
    }

    private void drawBlackBackground(float frameBx, float frameBy, float grayLevel)
    {

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tess = Tessellator.getInstance();
        VertexBuffer worldRenderer = tess.getBuffer();
        GL11.glColor4f(grayLevel, grayLevel, grayLevel, 1.0f);

        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
        worldRenderer.pos(frameA, frameBy, 0.005f).endVertex();
        worldRenderer.pos(frameBx, frameBy, 0.005f).endVertex();
        worldRenderer.pos(frameBx, frameA, 0.005f).endVertex();
        worldRenderer.pos(frameA, frameA, 0.005f).endVertex();
        tess.draw();

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
