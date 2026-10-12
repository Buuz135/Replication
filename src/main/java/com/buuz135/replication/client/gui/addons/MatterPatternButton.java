package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.api.pattern.MatterPattern;
import com.buuz135.replication.calculation.MatterValue;
import com.buuz135.replication.calculation.client.ClientReplicationCalculation;
import com.buuz135.replication.packet.MatterFluidSyncPacket;
import com.buuz135.replication.util.NumberUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public final class MatterPatternButton {
    private final MatterPattern pattern;
    private int cachedAmount;
    private final long createdWhen;
    private boolean shouldDisplayAnimation;

    public MatterPatternButton(MatterPattern pattern, int cachedAmount, long createdWhen, String network) {
        this.pattern = pattern;
        this.cachedAmount = cachedAmount;
        this.createdWhen = createdWhen;
        this.shouldDisplayAnimation = true;
        recalculateAmount(network);
    }

    public void render(GuiGraphicsExtractor graphics, int x, int y, double mouseX, double mouseY) {
        graphics.item(this.pattern.getStack(), x, y);
        String display;
        if (shouldDisplayAnimation) {
            long tick = Minecraft.getInstance().level == null ? 0 : Minecraft.getInstance().level.getGameTime() / 3;
            display = (tick % 3 == 0 ? "·" : ".") + (tick % 3 == 1 ? "·" : ".") + (tick % 3 == 2 ? "·" : ".");
            graphics.text(Minecraft.getInstance().font, display, x + 10, y + 8, 0xFFFFFFFF, true);
        } else {
            display = NumberUtils.getFormatedBigNumber(cachedAmount);
            float scale = 0.5F;
            graphics.pose().pushMatrix();
            graphics.pose().scale(scale, scale);
            graphics.text(Minecraft.getInstance().font, display, (int) ((x + 16) / scale) - Minecraft.getInstance().font.width(display), (int) ((y + 12) / scale), 0xFFFFFFFF, true);
            graphics.pose().popMatrix();
        }
        if (mouseX > x - 1 && mouseX < x + 17 && mouseY > y - 1 && mouseY < y + 17) {
            graphics.fill(x, y, x + 16, y + 16, 0x66FFFFFF);
            graphics.setTooltipForNextFrame(Minecraft.getInstance().font, this.pattern.getStack(), (int) mouseX, (int) mouseY);
        }
    }

    public void recalculateAmount(String network) {
        var matterCompound = ClientReplicationCalculation.getMatterCompound(pattern.getStack());
        if (matterCompound == null) {
            this.cachedAmount = 0;
            return;
        }
        this.cachedAmount = Integer.MAX_VALUE;
        var networkValues = MatterFluidSyncPacket.CLIENT_MATTER_STORAGE.get(network);
        if (networkValues == null) {
            this.cachedAmount = 0;
            return;
        }
        for (MatterValue matterValue : matterCompound.getValues().values()) {
            double amount = matterValue.getAmount();
            if (amount > 0) {
                this.cachedAmount = (int) Math.min(this.cachedAmount, Math.min(Integer.MAX_VALUE, networkValues.getOrDefault(matterValue.getMatter(), 0L) / amount));
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

    public boolean isShouldDisplayAnimation() {
        return shouldDisplayAnimation;
    }
}
