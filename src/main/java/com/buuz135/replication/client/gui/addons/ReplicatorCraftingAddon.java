package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;

public class ReplicatorCraftingAddon extends BasicScreenAddon {

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
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider,  int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 41, guiY + 26, 211,125,45,36);
//        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + this.getPosX() + 50 + 6, guiY + this.getPosY(), 206,125,50,27);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 100, guiY + 58, 250,161,6,3);
        if (!blockEntity.getCraftingStack().isEmpty()){
            guiGraphics.renderItem(blockEntity.getCraftingStack(), guiX + 67, guiY + 29);
        }
        var scale = 0.6f;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, scale);
//        guiGraphics.drawString(Minecraft.getInstance().font, LangUtil.getString("replication.current_crafting"), (guiX + this.getPosX() +2) * 1/scale, (guiY + this.getPosY() + 2) * 1/scale, 0x72e567, false);
        guiGraphics.drawString(Minecraft.getInstance().font, LangUtil.getString("replication.infinite_mode") + ": " + (blockEntity.isInfinite() ? "True" : "False"), (guiX + 41) * 1/scale, (guiY + 20) * 1/scale, 0x72e567, false);
        guiGraphics.pose().popPose();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,100);
        guiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 46, guiY + 32, 232,164,12,9);
        guiGraphics.pose().popPose();

        if (!blockEntity.getCraftingStack().isEmpty() && mouseX > (guiX + 67) && mouseX < (guiX + 67 + 16) && mouseY > (guiY + 29) && mouseY < (guiY + 29 + 16)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, this.blockEntity.getCraftingStack(), (int) mouseX, (int) mouseY);
        }
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider,int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {

    }
}
