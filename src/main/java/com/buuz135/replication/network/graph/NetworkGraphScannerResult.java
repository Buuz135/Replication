package com.buuz135.replication.network.graph;

import com.buuz135.replication.api.network.NetworkElement;

import java.util.List;
import java.util.Set;

public class NetworkGraphScannerResult {
    private final Set<NetworkElement> foundElements;
    private final Set<NetworkElement> newElements;
    private final Set<NetworkElement> removedElements;

    private final List<NetworkGraphScannerRequest> requests;

    public NetworkGraphScannerResult(Set<NetworkElement> foundElements, Set<NetworkElement> newElemenets, Set<NetworkElement> removedElements, List<NetworkGraphScannerRequest> requests) {
        this.foundElements = foundElements;
        this.newElements = newElemenets;
        this.removedElements = removedElements;
        this.requests = requests;
    }

    public Set<NetworkElement> getFoundElements() {
        return foundElements;
    }

    public Set<NetworkElement> getNewElements() {
        return newElements;
    }

    public Set<NetworkElement> getRemovedElements() {
        return removedElements;
    }

    public List<NetworkGraphScannerRequest> getRequests() {
        return requests;
    }
}
