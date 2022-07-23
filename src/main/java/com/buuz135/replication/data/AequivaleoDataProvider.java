package com.buuz135.replication.data;

import com.buuz135.replication.ReplicationRegistry;
import com.ldtteam.aequivaleo.api.compound.CompoundInstance;
import com.ldtteam.aequivaleo.api.compound.information.datagen.ForcedInformationProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.LinkedHashSet;

public class AequivaleoDataProvider extends ForcedInformationProvider {

    public AequivaleoDataProvider(String modId, DataGenerator dataGenerator) {
        super(modId, dataGenerator);
    }

    @Override
    public void calculateDataToSave() {
        saveData(Items.IRON_INGOT, metallic(9));
        saveData(Items.GOLD_INGOT, metallic(9), precious(9));

        saveData(Items.OAK_LOG, earth(8), organic(8));
        saveData(Items.NETHERRACK, earth(1), nether(1));
        saveData(Items.END_STONE, earth(1), ender(1));

        saveData(Items.NETHER_STAR, quantum(64), nether(16));
    }

    private void saveData(Item item, CompoundInstance... instances) {
        save(specFor(item).withCompounds(instances));
    }

    private void saveData(LinkedHashSet<Object> items, CompoundInstance... instances) {
        save(specFor(items).withCompounds(instances));
    }

    private static CompoundInstance metallic(double d){
        return new CompoundInstance(ReplicationRegistry.METALLIC.get(), d);
    }

    private static CompoundInstance earth(double d){
        return new CompoundInstance(ReplicationRegistry.EARTH.get(), d);
    }

    private static CompoundInstance organic(double d){
        return new CompoundInstance(ReplicationRegistry.ORGANIC.get(), d);
    }

    private static CompoundInstance quantum(double d){
        return new CompoundInstance(ReplicationRegistry.QUANTUM.get(), d);
    }

    private static CompoundInstance nether(double d){
        return new CompoundInstance(ReplicationRegistry.NETHER.get(), d);
    }

    private static CompoundInstance precious(double d){
        return new CompoundInstance(ReplicationRegistry.PRECIOUS.get(), d);
    }

    private static CompoundInstance ender(double d){
        return new CompoundInstance(ReplicationRegistry.ENDER.get(), d);
    }

}
