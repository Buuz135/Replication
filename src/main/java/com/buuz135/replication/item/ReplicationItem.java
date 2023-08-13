package com.buuz135.replication.item;

import com.buuz135.replication.Replication;
import com.hrznstudio.titanium.item.BasicItem;
import com.hrznstudio.titanium.tab.TitaniumTab;

public class ReplicationItem extends BasicItem {
    public ReplicationItem(Properties properties) {
        super(properties);
        Replication.TAB.getTabList().add(this);
    }

}
