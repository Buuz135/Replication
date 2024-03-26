package com.buuz135.replication.client.render;

import com.buuz135.replication.Replication;
import com.buuz135.replication.block.MatterPipeBlock;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.client.render.shader.ReplicationRenderTypes;
import com.buuz135.replication.client.render.shader.ShaderTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
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

        float min = 6/16F;
        float max = 10/16F;
        boolean isNorth = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.NORTH));
        boolean isSouth = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.SOUTH));
        boolean isEast = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.EAST));
        boolean isWest = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.WEST));
        boolean isUp = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.UP));
        boolean isDown = blockEntity.getBlockState().getValue(MatterPipeBlock.DIRECTIONS.get(Direction.DOWN));

        renderTop(blockEntity, matrix, consumer, min, max, isNorth, isSouth, isEast, isWest, isUp, isDown);//TODO DO UP AND DOWN
        renderBottom(blockEntity, matrix, consumer, min, max, isNorth, isSouth, isEast, isWest, isUp, isDown);
        renderNorthFace(blockEntity, matrix, consumer, min, max, isNorth, isSouth, isEast, isWest, isUp, isDown);
        renderSouthFace(blockEntity, matrix, consumer, min, max, isNorth, isSouth, isEast, isWest, isUp, isDown);
        renderWestFace(blockEntity, matrix, consumer, min, max, isNorth, isSouth, isEast, isWest, isUp, isDown);
        renderEastFace(blockEntity, matrix, consumer, min, max, isNorth, isSouth, isEast, isWest, isUp, isDown);

