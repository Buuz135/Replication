package com.buuz135.replication.data;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.MatterPipeBlock;
import com.hrznstudio.titanium.block.RotatableBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;

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
        var pipe = getMultipartBuilder(ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getKey().get());
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation(Replication.MOD_ID, "block/matter_network_pipe_middle"))).addModel();
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation(Replication.MOD_ID, "block/matter_network_pipe_side"))).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.NORTH), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationY(180).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.SOUTH), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationY(90).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.EAST), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationY(270).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.WEST), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationX(90).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.DOWN), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(new ResourceLocation(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationX(270).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.UP), true);

        this.blocks.stream().filter(blockBase -> blockBase instanceof RotatableBlock<?>)
                .map(blockBase -> (RotatableBlock) blockBase)
                .forEach(rotatableBlock -> {
                    VariantBlockStateBuilder builder = getVariantBuilder(rotatableBlock);
                    if (rotatableBlock.getRotationType().getProperties().length > 0) {
                        for (DirectionProperty property : rotatableBlock.getRotationType().getProperties()) {
                            for (Direction allowedValue : property.getPossibleValues()) {
                                builder.partialState().with(property, allowedValue)
                                        .addModels(new ConfiguredModel(new ModelFile.UncheckedModelFile(getModel(rotatableBlock)), allowedValue.get2DDataValue() == -1 ? allowedValue.getOpposite().getAxisDirection().getStep() * 90 : 0, (int) allowedValue.getOpposite().toYRot(), true));
                            }
                        }
                    } else {
                        builder.partialState().addModels(new ConfiguredModel(new ModelFile.UncheckedModelFile(getModel(rotatableBlock))));
                    }
                });
    }

    public static ResourceLocation getModel(Block block) {
        return new ResourceLocation(ForgeRegistries.BLOCKS.getKey(block).getNamespace(), "block/" + ForgeRegistries.BLOCKS.getKey(block).getPath());
    }
}
