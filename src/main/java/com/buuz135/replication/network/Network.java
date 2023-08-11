package com.buuz135.replication.network;

import com.buuz135.replication.api.network.NetworkElement;
import com.buuz135.replication.network.graph.NetworkGraph;
import com.buuz135.replication.network.graph.NetworkGraphScannerResult;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.Objects;

public abstract class Network {
    protected final NetworkGraph graph = new NetworkGraph(this);
    private final String id;
    private BlockPos originPos;
    private boolean didDoInitialScan;

    public Network(BlockPos originPos, String id) {
        this.id = id;
        this.originPos = originPos;
    }

    public void setOriginPos(BlockPos originPos) {
        this.originPos = originPos;
    }

    public String getId() {
        return id;
    }

    public NetworkGraphScannerResult scanGraph(Level level, BlockPos pos) {
        return graph.scan(level, pos);
    }

    public CompoundTag writeToNbt(CompoundTag tag) {
        tag.putString("id", id);
        tag.putLong("origin", originPos.asLong());

        return tag;
    }

    public void update(Level level) {
        if (!didDoInitialScan) {
            didDoInitialScan = true;

            scanGraph(level, originPos);
        }

        graph.getElements().forEach(NetworkElement::update);
    }

    public NetworkElement getElement(BlockPos pos) {
        return graph.getElements().stream().filter(p -> p.getPos().equals(pos)).findFirst().orElse(null);
    }

    public abstract void onMergedWith(Network mainNetwork);

    public abstract ResourceLocation getType();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return id.equals(network.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
