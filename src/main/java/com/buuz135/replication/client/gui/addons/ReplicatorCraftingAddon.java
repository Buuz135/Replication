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
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal.png"), guiX + this.getPosX(), guiY + this.getPosY(), 206,149,50,27);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal.png"), guiX + this.getPosX() + 50 + 6, guiY + this.getPosY(), 206,149,50,27);
        if (!blockEntity.getCraftingStack().isEmpty()){
            guiGraphics.renderItem(blockEntity.getCraftingStack(), guiX + this.getPosX() + 25 - 8, guiY + this.getPosY() + 8);
        }
        var scale = 0.5f;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(scale, scale, scale);
        guiGraphics.drawString(Minecraft.getInstance().font, LangUtil.getString("replication.current_crafting"), (guiX + this.getPosX() +2) * 1/scale, (guiY + this.getPosY() + 2) * 1/scale, 0x72e567, false);
        guiGraphics.drawString(Minecraft.getInstance().font, LangUtil.getString("replication.infinite_mode"), (guiX + this.getPosX() + 2 + 50 + 6) * 1/scale, (guiY + this.getPosY() + 2) * 1/scale, 0x72e567, false);

        guiGraphics.pose().popPose();

        if (!blockEntity.getCraftingStack().isEmpty() && mouseX > (this.getPosX() + 25 - 10 + guiX) && mouseX < (this.getPosX() + 25 - 10 + 18 + guiX) && mouseY > (this.getPosY()  + 8 - 2 + guiY) && mouseY < (this.getPosY() + 18 + 8 -2 + guiY)) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, this.blockEntity.getCraftingStack(), (int) mouseX, (int) mouseY);
        }
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider,int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {

    }
}
