package com.buuz135.replication.api.network;

import com.buuz135.replication.api.matter_fluid.IMatterTank;

import java.util.List;

public interface IMatterStacksSupplier {

    List<? extends IMatterTank> getTank();

}
