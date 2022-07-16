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
        saveData(Items.IRON_INGOT, metallic(18));
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
}
