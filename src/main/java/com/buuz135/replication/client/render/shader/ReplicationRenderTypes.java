package com.buuz135.replication.client.render.shader;

import com.buuz135.replication.Replication;
import com.buuz135.replication.client.render.shader.FixedMultiTextureStateShard;
import com.buuz135.replication.client.render.shader.ShaderTexture;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ReplicationRenderTypes extends RenderType {

    private static final Map<String, ShaderRenderType> RENDER_TYPES = Util.make(() -> {
        Map<String, ShaderRenderType> map = new HashMap<>();
        map.put("matter_pipe", new ShaderRenderType("matter_pipe", DefaultVertexFormat.POSITION_TEX, (textures, renderType) -> {
            CompositeState compState = CompositeState.builder()
                    .setShaderState(renderType.shaderState)
                    .setTextureState(new FixedMultiTextureStateShard(textures))
                    .createCompositeState(false);
            return create(renderType.formattedName(), renderType.format, VertexFormat.Mode.QUADS, 256, false, false, compState);
        }));

        //noinspection Java9CollectionFactory TODO remove when you have more shaders
        return Collections.unmodifiableMap(map);
    });

    public static Map<String, ShaderRenderType> getRenderTypes() {

        return RENDER_TYPES;
    }

    public static ShaderRenderType getRenderType(String name) {

        return getRenderTypes().get(name);
    }

    private ReplicationRenderTypes(String s, VertexFormat v, VertexFormat.Mode m, int i, boolean b, boolean b2, Runnable r, Runnable r2) {

        super(s, v, m, i, b, b2, r, r2);
        throw new IllegalStateException("This class is not meant to be constructed!");
    }


    public static class ShaderRenderType {

        private final String name;
        public ShaderInstance shader;
        private final ShaderStateShard shaderState = new ShaderStateShard(() -> shader);
        private final VertexFormat format;
        private final BiFunction<List<ShaderTexture>, ShaderRenderType, RenderType> builder;
        private final ResourceLocation shaderLocation;

        public ShaderRenderType(String name, VertexFormat format, BiFunction<List<ShaderTexture>, ShaderRenderType, RenderType> builder) {

            this.name = name;
            this.format = format;
            this.builder = Util.memoize(builder);
            this.shaderLocation = new ResourceLocation(Replication.MOD_ID, this.name);
        }

        public RenderType using(List<ShaderTexture> textures) {

            return builder.apply(textures, this);
        }

        public void register(ResourceProvider resourceManager, BiConsumer<ShaderInstance, Consumer<ShaderInstance>> registerFunc) throws IOException {

            registerFunc.accept(new ShaderInstance(resourceManager, shaderLocation(), format), this::shader);
        }

        public String formattedName() {

            return "%s_%s".formatted(Replication.MOD_ID, name);
        }

        public ResourceLocation shaderLocation() {

            return shaderLocation;
        }

        public String name() {

            return name;
        }

        public VertexFormat format() {

            return format;
        }

        private void shader(ShaderInstance shader) {

            this.shader = shader;
        }

    }

}
