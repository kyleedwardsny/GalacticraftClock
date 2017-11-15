package us.kyleedwards.mods.galacticraftclock.screen;

import micdoodle8.mods.galacticraft.api.client.IGameScreen;
import micdoodle8.mods.galacticraft.api.client.IScreenManager;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.core.client.screen.DrawGameScreen;
import micdoodle8.mods.galacticraft.core.tile.TileEntityDish;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import scala.tools.nsc.doc.model.Def;
import us.kyleedwards.mods.galacticraftclock.Constants;
import us.kyleedwards.mods.galacticraftclock.time.DimensionTimeInfo;
import us.kyleedwards.mods.galacticraftclock.time.DimensionTimeRegistry;

import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collections;

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
        float displayWidth = scaleX - frameA * 2;
        float displayHeight = scaleY - frameA * 2;
        float squareSize = Math.min(displayWidth, displayHeight);

        drawBlackBackground(frameBx, frameBy, 0.1f);

        DoubleBuffer planes = BufferUtils.createDoubleBuffer(4 * Double.SIZE);
        planeEquation(planes, frameA, frameA, 0, frameA, frameBy, 0, frameA, frameBy, 1);
        GL11.glClipPlane(GL11.GL_CLIP_PLANE0, planes);
        GL11.glEnable(GL11.GL_CLIP_PLANE0);
        planeEquation(planes, frameBx, frameBy, 0, frameBx, frameA, 0, frameBx, frameA, 1);
        GL11.glClipPlane(GL11.GL_CLIP_PLANE1, planes);
        GL11.glEnable(GL11.GL_CLIP_PLANE1);
        planeEquation(planes, frameA, frameBy, 0, frameBx, frameBy, 0, frameBx, frameBy, 1);
        GL11.glClipPlane(GL11.GL_CLIP_PLANE2, planes);
        GL11.glEnable(GL11.GL_CLIP_PLANE2);
        planeEquation(planes, frameBx, frameA, 0, frameA, frameA, 0, frameA, frameA, 1);
        GL11.glClipPlane(GL11.GL_CLIP_PLANE3, planes);
        GL11.glEnable(GL11.GL_CLIP_PLANE3);

        Tessellator tess = Tessellator.getInstance();
        VertexBuffer worldRenderer = tess.getBuffer();

        DrawGameScreen screen = (DrawGameScreen) scr;
        World world = screen.driver.getWorld();
        BlockPos scrPos = screen.driver.getPos();

        FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;
        float scaleText = squareSize / (float) renderer.FONT_HEIGHT * 0.15F;

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
            if (!DimensionTimeRegistry.dimensionTimes.isEmpty())
            {
                int currentBody = ((int) (ticks / (20 * 3))) % DimensionTimeRegistry.dimensionTimes.size();
                ArrayList<Integer> dimensionIds = new ArrayList<Integer>(DimensionTimeRegistry.dimensionTimes.keySet());
                Collections.sort(dimensionIds);
                int dimensionId = dimensionIds.get(currentBody);

                CelestialBody body = GalaxyRegistry.getCelestialBodyFromDimensionID(dimensionId);
                DimensionTimeInfo info = DimensionTimeRegistry.dimensionTimes.get(dimensionId);
                float dayProgress = ((float) info.time % (float) info.dayLength) / (float) info.dayLength;

                float barLeft = frameA + (displayWidth - squareSize) / 2 + 0.05f * squareSize;
                float barRight = frameBx - (displayWidth - squareSize) / 2 - 0.05f * squareSize;
                float barWidth = barRight - barLeft;
                float barHeight = barWidth * 5f / 40f;
                float barBottom = frameBy - (displayHeight - squareSize) / 2 - 0.05f * squareSize;
                float barTop = barBottom - barHeight;

                // Render the day/night bar
                ResourceLocation location = new ResourceLocation(Constants.ASSET_PREFIX, Constants.DAY_NIGHT_CYCLE_PNG_LOCATION);
                this.renderEngine.bindTexture(location);
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                worldRenderer.pos(barLeft, barBottom, 0).tex(0, 1.0f).endVertex();
                worldRenderer.pos(barRight, barBottom, 0).tex(1.0f, 1.0f).endVertex();
                worldRenderer.pos(barRight, barTop, 0).tex(1.0f, 0).endVertex();
                worldRenderer.pos(barLeft, barTop, 0).tex(0, 0).endVertex();
                tess.draw();

                float markerWidth = barWidth / 40f;
                float markerHeight = barHeight * 7f / 5f;
                float markerLeft = barLeft + barWidth * dayProgress - markerWidth / 2;
                float markerRight = markerLeft + markerWidth;
                float markerBottom = barBottom + barHeight / 5f;
                float markerTop = markerBottom - markerHeight;

                // Render the marker
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glColor4f(0.9f, 0.9f, 0.9f, 1.0f);
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
                worldRenderer.pos(markerLeft, markerBottom, -0.005f).endVertex();
                worldRenderer.pos(markerRight, markerBottom, -0.005f).endVertex();
                worldRenderer.pos(markerRight, markerTop, -0.005f).endVertex();
                worldRenderer.pos(markerLeft, markerTop, -0.005f).endVertex();
                tess.draw();

                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glEnable(GL11.GL_TEXTURE_2D);

                String name = body.getLocalizedName();
                int strWidth = renderer.getStringWidth(name);
                float strLeft = scaleX / 2 - (float) strWidth * scaleText / 2;
                float strBottom = markerTop - squareSize * (0.15F / (float) renderer.FONT_HEIGHT);
                float strTop = strBottom - squareSize * 0.15F;

                // Render the text
                GL11.glPushMatrix();
                GL11.glTranslatef(strLeft, strTop, 0.0F);
                GL11.glScalef(scaleText, scaleText, 1.0F);
                renderer.drawString(name, 0, 0, 0xE6E6E6, false);
                GL11.glPopMatrix();
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

                float bodyBottom = strTop - squareSize * (0.15F / (float) renderer.FONT_HEIGHT);
                float bodyTop = frameA + (displayHeight - squareSize) / 2 + 0.05F * squareSize;
                float bodyHeight = bodyBottom - bodyTop;
                float bodyWidth = bodyHeight;
                float bodyLeft = scaleX / 2 - bodyWidth / 2;
                float bodyRight = scaleX / 2 + bodyWidth / 2;

                // Render the celestial body
                this.renderEngine.bindTexture(body.getBodyIcon());
                worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                worldRenderer.pos(bodyLeft, bodyBottom, 0).tex(0, 1.0f).endVertex();
                worldRenderer.pos(bodyRight, bodyBottom, 0).tex(1.0f, 1.0f).endVertex();
                worldRenderer.pos(bodyRight, bodyTop, 0).tex(1.0f, 0).endVertex();
                worldRenderer.pos(bodyLeft, bodyTop, 0).tex(0, 0).endVertex();
                tess.draw();
            }
        }
        else
        {
            int color = 0xFF0000;

            String text = "Comm Error";
            int strWidth = renderer.getStringWidth(text);
            float strLeft = scaleX / 2 - (float) strWidth * scaleText / 2;
            float strTop = scaleY / 2 - (renderer.FONT_HEIGHT + 1) * scaleText;

            GL11.glPushMatrix();
            GL11.glTranslatef(strLeft, strTop, 0F);
            GL11.glScalef(scaleText, scaleText, 1F);
            renderer.drawString(text, 0, 0, color, false);
            GL11.glPopMatrix();

            text = "No Dish";
            strWidth = renderer.getStringWidth(text);
            strLeft = scaleX / 2 - (float) strWidth * scaleText / 2;
            strTop = scaleY / 2;

            GL11.glPushMatrix();
            GL11.glTranslatef(strLeft, strTop, 0F);
            GL11.glScalef(scaleText, scaleText, 1F);
            renderer.drawString(text, 0, 0, color, false);
            GL11.glPopMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }

        GL11.glDisable(GL11.GL_CLIP_PLANE3);
        GL11.glDisable(GL11.GL_CLIP_PLANE2);
        GL11.glDisable(GL11.GL_CLIP_PLANE1);
        GL11.glDisable(GL11.GL_CLIP_PLANE0);
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

    private static void planeEquation(DoubleBuffer planes, float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3)
    {
        double[] result = new double[4];
        result[0] = y1 * (z2 - z3) + y2 * (z3 - z1) + y3 * (z1 - z2);
        result[1] = z1 * (x2 - x3) + z2 * (x3 - x1) + z3 * (x1 - x2);
        result[2] = x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2);
        result[3] = -(x1 * (y2 * z3 - y3 * z2) + x2 * (y3 * z1 - y1 * z3) + x3 * (y1 * z2 - y2 * z1));
        planes.put(result, 0, 4);
        planes.position(0);
    }
}
