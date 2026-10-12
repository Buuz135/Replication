package com.buuz135.replication.client.render.shader;

import com.buuz135.replication.Replication;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.HashMap;
import java.util.Map;

public final class ReplicationRenderTypes {

    private static final Identifier SHADER = Identifier.fromNamespaceAndPath(Replication.MOD_ID, "core/matter_pipe");
    private static final RenderPipeline MATTER_PIPE = createPipeline("matter_pipe", BlendFunction.TRANSLUCENT);
    private static final RenderPipeline MATTER_PIPE_ADDITIVE = createPipeline("matter_pipe_additive", BlendFunction.ADDITIVE);
    private static final Map<Identifier, RenderType> TRANSLUCENT_TYPES = new HashMap<>();
    private static final Map<Identifier, RenderType> ADDITIVE_TYPES = new HashMap<>();

    private ReplicationRenderTypes() {
    }

    private static RenderPipeline createPipeline(String name, BlendFunction blendFunction) {
        return RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
                .withLocation(Identifier.fromNamespaceAndPath(Replication.MOD_ID, "pipeline/" + name))
                .withVertexShader(SHADER)
                .withFragmentShader(SHADER)
                .withSampler("Sampler0")
                .withColorTargetState(new ColorTargetState(blendFunction))
                .withCull(false)
                .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
                .withDepthStencilState(DepthStencilState.DEFAULT)
                .build();
    }

    public static void register(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(MATTER_PIPE);
        event.registerPipeline(MATTER_PIPE_ADDITIVE);
    }

    public static RenderType matter(Identifier texture, boolean additive) {
        Map<Identifier, RenderType> types = additive ? ADDITIVE_TYPES : TRANSLUCENT_TYPES;
        return types.computeIfAbsent(texture, identifier -> RenderType.create(
                additive ? "replication_matter_pipe_additive" : "replication_matter_pipe",
                RenderSetup.builder(additive ? MATTER_PIPE_ADDITIVE : MATTER_PIPE)
                        .withTexture("Sampler0", identifier)
                        .sortOnUpload()
                        .createRenderSetup()
        ));
    }
}
