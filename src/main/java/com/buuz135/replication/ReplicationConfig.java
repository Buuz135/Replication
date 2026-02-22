package com.buuz135.replication;

import com.hrznstudio.titanium.annotation.config.ConfigFile;
import com.hrznstudio.titanium.annotation.config.ConfigVal;

import java.util.Arrays;
import java.util.List;

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

        @ConfigVal
        @ConfigVal.InRangeInt(min = 1)
        public static int TANK_CAPACITY = 16000;

    }

    @ConfigFile.Child(ReplicationConfig.class)
    public class IdentificationChamber {

        @ConfigVal(comment = "The progress shown in game will be double")
        @ConfigVal.InRangeInt(min = 1)
        public static int MAX_PROGRESS = 100;

        @ConfigVal
        @ConfigVal.InRangeInt(min = 1)
        public static int POWER_USAGE = 5000;

        @ConfigVal
        @ConfigVal.InRangeDouble(min = 0, max = 1)
        public static double IDENTIFICATION_PROGRESS = 0.5;

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

        @ConfigVal
        @ConfigVal.InRangeDouble(min = 0, max = 1)
        public static double ENCLOSURE_SPEED_MULTIPLIER = 0.8;

        @ConfigVal
        @ConfigVal.InRangeDouble(min = 0)
        public static double ENCLOSURE_POWER_MULTIPLIER = 1.1;

    }

    @ConfigFile.Child(ReplicationConfig.class)
    public class RecipeCalculation {

        @ConfigVal(comment = "Setting this to 0 will disable the recipe calculation")
        @ConfigVal.InRangeInt(min = 0)
        public static int MAX_RECIPE_DEPTH = 11;

        @ConfigVal
        @ConfigVal.InRangeInt(min = 1)
        public static int MAX_VISITED_RECIPES = 50;

        @ConfigVal(comment = "If enabled, values of items that have a crafting remaining item like milk buckets in the cake recipe, will subtract their remaining item matter value from the calculation")
        public static boolean SUBTRACT_CRAFTING_REMAINING_ITEM = true;

        @ConfigVal(comment = "Recipes from the mods on this list will get ignored when calculating matter values")
        public static List<String> IGNORED_RECIPE_MODS = Arrays.asList("ae2qolrecipes");

    }

    @ConfigFile.Child(ReplicationConfig.class)
    public class MatterTank {

        @ConfigVal()
        @ConfigVal.InRangeInt(min = 1)
        public static int CAPACITY = 256000;

    }

    @ConfigFile.Child(ReplicationConfig.class)
    public class Client {

        @ConfigVal
        public static boolean SHOW_PRESS_SHIFT_TEXT = true;

        @ConfigVal
        public static boolean SHOW_MATTER_TOOLTIP_VALUES_OUTSIDE_TERMINAL = true;
    }
}
