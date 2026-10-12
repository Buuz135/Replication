package com.buuz135.replication.client;

import com.buuz135.replication.Replication;
import com.buuz135.replication.calculation.MatterValue;
import com.buuz135.replication.util.NumberUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

import java.awt.*;

public class MatterTooltipClientComponent implements ClientTooltipComponent {

    public static final Identifier BAR = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/matter_bar.png");
    private final MatterTooltipComponent instance;

    public MatterTooltipClientComponent(MatterTooltipComponent instance) {
        this.instance = instance;
    }

    @Override
    public int getHeight(Font font) {
        return 24;
    }

    @Override
    public int getWidth(Font font) {
        return 19 * instance.getInstance().getValues().size();
    }

    @Override
    public void extractImage(Font font, int x, int y, int width, int height, GuiGraphicsExtractor graphics) {
        int xModifier = 0;
        for (MatterValue value : this.instance.getInstance().getValues().values()) {
            renderInformationPill(value, graphics, x + xModifier, y);
            xModifier += 19;
        }
    }

    private void renderInformationPill(MatterValue value, GuiGraphicsExtractor graphics, int x, int y) {
        float[] values = value.getMatter().getColor().get();
        int color = new Color(values[0], values[1], values[2], values[3]).getRGB();
        int offset = Minecraft.getInstance().level != null && Minecraft.getInstance().level.getRandom().nextDouble() < 0.001 ? 1 : 0;
        Identifier texture = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/mattertypes/" + value.getMatter().getName().toLowerCase() + ".png");
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + offset, y + offset, 0, 0, 16, 16, 16, 16, color);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BAR, x + offset - 1, y + offset - 1, 60, 57, 18, 22, 128, 128, color);
        String number = NumberUtils.getFormatedBigNumber(value.getAmount());
        graphics.pose().pushMatrix();
        graphics.pose().scale(0.5F, 0.5F);
        graphics.centeredText(Minecraft.getInstance().font, number, (int) ((x + offset + 8) / 0.5F), (int) ((y + offset + 15) / 0.5F), color);
        graphics.pose().popMatrix();
    }
}
