package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.tile.IdentificationChamberBlockEntity;
import com.buuz135.replication.util.ReplicationTags;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class IdentificationChamberAddon extends BasicScreenAddon {

    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png");
    private final IdentificationChamberBlockEntity blockEntity;

    public IdentificationChamberAddon(int posX, int posY, IdentificationChamberBlockEntity blockEntity) {
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
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + 49, guiY + 43, 244, 161, 6, 3, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + 111, guiY + 18, 250, 178, 6, 6, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + 111, guiY + 25, 244, 161, 6, 3, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + 147, guiY + 18, 250, 178, 6, 6, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + 147, guiY + 25, 250, 161, 6, 3, 256, 256);
        int[][] positions = {{108, 31}, {108, 49}, {108, 67}, {144, 31}, {144, 49}, {144, 67}};
        for (int[] position : positions) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, guiX + position[0], guiY + position[1], 244, 164, 12, 14, 256, 256);
        }
        if (this.blockEntity.getInput().getStackInSlot(0).is(ReplicationTags.CANT_BE_SCANNED)) {
            graphics.item(new ItemStack(Items.BARRIER), guiX + 74, guiY + 48);
        }
    }

    @Override
    public void drawForegroundLayer(GuiGraphicsExtractor graphics, Screen screen, IAssetProvider assets, int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
    }
}
