package com.buuz135.replication.client.gui.addons;

import com.buuz135.replication.Replication;
import com.buuz135.replication.api.IMatterType;
import com.buuz135.replication.util.NumberUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record TerminalMatterValueDisplay(IMatterType type, long amount){

    public void render(GuiGraphics guiGraphics, int x, int y, double mouseX, double mouseY) {
        var color = type.getColor().get();
        guiGraphics.setColor(color[0], color[1], color[2], color[3]);
        RenderSystem.enableBlend();
        guiGraphics.blit(new ResourceLocation(Replication.MOD_ID, "textures/gui/mattertypes/" + type.getName().toLowerCase() + ".png"), x + 2, y + 2, 0,0, 16, 16, 16, 16);
        RenderSystem.disableBlend();
        guiGraphics.setColor(1, 1, 1, 1);
        guiGraphics.pose().pushPose();
        float scale = 0.5f;
        guiGraphics.pose().scale(scale, scale, scale);
        var display = NumberUtils.getFormatedBigNumber((int) amount);
        var opacity = (int) (0.85 * 255.0F) << 24 & -16777216;
        var size = Minecraft.getInstance().font.width(display) * scale;
        guiGraphics.fill((int) ((x + 17) / scale - Minecraft.getInstance().font.width(display)), (int) ((y + 13) / scale), (int) ((x + 18 + size) / scale - Minecraft.getInstance().font.width(display)), (int) ((y + 14 + 8 * scale) / scale), 0, FastColor.ARGB32.multiply(opacity, 0xFFFFFFFF));

        guiGraphics.drawString(Minecraft.getInstance().font, display, (x + 18) / scale - Minecraft.getInstance().font.width(display), (y + 14) / scale , 0xFFFFFF, true);
//        guiGraphics.drawString(Minecraft.getInstance().font, "Matter: " + type.getName().toUpperCase(), (x + 24) / scale, (y + 6) / scale, 0x72e567, false);
//        guiGraphics.drawString(Minecraft.getInstance().font, "Amount: " + new DecimalFormat().format(amount), (x + 24) / scale, (y + 11) / scale, 0x72e567, false);
        guiGraphics.pose().popPose();

        if (mouseX > x && mouseX <= x + 18 && mouseY > y && mouseY <= y + 18) {
            List<Component> strings = new ArrayList<>();
            strings.add(Component.literal(ChatFormatting.GOLD + Component.translatable("tooltip.replication.tank.matter").getString()).append(Component.translatable("replication.matter_type." + type.getName())).withStyle(ChatFormatting.WHITE));
            strings.add(net.minecraft.network.chat.Component.translatable("tooltip.titanium.tank.amount").withStyle(ChatFormatting.GOLD).append(Component.literal(ChatFormatting.WHITE + new DecimalFormat().format(amount) +  ChatFormatting.DARK_AQUA + " matter")));
            guiGraphics.renderTooltip(Minecraft.getInstance().font, strings, Optional.empty(), (int) mouseX, (int) mouseY);
        }
    }

}
