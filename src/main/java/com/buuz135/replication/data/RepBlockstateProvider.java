package com.buuz135.replication.data;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.MatterPipeBlock;
import com.hrznstudio.titanium.block.RotatableBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.ArrayList;
import java.util.List;

public class RepBlockstateProvider extends BlockStateProvider {

    private List<Block> blocks;

    public RepBlockstateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper, List<Block> blocks) {
        super(gen.getPackOutput(), modid, exFileHelper);
        this.blocks = blocks;
    }

    @Override
    protected void registerStatesAndModels() {
        var pipe = getMultipartBuilder(ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getBlock());
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_middle"))).addModel();
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.NORTH), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationY(180).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.SOUTH), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationY(90).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.EAST), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationY(270).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.WEST), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationX(90).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.DOWN), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationX(270).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.UP), true);

        this.blocks.stream().filter(blockBase -> blockBase instanceof RotatableBlock<?>)
                .map(blockBase -> (RotatableBlock) blockBase)
                .forEach(rotatableBlock -> {
                    VariantBlockStateBuilder builder = getVariantBuilder(rotatableBlock);
                    if (rotatableBlock.getRotationType().getProperties().length > 0) {
                        for (DirectionProperty property : rotatableBlock.getRotationType().getProperties()) {
                            for (Direction allowedValue : property.getPossibleValues()) {
                                builder.partialState().with(property, allowedValue)
                                        .addModels(new ConfiguredModel(new ModelFile.UncheckedModelFile(getModel(rotatableBlock)), allowedValue.get2DDataValue() == -1 ? allowedValue.getOpposite().getAxisDirection().getStep() * 90 : 0, (int) allowedValue.getOpposite().toYRot(), false));
                            }
                        }
                    } else {
                        builder.partialState().addModels(new ConfiguredModel(new ModelFile.UncheckedModelFile(getModel(rotatableBlock))));
                    }
                });
    }

    public static ResourceLocation getModel(Block block) {
        return ResourceLocation.fromNamespaceAndPath(BuiltInRegistries.BLOCK.getKey(block).getNamespace(), "block/" + BuiltInRegistries.BLOCK.getKey(block).getPath());
    }
}