//        //this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, Direction.SOUTH);
//        //this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, Direction.NORTH);
//        this.renderFace(blockEntity, matrix, consumer, 0.58F, 0.58F, 0.58F, 0.41F, 0.0F, 1F, 1.0F, 0.0F, Direction.EAST);
//        //this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, Direction.WEST);
//        //this.renderFace(p_173691_, p_173692_, p_173693_, 0.0F, 1.0F, f, f, 0.0F, 0.0F, 1.0F, 1.0F, Direction.DOWN);
//        this.renderFace(blockEntity, matrix, consumer, 0.41F, 0.58F, f1, f1, 1.0F, 1.0F, 0.0F, 0.0F, Direction.UP);
    }

    private void renderTop(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getX();
        float uMax = blockEntity.getBlockPos().getX() + 1;
        float vMin = blockEntity.getBlockPos().getZ();
        float vMax = blockEntity.getBlockPos().getZ() + 1;

        if (!isUp){
            consumer.vertex(matrix, min, max, max).uv(uMin + min, vMin + max).endVertex();
            consumer.vertex(matrix, max, max, max).uv(uMin + max, vMin + max).endVertex();
            consumer.vertex(matrix, max, max, min).uv(uMin + max, vMin + min).endVertex();
            consumer.vertex(matrix, min, max, min).uv(uMin + min, vMin + min).endVertex();
        }
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

    private void renderBottom(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getX();
        float uMax = blockEntity.getBlockPos().getX() + 1;
        float vMin = blockEntity.getBlockPos().getZ();
        float vMax = blockEntity.getBlockPos().getZ() + 1;

        if (!isDown){
            consumer.vertex(matrix, min, min, min).uv(uMin + min, vMin + min).endVertex();
            consumer.vertex(matrix, max, min, min).uv(uMin + max, vMin + min).endVertex();
            consumer.vertex(matrix, max, min, max).uv(uMin + max, vMin + max).endVertex();
            consumer.vertex(matrix, min, min, max).uv(uMin + min, vMin + max).endVertex();
        }
        if (isNorth) {
            consumer.vertex(matrix, min, min, 0).uv(uMin + min, vMin).endVertex();
            consumer.vertex(matrix, max, min, 0).uv(uMin + max, vMin).endVertex();
            consumer.vertex(matrix, max, min, min).uv(uMin + max, vMin + min).endVertex();
            consumer.vertex(matrix, min, min, min).uv(uMin + min, vMin + min).endVertex();
        }
        if (isSouth) {
            consumer.vertex(matrix, min, min, max).uv(uMin + min, vMin + max).endVertex();
            consumer.vertex(matrix, max, min, max).uv(uMin + max, vMin + max).endVertex();
            consumer.vertex(matrix, max, min, 1).uv(uMin + max, vMax).endVertex();
            consumer.vertex(matrix, min, min, 1).uv(uMin + min, vMax).endVertex();
        }
        if (isEast) {
            consumer.vertex(matrix, max, min, min).uv(uMin + max, vMin + min).endVertex();
            consumer.vertex(matrix, 1, min, min).uv(uMax, vMin + min).endVertex();
            consumer.vertex(matrix, 1, min, max).uv(uMax, vMin + max).endVertex();
            consumer.vertex(matrix, max, min, max).uv(uMin + max, vMin + max).endVertex();
        }
        if (isWest) {
            consumer.vertex(matrix, 0, min, min).uv(uMin, vMin + min).endVertex();
            consumer.vertex(matrix, min, min, min).uv(uMin + min, vMin + min).endVertex();
            consumer.vertex(matrix, min, min, max).uv(uMin + min, vMin + max).endVertex();
            consumer.vertex(matrix, 0, min, max).uv(uMin, vMin + max).endVertex();
        }
    }

    private void renderNorthFace(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getX();
        float uMax = blockEntity.getBlockPos().getX() + 1;
        float vMin = blockEntity.getBlockPos().getY();
        float vMax = blockEntity.getBlockPos().getY() + 1;
        if (!isNorth){
            consumer.vertex(matrix, max, max, min).uv(uMin + max, vMin + max).endVertex(); //TOPLEFT
            consumer.vertex(matrix, max, min, min).uv(uMin + max, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, min, min, min).uv(uMin + min, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, min, max, min).uv(uMin + min, vMin + max).endVertex();//TOPRIGHT
        }
        if (isEast){
            consumer.vertex(matrix, 1, max, min).uv(uMax, vMin + max).endVertex(); //TOPLEFT
            consumer.vertex(matrix, 1, min, min).uv(uMax, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, max, min, min).uv(uMin + max, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, max, max, min).uv(uMin + max, vMin + max).endVertex();//TOPRIGHT
        }
        if (isWest){
            consumer.vertex(matrix, min, max, min).uv(uMin + min, vMin + max).endVertex(); //TOPLEFT
            consumer.vertex(matrix, min, min, min).uv(uMin + min, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, 0, min, min).uv(uMin, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, 0, max, min).uv(uMin, vMin + max).endVertex();//TOPRIGHT
        }
        if (isUp){
            consumer.vertex(matrix, max, 1, min).uv(uMin + max, vMax).endVertex(); //TOPLEFT
            consumer.vertex(matrix, max, max, min).uv(uMin + max, vMin + max).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, min, max, min).uv(uMin + min, vMin + max).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, min, 1, min).uv(uMin + min, vMax).endVertex();//TOPRIGHT
        }
        if (isDown){
            consumer.vertex(matrix, max, min, min).uv(uMin + max, vMin + min).endVertex(); //TOPLEFT
            consumer.vertex(matrix, max, 0, min).uv(uMin + max, vMin).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, min, 0, min).uv(uMin + min, vMin).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, min, min, min).uv(uMin + min, vMin + min).endVertex();//TOPRIGHT
        }
    }

    private void renderWestFace(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getZ();
        float uMax = blockEntity.getBlockPos().getZ() + 1;
        float vMin = blockEntity.getBlockPos().getY();
        float vMax = blockEntity.getBlockPos().getY() + 1;
        if (!isWest){
            consumer.vertex(matrix, min, max, min).uv(uMin + min, vMin + max).endVertex();//TOPLEFT
            consumer.vertex(matrix, min, min, min).uv(uMin + min, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, min, min, max).uv(uMin + max, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, min, max, max).uv(uMin + max, vMin + max).endVertex();//TOPRIGHT
        }
        if (isUp){
            consumer.vertex(matrix, min, 1, min).uv(uMin + min, vMax).endVertex();//TOPLEFT
            consumer.vertex(matrix, min, max, min).uv(uMin + min, vMin + max).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, min, max, max).uv(uMin + max, vMin + max).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, min, 1, max).uv(uMin + max, vMax).endVertex();//TOPRIGHT
        }
        if (isDown){
            consumer.vertex(matrix, min, min, min).uv(uMin + min, vMin + min).endVertex();//TOPLEFT
            consumer.vertex(matrix, min, 0, min).uv(uMin + min, vMin).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, min, 0, max).uv(uMin + max, vMin).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, min, min, max).uv(uMin + max, vMin + min).endVertex();//TOPRIGHT
        }
        if (isNorth){
            consumer.vertex(matrix, min, max, 0).uv(uMin, vMin + max).endVertex();//TOPLEFT
            consumer.vertex(matrix, min, min, 0).uv(uMin, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, min, min, min).uv(uMin + min, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, min, max, min).uv(uMin + min, vMin + max).endVertex();//TOPRIGHT
        }
        if (isSouth){
            consumer.vertex(matrix, min, max, max).uv(uMin + max, vMin + max).endVertex();//TOPLEFT
            consumer.vertex(matrix, min, min, max).uv(uMin + max, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, min, min, 1).uv(uMax, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, min, max, 1).uv(uMax, vMin + max).endVertex();//TOPRIGHT
        }
    }

    private void renderEastFace(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getZ();
        float uMax = blockEntity.getBlockPos().getZ() + 1;
        float vMin = blockEntity.getBlockPos().getY();
        float vMax = blockEntity.getBlockPos().getY() + 1;
        if (!isEast){
            consumer.vertex(matrix, max, max, max).uv(uMin + max, vMin + max).endVertex();//TOPLEFT
            consumer.vertex(matrix, max, min, max).uv(uMin + max, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, max, min, min).uv(uMin + min, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, max, max, min).uv(uMin + min, vMin + max).endVertex();//TOPRIGHT
        }
        if (isUp){
            consumer.vertex(matrix, max, 1, max).uv(uMin + max, vMax).endVertex();//TOPLEFT
            consumer.vertex(matrix, max, max, max).uv(uMin + max, vMin + max).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, max, max, min).uv(uMin + min, vMin + max).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, max, 1, min).uv(uMin + min, vMax).endVertex();//TOPRIGHT
        }
        if (isDown){
            consumer.vertex(matrix, max, min, max).uv(uMin + max, vMin + min).endVertex();//TOPLEFT
            consumer.vertex(matrix, max, 0, max).uv(uMin + max, vMin).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, max, 0, min).uv(uMin + min, vMin).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, max, min, min).uv(uMin + min, vMin + min).endVertex();//TOPRIGHT
        }
        if (isNorth){
            consumer.vertex(matrix, max, max, min).uv(uMin + min, vMin + max).endVertex();//TOPLEFT
            consumer.vertex(matrix, max, min, min).uv(uMin + min, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, max, min, 0).uv(uMin, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, max, max, 0).uv(uMin, vMin + max).endVertex();//TOPRIGHT
        }
        if (isSouth){
            consumer.vertex(matrix, max, max, 1).uv(uMax, vMin + max).endVertex();//TOPLEFT
            consumer.vertex(matrix, max, min, 1).uv(uMax, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, max, min, max).uv(uMin + max, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, max, max, max).uv(uMin + max, vMin + max).endVertex();//TOPRIGHT
        }
    }


    private void renderSouthFace(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getX();
        float uMax = blockEntity.getBlockPos().getX() + 1;
        float vMin = blockEntity.getBlockPos().getY();
        float vMax = blockEntity.getBlockPos().getY() + 1;
        if (!isSouth){
            consumer.vertex(matrix, min, max, max).uv(uMin + min, vMin + max).endVertex();//TOPLEFT
            consumer.vertex(matrix, min, min, max).uv(uMin + min, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, max, min, max).uv(uMin + max, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, max, max, max).uv(uMin + max, vMin + max).endVertex(); //TOPRIGHT
        }
        if (isEast){
            consumer.vertex(matrix, max, max, max).uv(uMin + max, vMin + max).endVertex();//TOPLEFT
            consumer.vertex(matrix, max, min, max).uv(uMin + max, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, 1, min, max).uv(uMax, vMin + min).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, 1, max, max).uv(uMax, vMin + max).endVertex(); //TOPRIGHT
        }
        if (isWest){
            consumer.vertex(matrix, 0, min, max).uv(uMin, vMin + min).endVertex();//TOPLEFT
            consumer.vertex(matrix, min, min, max).uv(uMin + min, vMin + min).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, min, max, max).uv(uMin + min, vMin + max).endVertex(); //BOTTOMRIGHT
            consumer.vertex(matrix, 0, max, max).uv(uMin, vMin + max).endVertex();//TOPRIGHT
        }
        if (isUp){
            consumer.vertex(matrix, min, 1, max).uv(uMin + min, vMax).endVertex();//TOPLEFT
            consumer.vertex(matrix, min, max, max).uv(uMin + min, vMin + max).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, max, max, max).uv(uMin + max, vMin + max).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, max, 1, max).uv(uMin + max, vMax).endVertex(); //TOPRIGHT
        }
        if (isDown){
            consumer.vertex(matrix, min, min, max).uv(uMin + min, vMin + min).endVertex();//TOPLEFT
            consumer.vertex(matrix, min, 0, max).uv(uMin + min, vMin).endVertex();//BOTTOMLEFT
            consumer.vertex(matrix, max, 0, max).uv(uMin + max, vMin).endVertex();//BOTTOMRIGHT
            consumer.vertex(matrix, max, min, max).uv(uMin + max, vMin + min).endVertex(); //TOPRIGHT
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

    private void renderFaces(RenderType renderType, PoseStack stack, MultiBufferSource renderTypeBuffer, AABB pos, double x, double y, double z, float red, float green, float blue, float alpha) {
        float x1 = (float) (pos.minX + x);
        float x2 = (float) (pos.maxX + x);
        float y1 = (float) (pos.minY + y);
        float y2 = (float) (pos.maxY + y);
        float z1 = (float) (pos.minZ + z);
        float z2 = (float) (pos.maxZ + z);

        Matrix4f matrix = stack.last().pose();
        VertexConsumer buffer;

        buffer = renderTypeBuffer.getBuffer(renderType);

        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();

        buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();


        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();

        buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();


        buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();

        buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
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
                        new ShaderTexture(new ResourceLocation("replication:textures/block/shader.png"))

                ));
    }
}