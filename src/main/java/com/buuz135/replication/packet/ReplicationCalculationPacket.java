package com.buuz135.replication.packet;

import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.calculation.client.ClientReplicationCalculation;
import com.buuz135.replication.client.MatterCalculationStatusToast;
import com.hrznstudio.titanium.network.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ReplicationCalculationPacket extends Message {

    public CompoundTag data;

    public ReplicationCalculationPacket(CompoundTag data) {
        this.data = data;
    }

    public ReplicationCalculationPacket() {
    }

    @Override
    protected void handleMessage(IPayloadContext context) {
        context.enqueueWork(() -> {
            handle(context);
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void handle(IPayloadContext context) {
        ClientReplicationCalculation.acceptData(context.player().level().registryAccess(), data);
        var subtext = Component.literal("Matter Values Synced").withStyle(style -> style.withColor(0x72e567));
            /*if (state == AnalysisState.ERRORED){
                subtext = Component.literal("Error").withStyle(style -> style.withColor(ChatFormatting.RED));
            }*/
        var toast = new MatterCalculationStatusToast(new ItemStack(ReplicationRegistry.Blocks.REPLICATOR.getBlock()),
                Component.literal("Replication").withStyle(style -> style.withBold(true).withColor(0x72e567)),
                subtext, false);
        Minecraft.getInstance().getToasts().addToast(toast);
        new Thread(() -> {
            try {
                Thread.sleep(5 * 1000);
                toast.hide();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
