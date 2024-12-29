package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.tile.ChipStorageBlockEntity;
import com.buuz135.replication.block.tile.IdentificationChamberBlockEntity;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class ChipStorageAddon extends BasicScreenAddon {

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
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider,  int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,200);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 70, guiY + 19, 244,164,12,14);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 91, guiY + 19, 244,164,12,14);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 49, guiY + 40, 244,164,12,14);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 112, guiY + 40, 244,164,12,14);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 49, guiY + 61, 244,164,12,14);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 112, guiY + 61, 244,164,12,14);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 70, guiY + 82, 244,164,12,14);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 91, guiY + 82, 244,164,12,14);
        guiGraphics.pose().popPose();
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider,int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {

    }
}
