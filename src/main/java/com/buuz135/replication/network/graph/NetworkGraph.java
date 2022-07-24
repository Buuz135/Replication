package com.buuz135.replication.network.graph;

import com.buuz135.replication.api.network.NetworkElement;
import com.buuz135.replication.network.Network;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.Set;

public class NetworkGraph {
    private final Network network;

    private Set<NetworkElement> elements = new HashSet<>();


    public NetworkGraph(Network network) {
        this.network = network;
    }

    public NetworkGraphScannerResult scan(Level originLevel, BlockPos originPos) {
        NetworkGraphScanner scanner = new NetworkGraphScanner(elements, network.getType());

        NetworkGraphScannerResult result = scanner.scanAt(originLevel, originPos);

        this.elements = result.getFoundElements();

        result.getNewElements().forEach(p -> p.joinNetwork(network));
        result.getRemovedElements().forEach(NetworkElement::leaveNetwork);



        return result;
    }

    public Set<NetworkElement> getElements() {
        return elements;
    }


}
