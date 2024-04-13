package com.buuz135.replication.block;

import com.buuz135.replication.Replication;
import com.buuz135.replication.ReplicationRegistry;
import com.buuz135.replication.block.tile.MatterPipeBlockEntity;
import com.buuz135.replication.network.MatterNetwork;
import com.google.common.collect.ImmutableMap;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.block_network.INetworkDirectionalConnection;
import com.hrznstudio.titanium.block_network.NetworkManager;
import com.hrznstudio.titanium.block_network.element.NetworkElement;
import com.hrznstudio.titanium.datagenerator.loot.block.BasicBlockLootTables;
import com.hrznstudio.titanium.recipe.generator.TitaniumShapedRecipeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class MatterPipeBlock extends BasicTileBlock<MatterPipeBlockEntity> implements INetworkDirectionalConnection {

    public static final Map<Direction, BooleanProperty> DIRECTIONS = new HashMap<>();
    private static final Map<BlockState, VoxelShape> SHAPE_CACHE = new HashMap<>();
    private static final Map<BlockState, VoxelShape> COLL_SHAPE_CACHE = new HashMap<>();
    private static final VoxelShape CENTER_SHAPE = Block.box(5, 5, 5, 11, 11, 11);
    public static final Map<Direction, VoxelShape> DIR_SHAPES = ImmutableMap.<Direction, VoxelShape>builder()
            .put(Direction.UP, Block.box(5, 10, 5, 11, 16, 11))
            .put(Direction.DOWN, Block.box(5, 0, 5, 11, 6, 11))
            .put(Direction.NORTH, Block.box(5, 5, 0, 11, 11, 6))
            .put(Direction.SOUTH, Block.box(5, 5, 10, 11, 11, 16))
            .put(Direction.EAST, Block.box(10, 5, 5, 16, 11, 11))
            .put(Direction.WEST, Block.box(0, 5, 5, 6, 11, 11))
            .build();

    static {
        for (Direction value : Direction.values()) {
            DIRECTIONS.put(value, BooleanProperty.create(value.getName().toLowerCase(Locale.ROOT)));
        }
    }

    public MatterPipeBlock() {
        super("matter_network_pipe", Properties.copy(Blocks.IRON_BLOCK), MatterPipeBlockEntity.class);
        setItemGroup(Replication.TAB);
    }

    @Override
    public BlockEntityType.BlockEntitySupplier<?> getTileEntityFactory() {
        return (pos, state) -> new MatterPipeBlockEntity(this, ReplicationRegistry.Blocks.MATTER_NETWORK_PIPE.getRight().get(), pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DIRECTIONS.values().toArray(Property[]::new));
        builder.add(BlockStateProperties.WATERLOGGED);
    }

    private BlockState createState(Level world, BlockPos pos, BlockState curr) {
        var state = this.defaultBlockState();
        var fluid = world.getFluidState(pos);
        if (fluid.is(FluidTags.WATER) && fluid.getAmount() == 8)
            state = state.setValue(BlockStateProperties.WATERLOGGED, true);

        for (var dir : Direction.values()) {
            var prop = DIRECTIONS.get(dir);
            var type = this.getConnectionType(world, pos, dir, state);
            state = state.setValue(prop, type);
        }
        return state;
    }

    protected boolean getConnectionType(Level world, BlockPos pos, Direction direction, BlockState state) {
        if (world.isClientSide()) return false;
        var relativeState = world.getBlockState(pos.relative(direction));
        if (relativeState.getBlock() instanceof MatterPipeBlock){
            return true;
        }
        var networkManager = NetworkManager.get(world);
        if (networkManager != null){
            var network = networkManager.getElement(pos.relative(direction));
            if (network != null && network.getNetwork() instanceof MatterNetwork) {
                INetworkDirectionalConnection networkDirectionalConnection = (INetworkDirectionalConnection) relativeState.getBlock();
                return networkDirectionalConnection.canConnect(relativeState, direction.getOpposite());
            }
        }
        var tile = world.getBlockEntity(pos.relative(direction));
        if (tile != null){
            if (tile.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).isPresent()){
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.createState(context.getLevel(), context.getClickedPos(), this.defaultBlockState());
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        var newState = this.createState(worldIn, pos, state);
        if (newState != state) {
            worldIn.setBlockAndUpdate(pos, newState);
        }
        if (!worldIn.isClientSide) {
            NetworkElement pipe = NetworkManager.get(worldIn).getElement(pos);

            if (pipe != null && pipe.getNetwork() != null) {
                pipe.getNetwork().scanGraph(worldIn, pos);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.cacheAndGetShape(state, worldIn, pos, s -> s.getShape(worldIn, pos, context), SHAPE_CACHE, null);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        return this.cacheAndGetShape(state, worldIn, pos, s -> s.getCollisionShape(worldIn, pos, context), COLL_SHAPE_CACHE, s -> {
            // make the shape a bit higher so we can jump up onto a higher block
            var newShape = new MutableObject<VoxelShape>(Shapes.empty());
            s.forAllBoxes((x1, y1, z1, x2, y2, z2) -> newShape.setValue(Shapes.join(Shapes.create(x1, y1, z1, x2, y2 + 3 / 16F, z2), newShape.getValue(), BooleanOp.OR)));
            return newShape.getValue().optimize();
        });
    }

    private VoxelShape cacheAndGetShape(BlockState state, BlockGetter worldIn, BlockPos pos, Function<BlockState, VoxelShape> coverShapeSelector, Map<BlockState, VoxelShape> cache, Function<VoxelShape, VoxelShape> shapeModifier) {

        var shape = cache.get(state);
        if (shape == null) {
            shape = CENTER_SHAPE;
            for (var entry : DIRECTIONS.entrySet()) {
                if (state.getValue(entry.getValue()))
                    shape = Shapes.or(shape, DIR_SHAPES.get(entry.getKey()));
            }
            if (shapeModifier != null)
                shape = shapeModifier.apply(shape);
            cache.put(state, shape);
        }
        return shape;
    }

    @Override
    public boolean canConnect(BlockState state, Direction direction) {
        return true;
    }

    @Override
    public void registerRecipe(Consumer<FinishedRecipe> consumer) {
        TitaniumShapedRecipeBuilder.shapedRecipe(this)
                .pattern("PPP")
                .pattern("IRI")
                .pattern("PPP")
                .define('P', Tags.Items.GLASS_PANES)
                .define('I', ReplicationRegistry.Items.REPLICA_INGOT.get())
                .define('R', Items.REDSTONE)
                .save(consumer);
    }
}
