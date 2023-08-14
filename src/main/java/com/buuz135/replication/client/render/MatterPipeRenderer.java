package com.buuz135.replication.client.render;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.MatterPipeBlock;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.client.render.shader.ReplicationRenderTypes;
import com.buuz135.replication.client.render.shader.ShaderTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.List;

public class MatterPipeRenderer<T extends MatterPipeBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation END_SKY_LOCATION = new ResourceLocation("textures/environment/end_sky.png");
    public static final ResourceLocation END_PORTAL_LOCATION = new ResourceLocation(Replication.MOD_ID, "textures/block/pipe_render.png");

    public MatterPipeRenderer(BlockEntityRendererProvider.Context p_173689_) {
    }

    public void render(T p_112650_, float p_112651_, PoseStack p_112652_, MultiBufferSource p_112653_, int p_112654_, int p_112655_) {
        Matrix4f matrix4f = p_112652_.last().pose();
        this.renderCube(p_112650_, matrix4f, p_112653_.getBuffer(this.renderType()));
    }

    private void renderCube(T blockEntity, Matrix4f matrix, VertexConsumer consumer) {
        float f = this.getOffsetDown();
        float f1 = this.getOffsetUp();
        f1 = 0.58f;

        float min = 0.41f;
        float max = 0.58f;
        Boolean isNorth = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.NORTH));
        Boolean isSouth = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.SOUTH));
        Boolean isEast = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.EAST));
        Boolean isWest = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.WEST));

        renderTop(blockEntity, matrix, consumer, min, max, isNorth, isSouth, isEast, isWest);

//        //this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
//        //this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
//        this.renderFace(blockEntity, matrix, consumer, 0.58F, 0.58F, 0.58F, 0.41F, 0.0F, 1F, 1.0F, 0.0F, Direction.EAST);
//        //this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
//        //this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, f, f, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
//        this.renderFace(blockEntity, matrix, consumer, 0.41F, 0.58F, f1, f1, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private void renderTop(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest) {
        float uMin = blockEntity.getBlockPos().getX();
        float uMax = blockEntity.getBlockPos().getX() + 1;
        float vMin = blockEntity.getBlockPos().getZ();
        float vMax = blockEntity.getBlockPos().getZ() + 1;

        if (isNorth || isSouth || isEast || isWest) {
            consumer.vertex(matrix, min, max, max).uv(uMin + min, vMin + max).endVertex();
            consumer.vertex(matrix, max, max, max).uv(uMin + max, vMin + max).endVertex();
            consumer.vertex(matrix, max, max, min).uv(uMin + max, vMin + min).endVertex();
            consumer.vertex(matrix, min, max, min).uv(uMin + min, vMin + min).endVertex();
            if (isNorth) {
                consumer.vertex(matrix, min, max, min).uv(uMin + min, vMin + min).endVertex();
                consumer.vertex(matrix, max, max, min).uv(uMin + max, vMin + min).endVertex();
                consumer.vertex(matrix, max, max, 0).uv(uMin + max, vMin).endVertex();
                consumer.vertex(matrix, min, max, 0).uv(uMin + min, vMin).endVertex();
            }
            if (isSouth) {
                consumer.vertex(matrix, min, max, 1).uv(uMin + min, vMax).endVertex();
                consumer.vertex(matrix, max, max, 1).uv(uMin + max, vMax).endVertex();
                consumer.vertex(matrix, max, max, max).uv(uMin + max, vMin + max).endVertex();
                consumer.vertex(matrix, min, max, max).uv(uMin + min, vMin + max).endVertex();
            }
            if (isEast) {
                consumer.vertex(matrix, max, max, max).uv(uMin + max, vMin + max).endVertex();
                consumer.vertex(matrix, 1, max, max).uv(uMax, vMin + max).endVertex();
                consumer.vertex(matrix, 1, max, min).uv(uMax, vMin + min).endVertex();
                consumer.vertex(matrix, max, max, min).uv(uMin + max, vMin + min).endVertex();
            }
            if (isWest) {
                consumer.vertex(matrix, 0, max, max).uv(uMin, vMin + max).endVertex();
                consumer.vertex(matrix, min, max, max).uv(uMin + min, vMin + max).endVertex();
                consumer.vertex(matrix, min, max, min).uv(uMin + min, vMin + min).endVertex();
                consumer.vertex(matrix, 0, max, min).uv(uMin, vMin + min).endVertex();
            }
        }
    }

    private void renderFace(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float minX, float maxX, float minY, float maxY, float minZ, float maxZ, float p_173705_, Direction p_173706_) {
        if (true) {
            float uMin = blockEntity.getBlockPos().getX();
            float uMax = blockEntity.getBlockPos().getX() + 1;
            float vMin = blockEntity.getBlockPos().getZ();
            float vMax = blockEntity.getBlockPos().getZ() + 1;

            consumer.vertex(matrix, minX, minY, minZ).uv(uMin, vMax).endVertex();
            consumer.vertex(matrix, maxX, minY, maxZ).uv(uMax, vMax).endVertex();
            consumer.vertex(matrix, maxX, maxY, maxZ).uv(uMax, vMin).endVertex();
            consumer.vertex(matrix, minX, maxY, maxZ).uv(uMin, vMin).endVertex();
        }

    }

    protected float getOffsetUp() {
        return 0.75F;
    }

    protected float getOffsetDown() {
        return 0.375F;
    }

    protected RenderType renderType() {
        return ReplicationRenderTypes.getRenderType("matter_pipe")
                .using(List.of(
                        new ShaderTexture(new ResourceLocation("textures/block/amethyst_block.png"))
                ));
    }
}