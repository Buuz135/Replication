package com.buuz135.replication.block.tile;

import com.hrznstudio.titanium.annotation.Save;
import com.hrznstudio.titanium.block.BasicTileBlock;
import com.hrznstudio.titanium.util.FacingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ReplicatorBlockEntity extends NetworkBlockEntity<ReplicatorBlockEntity>{

    public static final int MAX_PROGRESS = 200;
    public static final float LOWER_PROGRESS = 0.563f;

    @Save
    private int progress;
    @Save
    private int action;

    public ReplicatorBlockEntity(BasicTileBlock<ReplicatorBlockEntity> base, BlockEntityType<?> blockEntityType, BlockPos pos, BlockState state) {
        super(base, blockEntityType, pos, state);
        this.progress = 0;
        this.action = 0;
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state, ReplicatorBlockEntity blockEntity) {
        super.serverTick(level, pos, state, blockEntity);
        tickProgress();
    }

    @NotNull
    @Override
    public ReplicatorBlockEntity getSelf() {
        return this;
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state, ReplicatorBlockEntity blockEntity) {
        super.clientTick(level, pos, state, blockEntity);
        tickProgress();
    }

    private void tickProgress(){
        if (this.action == 0){
            ++this.progress;
            syncObject(this.progress);
            if (this.progress >= MAX_PROGRESS){
                this.action = 1;
                syncObject(this.action);
            }
        }else{
            --this.progress;
            syncObject(this.progress);
            if (this.progress <= 0){
                this.action = 0;
                syncObject(this.action);
            }
        }
    }

    public int getProgress() {
        return progress;
    }

    public int getAction() {
        return action;
    }
}
