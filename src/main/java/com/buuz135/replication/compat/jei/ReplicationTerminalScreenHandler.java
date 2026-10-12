/*
 * This file is part of Titanium
 * Copyright (C) 2024, Horizon Studio <contact@hrznstudio.com>.
 *
 * This code is licensed under GNU Lesser General Public License v3.0, the full license text can be found in LICENSE.txt
 */

package com.buuz135.replication.compat.jei;


import com.buuz135.replication.client.gui.ReplicationTerminalScreen;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rect2i;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ReplicationTerminalScreenHandler implements IGuiContainerHandler<ReplicationTerminalScreen> {

    @Override
    @Nonnull
    public List<Rect2i> getGuiExtraAreas(ReplicationTerminalScreen containerScreen) {
        List<Rect2i> rectangles = new ArrayList<>();
        if (containerScreen.shouldBaseGUIRender()) {
            rectangles.add(new Rect2i(containerScreen.getLeftPos() + containerScreen.getImageWidth(), containerScreen.getTopPos() + 20, 47, 174));
        } else {
            rectangles.add(new Rect2i(containerScreen.getLeftPos() + containerScreen.getImageWidth(), containerScreen.getTopPos(), 61, containerScreen.getImageHeight()));
            rectangles.add(new Rect2i(containerScreen.getLeftPos() - 31, containerScreen.getTopPos(), 31, containerScreen.getImageHeight()));
        }
        return rectangles;
    }
}

