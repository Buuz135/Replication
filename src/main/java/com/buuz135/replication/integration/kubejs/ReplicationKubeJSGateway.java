package com.buuz135.replication.integration.kubejs;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Global object exposed to KubeJS scripts as `Replication`.
 * Provides shorthand helpers to build matter value recipe JSON for use in ServerEvents.recipes.
 */
public class ReplicationKubeJSGateway {

    private static ResourceLocation resolveMatterKey(String key) {
        if (key.indexOf(':') < 0) {
            return ResourceLocation.fromNamespaceAndPath("replication", key);
        }
        return ResourceLocation.parse(key);
    }

    /**
     * Builds a matter value recipe JSON for a single item ID.
     * Example: event.custom(Replication.matterValueForItem('minecraft:stone', { earth: 3.0, metallic: 1.5 }))
     */
    public Map<String, Object> matterValueForItem(String itemId, Map<String, Object> matterMap) {
        Map<String, Object> ingredient = Map.of("item", itemId);
        return buildMatterValueJson(ingredient, matterMap);
    }

    /**
     * Builds a matter value recipe JSON for a tag (with or without # prefix).
     * Example: event.custom(Replication.matterValueForTag('#c:iron_ingots', { metallic: 4 }))
     */
    public Map<String, Object> matterValueForTag(String tag, Map<String, Object> matterMap) {
        String t = tag.startsWith("#") ? tag.substring(1) : tag;
        Map<String, Object> ingredient = Map.of("tag", t);
        return buildMatterValueJson(ingredient, matterMap);
    }

    /**
     * Builds a matter value recipe JSON for a KubeJS ingredient-like object.
     * Example: event.custom(Replication.matterValue({ tag: 'minecraft:planks' }, { organic: 2 }))
     */
    public Map<String, Object> matterValue(Map<String, Object> ingredient, Map<String, Object> matterMap) {
        return buildMatterValueJson(ingredient, matterMap);
    }

    private Map<String, Object> buildMatterValueJson(Map<String, Object> ingredient, Map<String, Object> matterMap) {
        List<Map<String, Object>> matterList = new ArrayList<>();
        for (Map.Entry<String, Object> e : matterMap.entrySet()) {
            double amount;
            Object v = e.getValue();
            if (v instanceof Number n) {
                amount = n.doubleValue();
            } else if (v instanceof String s) {
                amount = Double.parseDouble(s);
            } else {
                throw new IllegalArgumentException("Invalid amount for matter '" + e.getKey() + "': " + v);
            }
            Map<String, Object> mv = new HashMap<>();
            mv.put("type", resolveMatterKey(e.getKey()).toString());
            mv.put("amount", amount);
            matterList.add(mv);
        }

        Map<String, Object> json = new HashMap<>();
        json.put("type", "replication:matter_value");
        json.put("input", ingredient);
        json.put("matter", matterList);
        return json;
    }
}
