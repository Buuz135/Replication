package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.api.matter_fluid.MatterStack;
import com.hrznstudio.titanium.api.client.AssetTypes;
import com.hrznstudio.titanium.api.client.assets.types.ITankAsset;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.AssetUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record MatterTankDisplay(IMatterType type, long amount){

    public void render(GuiGraphics guiGraphics, int x, int y, double mouseX, double mouseY) {
        int topBottomPadding = 6;
        //double amount = Math.min(amount, Integer.MAX_VALUE)/ Integer.MAX_VALUE);
        int offset = (int) (( 1 * (19 - topBottomPadding)));
        ResourceLocation flowing = new ResourceLocation("minecraft:block/white_wool");
        if (flowing != null) {
            AbstractTexture texture = Minecraft.getInstance().screen.getMinecraft().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS); //getAtlasSprite
            if (texture instanceof TextureAtlas) {
                TextureAtlasSprite sprite = ((TextureAtlas) texture).getSprite(flowing);
                if (sprite != null) {
                    //RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
                    var color = type.getColor().get();
                    guiGraphics.setColor(color[0], color[1], color[2], color[3]);
                    RenderSystem.enableBlend();
                    guiGraphics.blit(
                            x + 3,
                            y + 3, //isGas
                            0,
                            19 - 6,
                            offset,
                            sprite);
                    RenderSystem.disableBlend();
                    guiGraphics.setColor(1, 1, 1, 1);
                }
            }
        }

        ITankAsset asset = IAssetProvider.getAsset(IAssetProvider.DEFAULT_PROVIDER, AssetTypes.TANK_SMALL);
        AssetUtil.drawAsset(guiGraphics, Minecraft.getInstance().screen, asset, x, y);

        if (mouseX > x && mouseX < x + 18 && mouseY > y && mouseY < y + 18) {
            List<Component> strings = new ArrayList<>();
            strings.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.replication.tank.matter").getString()).append(Component.translatable(type.getName())).withStyle(ChatFormatting.WHITE));
            strings.add(net.minecraft.network.chat.Component.translatable("tooltip.titanium.tank.amount").withStyle(ChatFormatting.GOLD).append(Component.literal(ChatFormatting.WHITE + new DecimalFormat().format(amount) +  ChatFormatting.DARK_AQUA + " matter")));
            guiGraphics.renderTooltip(Minecraft.getInstance().font, strings, Optional.empty(), (int) mouseX, (int) mouseY);
        }
    }

}
