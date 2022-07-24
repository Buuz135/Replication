package com.buuz135.replication.network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.Random;

public interface NetworkFactory {

    public static String randomString(Random r, int length) {
        StringBuilder str = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int tmp = 'a' + r.nextInt('z' - 'a');
            str.append((char) tmp);
        }
        return str.toString();
    }

    Network create(BlockPos pos);

    Network create(CompoundTag tag);


}
