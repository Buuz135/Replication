package com.buuz135.replication.network.graph;

import com.buuz135.replication.api.network.NetworkElement;
import com.buuz135.replication.network.NetworkManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.*;

public class NetworkGraphScanner {
    private final Set<NetworkElement> foundElements = new HashSet<>();
    private final Set<NetworkElement> newElements = new HashSet<>();
    private final Set<NetworkElement> removedElements = new HashSet<>();

    private final Set<NetworkElement> currentElements;
    private final ResourceLocation requiredNetworkType;

    private final List<NetworkGraphScannerRequest> allRequests = new ArrayList<>();
    private final Queue<NetworkGraphScannerRequest> requests = new ArrayDeque<>();

    public NetworkGraphScanner(Set<NetworkElement> currentElements, ResourceLocation requiredNetworkType) {
        this.currentElements = currentElements;
        this.removedElements.addAll(currentElements);
        this.requiredNetworkType = requiredNetworkType;
    }

    public NetworkGraphScannerResult scanAt(Level level, BlockPos pos) {
        addRequest(new NetworkGraphScannerRequest(level, pos, null, null));

        NetworkGraphScannerRequest request;
        while ((request = requests.poll()) != null) {
            singleScanAt(request);
        }

        return new NetworkGraphScannerResult(
                foundElements,
                newElements,
                removedElements,
                allRequests
        );
    }

    private void singleScanAt(NetworkGraphScannerRequest request) {
        NetworkElement pipe = NetworkManager.get(request.getLevel()).getElement(request.getPos());

        if (pipe != null) {
            if (!requiredNetworkType.equals(pipe.getNetworkType())) {
                return;
            }
            if (request.getDirection() != null && !pipe.canConnectFrom(request.getDirection().getOpposite())){
                return;
            }
            if (foundElements.add(pipe)) {
                if (!currentElements.contains(pipe)) {
                    newElements.add(pipe);
                }

                removedElements.remove(pipe);

                request.setSuccessful(true);

                for (Direction dir : Direction.values()) {
                    if (!pipe.canConnectFrom(dir)) continue;
                    addRequest(new NetworkGraphScannerRequest(
                            request.getLevel(),
                            request.getPos().relative(dir),
                            dir,
                            request
                    ));
                }
            }
        }
    }

    private void addRequest(NetworkGraphScannerRequest request) {
        requests.add(request);
        allRequests.add(request);
    }
}
