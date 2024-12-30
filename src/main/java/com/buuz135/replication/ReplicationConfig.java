package com.buuz135.replication;

import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

@ConfigFile
public class ReplicationConfig {

    @ConfigFile.Child(ReplicationConfig.class)
    public class Disintegrator {

        @ConfigVal
        @ConfigVal.InRangeInt(min = 1)
        public static int MAX_PROGRESS = 40;

        @ConfigVal
        @ConfigVal.InRangeInt(min = 1)
        public static int POWER_USAGE = 1500;

    }

    @ConfigFile.Child(ReplicationConfig.class)
    public class IdentificationChamber {

        @ConfigVal(comment = "The progress shown in game will be double")
        @ConfigVal.InRangeInt(min = 1)
        public static int MAX_PROGRESS = 100;

        @ConfigVal
        @ConfigVal.InRangeInt(min = 1)
        public static int POWER_USAGE = 5000;

    }

    @ConfigFile.Child(ReplicationConfig.class)
    public class MatterPipe {

        @ConfigVal
        @ConfigVal.InRangeInt(min = 1)
        public static int POWER_TRANSFER = 2560;

    }

    @ConfigFile.Child(ReplicationConfig.class)
    public class Replicator {

        @ConfigVal(comment = "The progress shown in game will be double")
        @ConfigVal.InRangeInt(min = 1)
        public static int MAX_PROGRESS = 100;

        @ConfigVal
        @ConfigVal.InRangeInt(min = 1)
        public static int POWER_TICK = 80;

    }

    @ConfigFile.Child(ReplicationConfig.class)
    public class RecipeCalculation {

        @ConfigVal.InRangeInt(min = 1)
        public static int MAX_RECIPE_DEPTH = 11;

        @ConfigVal
        @ConfigVal.InRangeInt(min = 1)
        public static int MAX_VISITED_RECIPES = 50;

    }


}
