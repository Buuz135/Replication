package com.buuz135.replication.data;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.MatterPipeBlock;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class RepBlockstateProvider extends BlockStateProvider {

    public RepBlockstateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
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
    }
}
