package com.buuz135.replication.data;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.ReplicatorBlock;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.List;

public class RepBlockstateProvider extends BlockStateProvider {

    private List<Block> blocks;

    public RepBlockstateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper, List<Block> blocks) {
        super(gen.getPackOutput(), modid, exFileHelper);
        this.blocks = blocks;
    }

    @Override
    protected void registerStatesAndModels() {
        /*var pipe = getMultipartBuilder(ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getBlock());
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_middle"))).addModel();
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.NORTH), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationY(180).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.SOUTH), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationY(90).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.EAST), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationY(270).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.WEST), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationX(90).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.DOWN), true);
        pipe.part().modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/matter_network_pipe_side"))).rotationX(270).addModel().condition(MatterPipeBlock.DIRECTIONS.get(Direction.UP), true);

        this.blocks.stream().motor(blockBase -> blockBase instanceof RotatableBlock<?>)
                .map(blockBase -> (RotatableBlock) blockBase)
                .motor(rotatableBlock -> !(rotatableBlock instanceof ReplicatorBlock))
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
                });*/
        var replicator = getMultipartBuilder(ReplicationRegistry.Blocks.REPLICATOR.getBlock());
        var repNorthPart = replicator.part();
        repNorthPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator"))).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.NORTH);
        repNorthPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator_enclosure"))).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.NORTH).condition(ReplicatorBlock.HAS_ENCLOSURE, true);
        repNorthPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator_motor"))).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.NORTH).condition(ReplicatorBlock.HAS_MOTOR, true);

        var repSouthPart = replicator.part();
        repSouthPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator"))).rotationY(180).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.SOUTH);
        repSouthPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator_enclosure"))).rotationY(180).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.SOUTH).condition(ReplicatorBlock.HAS_ENCLOSURE, true);
        repSouthPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator_motor"))).rotationY(180).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.SOUTH).condition(ReplicatorBlock.HAS_MOTOR, true);

        var repEastPart = replicator.part();
        repEastPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator"))).rotationY(90).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.EAST);
        repEastPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator_enclosure"))).rotationY(90).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.EAST).condition(ReplicatorBlock.HAS_ENCLOSURE, true);
        repEastPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator_motor"))).rotationY(90).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.EAST).condition(ReplicatorBlock.HAS_MOTOR, true);

        var repWestPart = replicator.part();
        repWestPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator"))).rotationY(270).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.WEST);
        repWestPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator_enclosure"))).rotationY(270).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.WEST).condition(ReplicatorBlock.HAS_ENCLOSURE, true);
        repWestPart.modelFile(new ModelFile.UncheckedModelFile(ResourceLocation.fromNamespaceAndPath(Replication.MOD_ID, "block/replicator_motor"))).rotationY(270).addModel().condition(ReplicatorBlock.FACING_HORIZONTAL, Direction.WEST).condition(ReplicatorBlock.HAS_MOTOR, true);


    }

    public static ResourceLocation getModel(Block block) {
        return ResourceLocation.fromNamespaceAndPath(BuiltInRegistries.BLOCK.getKey(block).getNamespace(), "block/" + BuiltInRegistries.BLOCK.getKey(block).getPath());
    }
}
