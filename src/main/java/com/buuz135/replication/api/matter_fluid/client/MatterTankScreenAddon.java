package com.buuz135.replication.api.matter_fluid.client;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.matter_fluid.IMatterTank;
import com.hrznstudio.titanium.api.client.assets.types.ITankAsset;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.component.fluid.FluidTankComponent;
import com.hrznstudio.titanium.util.AssetUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MatterTankScreenAddon extends BasicScreenAddon {

    private static final SpriteId MATTER = new SpriteId(TextureAtlas.LOCATION_BLOCKS, Identifier.fromNamespaceAndPath(Replication.MOD_ID, "block/matter"));
    private final IMatterTank tank;
    private final FluidTankComponent.Type type;
    private ITankAsset asset;

    public MatterTankScreenAddon(int posX, int posY, IMatterTank tank, FluidTankComponent.Type type) {
        super(posX, posY);
        this.tank = tank;
        this.type = type;
    }

    private static int color(float[] values) {
        int alpha = Math.max(0, Math.min(255, Math.round(values[3] * 255)));
        int red = Math.max(0, Math.min(255, Math.round(values[0] * 255)));
        int green = Math.max(0, Math.min(255, Math.round(values[1] * 255)));
        int blue = Math.max(0, Math.min(255, Math.round(values[2] * 255)));
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphicsExtractor graphics, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        this.asset = IAssetProvider.getAsset(provider, this.type.getAssetType());
        Rectangle area = this.asset.getArea();
        if (!this.tank.getMatter().isEmpty() && this.tank.getCapacity() > 0) {
            int verticalPadding = this.asset.getFluidRenderPadding(Direction.UP) + this.asset.getFluidRenderPadding(Direction.DOWN);
            int horizontalPadding = this.asset.getFluidRenderPadding(Direction.WEST) + this.asset.getFluidRenderPadding(Direction.EAST);
            int height = (int) (Math.min(this.tank.getMatterAmount(), this.tank.getCapacity()) / this.tank.getCapacity() * (area.height - verticalPadding));
            int x = guiX + this.getPosX() + this.asset.getFluidRenderPadding(Direction.WEST);
            int y = guiY + this.getPosY() + area.height - this.asset.getFluidRenderPadding(Direction.DOWN) - height;
            int width = area.width - horizontalPadding;
            float[] matterColor = this.tank.getMatter().getMatterType().getColor().get();
            int color = color(matterColor);
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, Minecraft.getInstance().getAtlasManager().get(MATTER), x, y, width, height, color);
        }
        AssetUtil.drawAsset(graphics, screen, this.asset, guiX + this.getPosX(), guiY + this.getPosY());
    }

    @Override
    public void drawForegroundLayer(GuiGraphicsExtractor graphics, Screen screen, IAssetProvider provider, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    public List<Component> getTooltipLines() {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.replication.tank.matter").getString())
                .append(this.tank.getMatter().isEmpty() ? Component.translatable("tooltip.titanium.tank.empty").withStyle(ChatFormatting.WHITE)
                        : Component.translatable(this.tank.getMatter().getTranslationKey()).withStyle(ChatFormatting.WHITE)));
        lines.add(Component.translatable("tooltip.replication.terminal.amount").withStyle(ChatFormatting.GOLD)
                .append(Component.literal(ChatFormatting.WHITE + new DecimalFormat().format(this.tank.getMatter().getAmount()) + ChatFormatting.GOLD + "/" + ChatFormatting.WHITE + new DecimalFormat().format(this.tank.getCapacity())))
                .append(Component.translatable("tooltip.replication.tank.unit").withStyle(ChatFormatting.DARK_AQUA)));
        return lines;
    }

    @Override
    public int getXSize() {
        return this.asset == null ? 0 : this.asset.getArea().width;
    }

    @Override
    public int getYSize() {
        return this.asset == null ? 0 : this.asset.getArea().height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
}
