package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.tile.IdentificationChamberBlockEntity;
import com.buuz135.replication.block.tile.ReplicatorBlockEntity;
import com.buuz135.replication.util.ReplicationTags;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import com.hrznstudio.titanium.util.LangUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class IdentificationChamberAddon extends BasicScreenAddon {

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
    public void drawBackgroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider,  int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 49, guiY + 43, 244,161,6,3);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 111, guiY + 18, 250,178,6,6);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 111, guiY + 25, 244,161,6,3);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 147, guiY + 18, 250,178,6,6);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 147, guiY + 25, 250,161,6,3);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,200);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 108, guiY + 31, 244,164,12,14);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 108, guiY + 31 + 18, 244,164,12,14);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 108, guiY + 31 + 36, 244,164,12,14);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 144, guiY + 31, 244,164,12,14);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 144, guiY + 31 + 18, 244,164,12,14);
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 144, guiY + 31 + 36, 244,164,12,14);
        guiGraphics.pose().translate(0,0,200);
        if (this.blockEntity.getInput().getStackInSlot(0).is(ReplicationTags.CANT_BE_SCANNED)) guiGraphics.renderItem(new ItemStack(Items.BARRIER), guiX + 74, guiY + 48);
        guiGraphics.pose().popPose();
    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider,int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {

    }
}
