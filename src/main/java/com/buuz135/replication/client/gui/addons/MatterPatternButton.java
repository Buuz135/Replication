package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.api.pattern.MatterPattern;
import com.buuz135.replication.calculation.MatterValue;
import com.buuz135.replication.calculation.client.ClientReplicationCalculation;
import com.buuz135.replication.packet.MatterFluidSyncPacket;
import com.buuz135.replication.util.NumberUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

public final class MatterPatternButton {
    private final MatterPattern pattern;
    private int cachedAmount;
    private long createdWhen;
    private boolean shouldDisplayAnimation;

    public MatterPatternButton(MatterPattern pattern, int cachedAmount, long createdWhen, String network) {
        this.pattern = pattern;
        this.cachedAmount = cachedAmount;
        this.createdWhen = createdWhen;
        this.shouldDisplayAnimation = true;
        recalculateAmount(network);
    }

    public void render(GuiGraphics guiGraphics, int x, int y, double mouseX, double mouseY) {
        guiGraphics.renderItem(this.pattern.getStack(), x, y);
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, 0, 200);
        if (shouldDisplayAnimation) {
            var speed = 3;
            guiGraphics.drawString(Minecraft.getInstance().font, ((Minecraft.getInstance().level.getGameTime() / speed) % 3 == 0 ? "·" : ".")
                    + ((Minecraft.getInstance().level.getGameTime() / speed) % 3 == 1 ? "·" : ".")
                    + ((Minecraft.getInstance().level.getGameTime() / speed) % 3 == 2 ? "·" : "."), x + 10, y + 8, 0xFFFFFF, true);
        } else {
            var scale = 0.5f;
            var display = NumberUtils.getFormatedBigNumber(cachedAmount);
            guiGraphics.pose().scale(scale, scale, scale);
            guiGraphics.drawString(Minecraft.getInstance().font, display, (x + 16 ) / scale - Minecraft.getInstance().font.width(display), (y + 12) / scale , 0xFFFFFF, true);
        }
        guiGraphics.pose().popPose();
        if (mouseX > x - 1 && mouseX < x + 17 && mouseY > y - 1 && mouseY < y + 17) {
            AbstractContainerScreen.renderSlotHighlight(guiGraphics, x, y, 1);
            guiGraphics.renderTooltip(Minecraft.getInstance().font, this.pattern.getStack(), (int) mouseX, (int) mouseY);
        }
    }

    public void recalculateAmount(String network) {
        var matterCompound = ClientReplicationCalculation.getMatterCompound(pattern.getStack());
        if (matterCompound == null) {
            this.cachedAmount = 0;
        } else {
            this.cachedAmount = Integer.MAX_VALUE;
            var networkValues = MatterFluidSyncPacket.CLIENT_MATTER_STORAGE.get(network);
            if (networkValues != null){
                for (MatterValue matterValue : matterCompound.getValues().values()) {
                    var matterType = matterValue.getMatter();
                    var amount = matterValue.getAmount();
                    this.cachedAmount = (int) Math.min(this.cachedAmount, Math.min(Integer.MAX_VALUE, networkValues.getOrDefault(matterType, 0L) / amount));
                }
            }
        }
    }

    public void setShouldDisplayAnimation(boolean shouldDisplayAnimation) {
        this.shouldDisplayAnimation = shouldDisplayAnimation;
    }

    public MatterPattern pattern() {
        return pattern;
    }

    public int cachedAmount() {
        return cachedAmount;
    }

    public long createdWhen() {
        return createdWhen;
    }

    public void setCachedAmount(int cachedAmount) {
        this.cachedAmount = cachedAmount;
    }

    public void setCreatedWhen(long createdWhen) {
        this.createdWhen = createdWhen;
    }

    public boolean isShouldDisplayAnimation() {
        return shouldDisplayAnimation;
    }
}
