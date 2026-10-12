package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.util.NumberUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public record TerminalMatterValueDisplay(IMatterType type, long amount) {

    public void render(GuiGraphicsExtractor graphics, int x, int y, double mouseX, double mouseY) {
        float[] values = type.getColor().get();
        int color = new Color(values[0], values[1], values[2], values[3]).getRGB();
        Identifier texture = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "textures/gui/mattertypes/" + type.getName().toLowerCase() + ".png");
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x + 2, y + 2, 0, 0, 16, 16, 16, 16, color);
        String display = NumberUtils.getFormatedBigNumber(amount);
        float scale = 0.5F;
        int width = Minecraft.getInstance().font.width(display);
        graphics.pose().pushMatrix();
        graphics.pose().scale(scale, scale);
        graphics.fill((int) ((x + 17) / scale) - width, (int) ((y + 13) / scale), (int) ((x + 18) / scale), (int) ((y + 18) / scale), 0xD9000000);
        graphics.text(Minecraft.getInstance().font, display, (int) ((x + 18) / scale) - width, (int) ((y + 14) / scale), 0xFFFFFFFF, true);
        graphics.pose().popMatrix();
        if (mouseX > x && mouseX <= x + 18 && mouseY > y && mouseY <= y + 18) {
            List<Component> lines = List.of(
                    Component.translatable("tooltip.replication.tank.matter").withStyle(ChatFormatting.GOLD).append(Component.translatable("replication.matter_type." + type.getName()).withStyle(ChatFormatting.WHITE)),
                    Component.translatable("tooltip.titanium.tank.amount").withStyle(ChatFormatting.GOLD).append(Component.literal(new DecimalFormat().format(amount)).withStyle(ChatFormatting.WHITE)).append(Component.translatable("tooltip.replication.tank.unit").withStyle(ChatFormatting.DARK_AQUA))
            );
            graphics.setComponentTooltipForNextFrame(Minecraft.getInstance().font, lines, (int) mouseX, (int) mouseY);
        }
    }
}
