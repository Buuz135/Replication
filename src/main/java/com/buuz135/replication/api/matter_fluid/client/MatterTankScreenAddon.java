package com.buuz135.replication.api.matter_fluid.client;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.buuz135.replication.api.matter_fluid.MatterTank;
import com.hrznstudio.titanium.Titanium;
import com.hrznstudio.titanium.api.client.assets.types.ITankAsset;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.network.locator.ILocatable;
import com.hrznstudio.titanium.network.messages.ButtonClickNetworkMessage;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.BucketItem;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.joml.Matrix4f;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MatterTankScreenAddon extends BasicScreenAddon {


    private IMatterTank tank;
    private ITankAsset asset;
    private FluidTankComponent.Type type;

    public MatterTankScreenAddon(int posX, int posY, IMatterTank tank, FluidTankComponent.Type type) {
        super(posX, posY);
        this.tank = tank;
        this.type = type;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        asset = IAssetProvider.getAsset(provider, type.getAssetType());
        Rectangle area = asset.getArea();
        if (!tank.getMatter().isEmpty()) {
            MatterStack matterStack = tank.getMatter();
            double stored = tank.getMatterAmount();
            double capacity = tank.getCapacity();
            int topBottomPadding = asset.getFluidRenderPadding(Direction.UP) + asset.getFluidRenderPadding(Direction.DOWN);
            int offset = (int) ((stored / capacity) * (area.height - topBottomPadding));
            ResourceLocation flowing = new ResourceLocation(Replication.MOD_ID, "block/matter");
            if (flowing != null) {
                AbstractTexture texture = screen.getMinecraft().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS); //getAtlasSprite
                if (texture instanceof TextureAtlas) {
                    TextureAtlasSprite sprite = ((TextureAtlas) texture).getSprite(flowing);
                    if (sprite != null) {
                        //RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
                        var color = matterStack.getMatterType().getColor().get();
                        guiGraphics.setColor(color[0], color[1], color[2], color[3]);
                        RenderSystem.enableBlend();
                        drawTiledSprite(guiGraphics, this.getPosX() + guiX + asset.getFluidRenderPadding(Direction.WEST),
                                this.getPosY() + guiY + asset.getFluidRenderPadding(Direction.UP) + area.height - topBottomPadding - offset,
                                offset,
                                (int) (area.getWidth() - asset.getFluidRenderPadding(Direction.EAST) - asset.getFluidRenderPadding(Direction.WEST)),
                                offset, sprite, sprite.contents().width(), sprite.contents().height(), 0, false);
                        RenderSystem.disableBlend();
                        guiGraphics.setColor(1, 1, 1, 1);
                    }
                }
            }
        }
        guiGraphics.setColor(1, 1, 1, 1);
        ITankAsset asset = IAssetProvider.getAsset(provider, type.getAssetType());
        AssetUtil.drawAsset(guiGraphics, screen, asset, guiX + getPosX(), guiY + getPosY());
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    public java.util.List<net.minecraft.network.chat.Component> getTooltipLines() {
        List<Component> strings = new ArrayList<>();
        strings.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.replication.tank.matter").getString()).append(tank.getMatter().isEmpty() ? Component.translatable("tooltip.titanium.tank.empty").withStyle(ChatFormatting.WHITE) : Component.translatable(tank.getMatter().getTranslationKey())).withStyle(ChatFormatting.WHITE));
        strings.add(net.minecraft.network.chat.Component.translatable("tooltip.titanium.tank.amount").withStyle(ChatFormatting.GOLD).append(Component.literal(ChatFormatting.WHITE + new DecimalFormat().format(tank.getMatterAmount()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(tank.getCapacity()) + ChatFormatting.DARK_AQUA + " matter")));
        return strings;
    }

    @Override
    public int getXSize() {
        return asset != null ? asset.getArea().width : 0;
    }

    @Override
    public int getYSize() {
        return asset != null ? asset.getArea().height : 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    public static void drawTiledSprite(GuiGraphics guiGraphics, int xPosition, int yPosition, int yOffset, int desiredWidth, int desiredHeight, TextureAtlasSprite sprite,
                                       int textureWidth, int textureHeight, int zLevel,  boolean blend) {
        if (desiredWidth == 0 || desiredHeight == 0 || textureWidth == 0 || textureHeight == 0) {
            return;
        }
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        int xTileCount = desiredWidth / textureWidth;
        int xRemainder = desiredWidth - (xTileCount * textureWidth);
        int yTileCount = desiredHeight / textureHeight;
        int yRemainder = desiredHeight - (yTileCount * textureHeight);
        int yStart = yPosition + yOffset;
        float uMin = sprite.getU0();
        float uMax = sprite.getU1();
        float vMin = sprite.getV0();
        float vMax = sprite.getV1();
        float uDif = uMax - uMin;
        float vDif = vMax - vMin;
        if (blend) {
            RenderSystem.enableBlend();
        }
        //Note: We still use the tesselator as that is what GuiGraphics#innerBlit does
        BufferBuilder vertexBuffer = Tesselator.getInstance().getBuilder();
        vertexBuffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix4f = guiGraphics.pose().last().pose();
        for (int xTile = 0; xTile <= xTileCount; xTile++) {
            int width = (xTile == xTileCount) ? xRemainder : textureWidth;
            if (width == 0) {
                break;
            }
            int x = xPosition + (xTile * textureWidth);
            int maskRight = textureWidth - width;
            int shiftedX = x + textureWidth - maskRight;
            float uLocalDif = uDif * maskRight / textureWidth;
            float uLocalMin;
            float uLocalMax;
            //if (tilingDirection.right) {
                uLocalMin = uMin;
                uLocalMax = uMax - uLocalDif;
            //} else {
            //    uLocalMin = uMin + uLocalDif;
            //    uLocalMax = uMax;
            //}
            for (int yTile = 0; yTile <= yTileCount; yTile++) {
                int height = (yTile == yTileCount) ? yRemainder : textureHeight;
                if (height == 0) {
                    //Note: We don't want to fully break out because our height will be zero if we are looking to
                    // draw the remainder, but there is no remainder as it divided evenly
                    break;
                }
                int y = yStart - ((yTile + 1) * textureHeight);
                int maskTop = textureHeight - height;
                float vLocalDif = vDif * maskTop / textureHeight;
                float vLocalMin;
                float vLocalMax;
                //if (tilingDirection.down) {
                    vLocalMin = vMin;
                    vLocalMax = vMax - vLocalDif;
                //} else {
                //    vLocalMin = vMin + vLocalDif;
                //    vLocalMax = vMax;
                //}
                vertexBuffer.vertex(matrix4f, x, y + textureHeight, zLevel).uv(uLocalMin, vLocalMax).endVertex();
                vertexBuffer.vertex(matrix4f, shiftedX, y + textureHeight, zLevel).uv(uLocalMax, vLocalMax).endVertex();
                vertexBuffer.vertex(matrix4f, shiftedX, y + maskTop, zLevel).uv(uLocalMax, vLocalMin).endVertex();
                vertexBuffer.vertex(matrix4f, x, y + maskTop, zLevel).uv(uLocalMin, vLocalMin).endVertex();
            }
        }
        BufferUploader.drawWithShader(vertexBuffer.end());
        if (blend) {
            RenderSystem.disableBlend();
        }
    }
}
