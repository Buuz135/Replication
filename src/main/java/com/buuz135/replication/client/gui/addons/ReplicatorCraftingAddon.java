package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ReplicatorCraftingAddon extends BasicScreenAddon {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png");
    private final ReplicatorBlockEntity blockEntity;

    public ReplicatorCraftingAddon(int posX, int posY, ReplicatorBlockEntity blockEntity) {
        super(posX, posY);
        this.blockEntity = blockEntity;
    }

    @Override
    public int getXSize() {
        return 0;
    }

    @Override
    public int getYSize() {
        return 0;
    }

    @Override
    public void drawBackgroundLayer(GuiGraphicsExtractor graphics, Screen screen, IAssetProvider assets, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + 41, guiY + 26, 211, 125, 45, 36, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + 100, guiY + 58, 250, 161, 6, 3, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + 46, guiY + 32, 232, 164, 12, 9, 256, 256);
        if (!this.blockEntity.getCraftingStack().isEmpty()) {
            graphics.item(this.blockEntity.getCraftingStack(), guiX + 67, guiY + 29);
            if (mouseX > guiX + 67 && mouseX < guiX + 83 && mouseY > guiY + 29 && mouseY < guiY + 45) {
                graphics.setTooltipForNextFrame(screen.getFont(), this.blockEntity.getCraftingStack(), mouseX, mouseY);
            }
        }
        Component state = Component.translatable("replication.infinite_mode").append(this.blockEntity.isInfinite()
                ? Component.translatable("replication.true") : Component.translatable("replication.false"));
        graphics.text(screen.getFont(), state, guiX + 41, guiY + 18, 0xFF72E567, false);
    }

    @Override
    public void drawForegroundLayer(GuiGraphicsExtractor graphics, Screen screen, IAssetProvider assets, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
    }
}
