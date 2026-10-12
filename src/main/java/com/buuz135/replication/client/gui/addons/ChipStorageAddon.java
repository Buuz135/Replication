package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.tile.ChipStorageBlockEntity;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class ChipStorageAddon extends BasicScreenAddon {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png");
    private final ChipStorageBlockEntity blockEntity;

    public ChipStorageAddon(int posX, int posY, ChipStorageBlockEntity blockEntity) {
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
        int[][] positions = {{70, 19}, {91, 19}, {49, 40}, {112, 40}, {49, 61}, {112, 61}, {70, 82}, {91, 82}};
        for (int[] position : positions) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + position[0], guiY + position[1], 244, 164, 12, 14, 256, 256);
        }
    }

    @Override
    public void drawForegroundLayer(GuiGraphicsExtractor graphics, Screen screen, IAssetProvider assets, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
    }
}
