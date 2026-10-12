package com.buuz135.replication.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

final class ReplicationRenderHelper {

    private ReplicationRenderHelper() {
    }

    static void rotate(PoseStack poseStack, Direction facing) {
        if (facing == Direction.EAST) {
            poseStack.translate(1, 0, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
        } else if (facing == Direction.SOUTH) {
            poseStack.translate(1, 0, 1);
            poseStack.mulPose(Axis.YP.rotationDegrees(-180));
        } else if (facing == Direction.WEST) {
            poseStack.translate(0, 0, 1);
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }
    }

    static void colorBox(PoseStack.Pose pose, VertexConsumer buffer, AABB box, int color) {
        float x1 = (float) box.minX;
        float x2 = (float) box.maxX;
        float y1 = (float) box.minY;
        float y2 = (float) box.maxY;
        float z1 = (float) box.minZ;
        float z2 = (float) box.maxZ;
        vertex(buffer, pose, x1, y1, z1, color);
        vertex(buffer, pose, x1, y2, z1, color);
        vertex(buffer, pose, x2, y2, z1, color);
        vertex(buffer, pose, x2, y1, z1, color);
        vertex(buffer, pose, x1, y1, z2, color);
        vertex(buffer, pose, x2, y1, z2, color);
        vertex(buffer, pose, x2, y2, z2, color);
        vertex(buffer, pose, x1, y2, z2, color);
        vertex(buffer, pose, x1, y1, z1, color);
        vertex(buffer, pose, x2, y1, z1, color);
        vertex(buffer, pose, x2, y1, z2, color);
        vertex(buffer, pose, x1, y1, z2, color);
        vertex(buffer, pose, x1, y2, z1, color);
        vertex(buffer, pose, x1, y2, z2, color);
        vertex(buffer, pose, x2, y2, z2, color);
        vertex(buffer, pose, x2, y2, z1, color);
        vertex(buffer, pose, x1, y1, z1, color);
        vertex(buffer, pose, x1, y1, z2, color);
        vertex(buffer, pose, x1, y2, z2, color);
        vertex(buffer, pose, x1, y2, z1, color);
        vertex(buffer, pose, x2, y1, z1, color);
        vertex(buffer, pose, x2, y2, z1, color);
        vertex(buffer, pose, x2, y2, z2, color);
        vertex(buffer, pose, x2, y1, z2, color);
    }

    static void texturedBox(PoseStack.Pose pose, VertexConsumer buffer, AABB box, int color, int light, float u0, float v0, float u1, float v1) {
        float x1 = (float) box.minX;
        float x2 = (float) box.maxX;
        float y1 = (float) box.minY;
        float y2 = (float) box.maxY;
        float z1 = (float) box.minZ;
        float z2 = (float) box.maxZ;
        texturedQuad(pose, buffer, color, light, x1, y1, z1, x1, y2, z1, x2, y2, z1, x2, y1, z1, u0, v0, u1, v1, 0, 0, -1);
        texturedQuad(pose, buffer, color, light, x1, y1, z2, x2, y1, z2, x2, y2, z2, x1, y2, z2, u0, v0, u1, v1, 0, 0, 1);
        texturedQuad(pose, buffer, color, light, x1, y1, z1, x2, y1, z1, x2, y1, z2, x1, y1, z2, u0, v0, u1, v1, 0, -1, 0);
        texturedQuad(pose, buffer, color, light, x1, y2, z1, x1, y2, z2, x2, y2, z2, x2, y2, z1, u0, v0, u1, v1, 0, 1, 0);
        texturedQuad(pose, buffer, color, light, x1, y1, z1, x1, y1, z2, x1, y2, z2, x1, y2, z1, u0, v0, u1, v1, -1, 0, 0);
        texturedQuad(pose, buffer, color, light, x2, y1, z1, x2, y2, z1, x2, y2, z2, x2, y1, z2, u0, v0, u1, v1, 1, 0, 0);
    }

    private static void vertex(VertexConsumer buffer, PoseStack.Pose pose, float x, float y, float z, int color) {
        buffer.addVertex(pose.pose(), x, y, z).setColor(color);
    }

    private static void texturedQuad(PoseStack.Pose pose, VertexConsumer buffer, int color, int light,
                                     float x1, float y1, float z1, float x2, float y2, float z2,
                                     float x3, float y3, float z3, float x4, float y4, float z4,
                                     float u0, float v0, float u1, float v1, float nx, float ny, float nz) {
        texturedVertex(pose, buffer, color, light, x1, y1, z1, u0, v1, nx, ny, nz);
        texturedVertex(pose, buffer, color, light, x2, y2, z2, u0, v0, nx, ny, nz);
        texturedVertex(pose, buffer, color, light, x3, y3, z3, u1, v0, nx, ny, nz);
        texturedVertex(pose, buffer, color, light, x4, y4, z4, u1, v1, nx, ny, nz);
    }

    private static void texturedVertex(PoseStack.Pose pose, VertexConsumer buffer, int color, int light,
                                       float x, float y, float z, float u, float v, float nx, float ny, float nz) {
        buffer.addVertex(pose.pose(), x, y, z)
                .setColor(color)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, nx, ny, nz);
    }
}
