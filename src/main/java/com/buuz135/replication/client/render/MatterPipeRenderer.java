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
    public static final ResourceLocation END_SKY_LOCATION = ResourceLocation.fromNamespaceAndPath("minecraft","textures/environment/end_sky.png");
    public static final ResourceLocation END_PORTAL_LOCATION = ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "textures/block/pipe_render.png");

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
            consumer.addVertex(matrix, min, max, max).setUv(uMin + min, vMin + max);
            consumer.addVertex(matrix, max, max, max).setUv(uMin + max, vMin + max);
            consumer.addVertex(matrix, max, max, min).setUv(uMin + max, vMin + min);
            consumer.addVertex(matrix, min, max, min).setUv(uMin + min, vMin + min);
        }
        if (isNorth) {
            consumer.addVertex(matrix, min, max, min).setUv(uMin + min, vMin + min);
            consumer.addVertex(matrix, max, max, min).setUv(uMin + max, vMin + min);
            consumer.addVertex(matrix, max, max, 0).setUv(uMin + max, vMin);
            consumer.addVertex(matrix, min, max, 0).setUv(uMin + min, vMin);
        }
        if (isSouth) {
            consumer.addVertex(matrix, min, max, 1).setUv(uMin + min, vMax);
            consumer.addVertex(matrix, max, max, 1).setUv(uMin + max, vMax);
            consumer.addVertex(matrix, max, max, max).setUv(uMin + max, vMin + max);
            consumer.addVertex(matrix, min, max, max).setUv(uMin + min, vMin + max);
        }
        if (isEast) {
            consumer.addVertex(matrix, max, max, max).setUv(uMin + max, vMin + max);
            consumer.addVertex(matrix, 1, max, max).setUv(uMax, vMin + max);
            consumer.addVertex(matrix, 1, max, min).setUv(uMax, vMin + min);
            consumer.addVertex(matrix, max, max, min).setUv(uMin + max, vMin + min);
        }
        if (isWest) {
            consumer.addVertex(matrix, 0, max, max).setUv(uMin, vMin + max);
            consumer.addVertex(matrix, min, max, max).setUv(uMin + min, vMin + max);
            consumer.addVertex(matrix, min, max, min).setUv(uMin + min, vMin + min);
            consumer.addVertex(matrix, 0, max, min).setUv(uMin, vMin + min);
        }
    }

    private void renderBottom(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getX();
        float uMax = blockEntity.getBlockPos().getX() + 1;
        float vMin = blockEntity.getBlockPos().getZ();
        float vMax = blockEntity.getBlockPos().getZ() + 1;

        if (!isDown){
            consumer.addVertex(matrix, min, min, min).setUv(uMin + min, vMin + min);
            consumer.addVertex(matrix, max, min, min).setUv(uMin + max, vMin + min);
            consumer.addVertex(matrix, max, min, max).setUv(uMin + max, vMin + max);
            consumer.addVertex(matrix, min, min, max).setUv(uMin + min, vMin + max);
        }
        if (isNorth) {
            consumer.addVertex(matrix, min, min, 0).setUv(uMin + min, vMin);
            consumer.addVertex(matrix, max, min, 0).setUv(uMin + max, vMin);
            consumer.addVertex(matrix, max, min, min).setUv(uMin + max, vMin + min);
            consumer.addVertex(matrix, min, min, min).setUv(uMin + min, vMin + min);
        }
        if (isSouth) {
            consumer.addVertex(matrix, min, min, max).setUv(uMin + min, vMin + max);
            consumer.addVertex(matrix, max, min, max).setUv(uMin + max, vMin + max);
            consumer.addVertex(matrix, max, min, 1).setUv(uMin + max, vMax);
            consumer.addVertex(matrix, min, min, 1).setUv(uMin + min, vMax);
        }
        if (isEast) {
            consumer.addVertex(matrix, max, min, min).setUv(uMin + max, vMin + min);
            consumer.addVertex(matrix, 1, min, min).setUv(uMax, vMin + min);
            consumer.addVertex(matrix, 1, min, max).setUv(uMax, vMin + max);
            consumer.addVertex(matrix, max, min, max).setUv(uMin + max, vMin + max);
        }
        if (isWest) {
            consumer.addVertex(matrix, 0, min, min).setUv(uMin, vMin + min);
            consumer.addVertex(matrix, min, min, min).setUv(uMin + min, vMin + min);
            consumer.addVertex(matrix, min, min, max).setUv(uMin + min, vMin + max);
            consumer.addVertex(matrix, 0, min, max).setUv(uMin, vMin + max);
        }
    }

    private void renderNorthFace(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getX();
        float uMax = blockEntity.getBlockPos().getX() + 1;
        float vMin = blockEntity.getBlockPos().getY();
        float vMax = blockEntity.getBlockPos().getY() + 1;
        if (!isNorth){
            consumer.addVertex(matrix, max, max, min).setUv(uMin + max, vMin + max); //TOPLEFT
            consumer.addVertex(matrix, max, min, min).setUv(uMin + max, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, min, min, min).setUv(uMin + min, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, min, max, min).setUv(uMin + min, vMin + max);//TOPRIGHT
        }
        if (isEast){
            consumer.addVertex(matrix, 1, max, min).setUv(uMax, vMin + max); //TOPLEFT
            consumer.addVertex(matrix, 1, min, min).setUv(uMax, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, max, min, min).setUv(uMin + max, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, max, max, min).setUv(uMin + max, vMin + max);//TOPRIGHT
        }
        if (isWest){
            consumer.addVertex(matrix, min, max, min).setUv(uMin + min, vMin + max); //TOPLEFT
            consumer.addVertex(matrix, min, min, min).setUv(uMin + min, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, 0, min, min).setUv(uMin, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, 0, max, min).setUv(uMin, vMin + max);//TOPRIGHT
        }
        if (isUp){
            consumer.addVertex(matrix, max, 1, min).setUv(uMin + max, vMax); //TOPLEFT
            consumer.addVertex(matrix, max, max, min).setUv(uMin + max, vMin + max);//BOTTOMLEFT
            consumer.addVertex(matrix, min, max, min).setUv(uMin + min, vMin + max);//BOTTOMRIGHT
            consumer.addVertex(matrix, min, 1, min).setUv(uMin + min, vMax);//TOPRIGHT
        }
        if (isDown){
            consumer.addVertex(matrix, max, min, min).setUv(uMin + max, vMin + min); //TOPLEFT
            consumer.addVertex(matrix, max, 0, min).setUv(uMin + max, vMin);//BOTTOMLEFT
            consumer.addVertex(matrix, min, 0, min).setUv(uMin + min, vMin);//BOTTOMRIGHT
            consumer.addVertex(matrix, min, min, min).setUv(uMin + min, vMin + min);//TOPRIGHT
        }
    }

    private void renderWestFace(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getZ();
        float uMax = blockEntity.getBlockPos().getZ() + 1;
        float vMin = blockEntity.getBlockPos().getY();
        float vMax = blockEntity.getBlockPos().getY() + 1;
        if (!isWest){
            consumer.addVertex(matrix, min, max, min).setUv(uMin + min, vMin + max);//TOPLEFT
            consumer.addVertex(matrix, min, min, min).setUv(uMin + min, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, min, min, max).setUv(uMin + max, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, min, max, max).setUv(uMin + max, vMin + max);//TOPRIGHT
        }
        if (isUp){
            consumer.addVertex(matrix, min, 1, min).setUv(uMin + min, vMax);//TOPLEFT
            consumer.addVertex(matrix, min, max, min).setUv(uMin + min, vMin + max);//BOTTOMLEFT
            consumer.addVertex(matrix, min, max, max).setUv(uMin + max, vMin + max);//BOTTOMRIGHT
            consumer.addVertex(matrix, min, 1, max).setUv(uMin + max, vMax);//TOPRIGHT
        }
        if (isDown){
            consumer.addVertex(matrix, min, min, min).setUv(uMin + min, vMin + min);//TOPLEFT
            consumer.addVertex(matrix, min, 0, min).setUv(uMin + min, vMin);//BOTTOMLEFT
            consumer.addVertex(matrix, min, 0, max).setUv(uMin + max, vMin);//BOTTOMRIGHT
            consumer.addVertex(matrix, min, min, max).setUv(uMin + max, vMin + min);//TOPRIGHT
        }
        if (isNorth){
            consumer.addVertex(matrix, min, max, 0).setUv(uMin, vMin + max);//TOPLEFT
            consumer.addVertex(matrix, min, min, 0).setUv(uMin, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, min, min, min).setUv(uMin + min, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, min, max, min).setUv(uMin + min, vMin + max);//TOPRIGHT
        }
        if (isSouth){
            consumer.addVertex(matrix, min, max, max).setUv(uMin + max, vMin + max);//TOPLEFT
            consumer.addVertex(matrix, min, min, max).setUv(uMin + max, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, min, min, 1).setUv(uMax, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, min, max, 1).setUv(uMax, vMin + max);//TOPRIGHT
        }
    }

    private void renderEastFace(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getZ();
        float uMax = blockEntity.getBlockPos().getZ() + 1;
        float vMin = blockEntity.getBlockPos().getY();
        float vMax = blockEntity.getBlockPos().getY() + 1;
        if (!isEast){
            consumer.addVertex(matrix, max, max, max).setUv(uMin + max, vMin + max);//TOPLEFT
            consumer.addVertex(matrix, max, min, max).setUv(uMin + max, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, max, min, min).setUv(uMin + min, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, max, max, min).setUv(uMin + min, vMin + max);//TOPRIGHT
        }
        if (isUp){
            consumer.addVertex(matrix, max, 1, max).setUv(uMin + max, vMax);//TOPLEFT
            consumer.addVertex(matrix, max, max, max).setUv(uMin + max, vMin + max);//BOTTOMLEFT
            consumer.addVertex(matrix, max, max, min).setUv(uMin + min, vMin + max);//BOTTOMRIGHT
            consumer.addVertex(matrix, max, 1, min).setUv(uMin + min, vMax);//TOPRIGHT
        }
        if (isDown){
            consumer.addVertex(matrix, max, min, max).setUv(uMin + max, vMin + min);//TOPLEFT
            consumer.addVertex(matrix, max, 0, max).setUv(uMin + max, vMin);//BOTTOMLEFT
            consumer.addVertex(matrix, max, 0, min).setUv(uMin + min, vMin);//BOTTOMRIGHT
            consumer.addVertex(matrix, max, min, min).setUv(uMin + min, vMin + min);//TOPRIGHT
        }
        if (isNorth){
            consumer.addVertex(matrix, max, max, min).setUv(uMin + min, vMin + max);//TOPLEFT
            consumer.addVertex(matrix, max, min, min).setUv(uMin + min, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, max, min, 0).setUv(uMin, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, max, max, 0).setUv(uMin, vMin + max);//TOPRIGHT
        }
        if (isSouth){
            consumer.addVertex(matrix, max, max, 1).setUv(uMax, vMin + max);//TOPLEFT
            consumer.addVertex(matrix, max, min, 1).setUv(uMax, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, max, min, max).setUv(uMin + max, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, max, max, max).setUv(uMin + max, vMin + max);//TOPRIGHT
        }
    }


    private void renderSouthFace(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float min, float max, boolean isNorth, boolean isSouth, boolean isEast, boolean isWest, boolean isUp, boolean isDown) {
        float uMin = blockEntity.getBlockPos().getX();
        float uMax = blockEntity.getBlockPos().getX() + 1;
        float vMin = blockEntity.getBlockPos().getY();
        float vMax = blockEntity.getBlockPos().getY() + 1;
        if (!isSouth){
            consumer.addVertex(matrix, min, max, max).setUv(uMin + min, vMin + max);//TOPLEFT
            consumer.addVertex(matrix, min, min, max).setUv(uMin + min, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, max, min, max).setUv(uMin + max, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, max, max, max).setUv(uMin + max, vMin + max); //TOPRIGHT
        }
        if (isEast){
            consumer.addVertex(matrix, max, max, max).setUv(uMin + max, vMin + max);//TOPLEFT
            consumer.addVertex(matrix, max, min, max).setUv(uMin + max, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, 1, min, max).setUv(uMax, vMin + min);//BOTTOMRIGHT
            consumer.addVertex(matrix, 1, max, max).setUv(uMax, vMin + max); //TOPRIGHT
        }
        if (isWest){
            consumer.addVertex(matrix, 0, min, max).setUv(uMin, vMin + min);//TOPLEFT
            consumer.addVertex(matrix, min, min, max).setUv(uMin + min, vMin + min);//BOTTOMLEFT
            consumer.addVertex(matrix, min, max, max).setUv(uMin + min, vMin + max); //BOTTOMRIGHT
            consumer.addVertex(matrix, 0, max, max).setUv(uMin, vMin + max);//TOPRIGHT
        }
        if (isUp){
            consumer.addVertex(matrix, min, 1, max).setUv(uMin + min, vMax);//TOPLEFT
            consumer.addVertex(matrix, min, max, max).setUv(uMin + min, vMin + max);//BOTTOMLEFT
            consumer.addVertex(matrix, max, max, max).setUv(uMin + max, vMin + max);//BOTTOMRIGHT
            consumer.addVertex(matrix, max, 1, max).setUv(uMin + max, vMax); //TOPRIGHT
        }
        if (isDown){
            consumer.addVertex(matrix, min, min, max).setUv(uMin + min, vMin + min);//TOPLEFT
            consumer.addVertex(matrix, min, 0, max).setUv(uMin + min, vMin);//BOTTOMLEFT
            consumer.addVertex(matrix, max, 0, max).setUv(uMin + max, vMin);//BOTTOMRIGHT
            consumer.addVertex(matrix, max, min, max).setUv(uMin + max, vMin + min); //TOPRIGHT
        }
    }

    private void renderFace(T blockEntity, Matrix4f matrix, VertexConsumer consumer, float minX, float maxX, float minY, float maxY, float minZ, float maxZ, float p_173705_, Direction p_173706_) {
        if (true) {
            float uMin = blockEntity.getBlockPos().getX();
            float uMax = blockEntity.getBlockPos().getX() + 1;
            float vMin = blockEntity.getBlockPos().getZ();
            float vMax = blockEntity.getBlockPos().getZ() + 1;

            consumer.addVertex(matrix, minX, minY, minZ).setUv(uMin, vMax);
            consumer.addVertex(matrix, maxX, minY, maxZ).setUv(uMax, vMax);
            consumer.addVertex(matrix, maxX, maxY, maxZ).setUv(uMax, vMin);
            consumer.addVertex(matrix, minX, maxY, maxZ).setUv(uMin, vMin);
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

        buffer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y2, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y1, z1).setColor(red, green, blue, alpha);

        buffer.addVertex(matrix, x1, y1, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y1, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y2, z2).setColor(red, green, blue, alpha);


        buffer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y1, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y1, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y1, z2).setColor(red, green, blue, alpha);

        buffer.addVertex(matrix, x1, y2, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y2, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z1).setColor(red, green, blue, alpha);


        buffer.addVertex(matrix, x1, y1, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y1, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y2, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x1, y2, z1).setColor(red, green, blue, alpha);

        buffer.addVertex(matrix, x2, y1, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z1).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y2, z2).setColor(red, green, blue, alpha);
        buffer.addVertex(matrix, x2, y1, z2).setColor(red, green, blue, alpha);
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
                        new ShaderTexture(ResourceLocation.parse("replication:textures/block/shader.png"))

                ));
    }
}