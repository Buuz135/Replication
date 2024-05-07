package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.tile.DisintegratorBlockEntity;
import com.buuz135.replication.block.tile.IdentificationChamberBlockEntity;
import com.buuz135.replication.util.ReplicationTags;
import com.hrznstudio.titanium.client.screen.addon.BasicScreenAddon;
import com.hrznstudio.titanium.client.screen.asset.IAssetProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class DisintegratorAddon extends BasicScreenAddon {

    private final DisintegratorBlockEntity blockEntity;

    public DisintegratorAddon(int posX, int posY, DisintegratorBlockEntity blockEntity) {
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
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/replication_terminal_extras.png"), guiX + 34, guiY + 25, 244,161,6,3);

        if (this.blockEntity.getInput().getStackInSlot(0).is(ReplicationTags.CANT_BE_DISINTEGRATED)) guiGraphics.renderItem(new ItemStack(Items.BARRIER), guiX + 29, guiY + 30);
        if (this.blockEntity.getInput().getStackInSlot(1).is(ReplicationTags.CANT_BE_DISINTEGRATED)) guiGraphics.renderItem(new ItemStack(Items.BARRIER), guiX + 29, guiY + 48);
        if (this.blockEntity.getInput().getStackInSlot(2).is(ReplicationTags.CANT_BE_DISINTEGRATED)) guiGraphics.renderItem(new ItemStack(Items.BARRIER), guiX + 29, guiY + 48 + 18);

    }

    @Override
    public void drawForegroundLayer(GuiGraphics guiGraphics, Screen screen, IAssetProvider iAssetProvider,int guiX, int guiY, int mouseX, int mouseY, float partialTicks) {

    }
}
