package qa;

import net.minecraft.server.MinecraftServer;

public final class DataCases {
  public static void run(MinecraftServer server) {
    var ids =
        server.getRecipeManager().getRecipes().stream()
            .map(r -> r.id().identifier().toString())
            .collect(java.util.stream.Collectors.toSet());
    Checks.test(
        "populated_tag_recipe_retained",
        () ->
            Checks.require(
                ids.contains("replication:matter_values/itv_qa_populated_tag"),
                "Populated tag recipe was disabled"));
    Checks.test(
        "empty_tag_recipe_skipped",
        () ->
            Checks.require(
                !ids.contains("replication:matter_values/itv_qa_empty_tag"),
                "Empty tag recipe was loaded"));
  }
}
