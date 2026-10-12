package com.buuz135.replication.data;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.ReplicatorBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

public class RepBlockstateProvider extends ModelProvider {

    public RepBlockstateProvider(DataGenerator generator, String modId) {
        super(generator.getPackOutput(), modId);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        var replicator = ReplicationRegistry.Blocks.REPLICATOR.getBlock();
        var multipart = MultiPartGenerator.multiPart(replicator);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            multipart.with(BlockModelGenerators.condition().term(ReplicatorBlock.FACING_HORIZONTAL, direction), rotatedModel("replicator", direction));
            multipart.with(BlockModelGenerators.condition().term(ReplicatorBlock.FACING_HORIZONTAL, direction).term(ReplicatorBlock.HAS_ENCLOSURE, true), rotatedModel("replicator_enclosure", direction));
            multipart.with(BlockModelGenerators.condition().term(ReplicatorBlock.FACING_HORIZONTAL, direction).term(ReplicatorBlock.HAS_MOTOR, true), rotatedModel("replicator_motor", direction));
        }
        blockModels.blockStateOutput.accept(multipart);
    }

    private MultiVariant rotatedModel(String model, Direction direction) {
        var variant = BlockModelGenerators.plainVariant(Identifier.fromNamespaceAndPath(Replication.MOD_ID, "block/" + model));
        return switch (direction) {
            case SOUTH -> variant.with(BlockModelGenerators.Y_ROT_180);
            case EAST -> variant.with(BlockModelGenerators.Y_ROT_90);
            case WEST -> variant.with(BlockModelGenerators.Y_ROT_270);
            default -> variant;
        };
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.of(ReplicationRegistry.Blocks.REPLICATOR.getBlock().builtInRegistryHolder());
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return Stream.empty();
    }
}
