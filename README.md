
![](https://i.imgur.com/aTW6Oau.png)

**Replication** is a tech mod that allows you to replicate resources of similar types. You can transform dirt to stone but you can't transform dirt to diamonds.

![](https://imgur.com/CzWVTZI.png)

This mod needs [Aequivaleo](https://www.curseforge.com/minecraft/mc-mods/aequivaleo) and [Titanium](https://www.curseforge.com/minecraft/mc-mods/titanium)

# Important Concepts
**Matter pipes** will allow you to connect Replication machines and they will automate some processes:
* Transfer **Power**: they work like any power pipe
* Transfer **Matter**: they will transfer matter from the **Disintegrator** to **Matter Tanks** and from **Matter Tanks** to **other machines** that need it

The **Identification Chamber** will scan items to know their matter values and store them into chips. Those **Chips** can be stored in the **Chip Storage** and will be available to the network.

**Replicators** can be used in "Infinite Mode" where they will keep replicating a resource until it is full or has run out matter, you can configure that mode in the GUI.

# How it works
To transform items you will need to break them down to their primal values using a **Disintegrator**. Using that machine you will transform any item with matter values into matter. Once you have scanned some items and stored their values into chips you can use the **Replication Terminal** to request items. With a request created **Replicators** will use the Matter stored in tanks to replicate the item from scratch and send it back to the terminal.

# For Pack Makers
## Datapack
You can modify/add/remove matter values using datapacks, you don't need to add values for each modded item as the system will calculate the values using the crafting recipes. Datapack [examples](https://github.com/Buuz135/Replication/tree/master/src/generated/resources/data/replication/aequivaleo/value/general).
If the type is a tag you need to add the tag to the config file `vanilla-aequivaleo.toml` to the `tagsToRegister` array.

## Blueprints
You can create blueprints (one use item) that have a % of the scanned item information using the command `/replication create-blueprint-using-hand &lt;progress&gt;` where progress is a decimal number between 0-1. This will allow you to create the blueprint using the current item in your hand respecting the NBT of the item. Then those blueprints can be transferred using the Identification Chamber or directly to the Chip Storage
## Tag
You can disable an item from being able to be scanned using the tag `replication:cant_be_scanned`. This will only prevent the item from being scanned and not replicated