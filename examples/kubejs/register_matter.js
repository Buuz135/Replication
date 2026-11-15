/*
Needs to be on startup_scripts folder!

Similar to registering items and blocks, we need to do the same
for our matter types

You must have a .png with the texture on the kubejs/assets/replication/textures/gui/mattertypes/<id of the matter>.png
or the game will crash if you open the terminal!

Links:
ReplicationRegistry -> https://github.com/Buuz135/Replication/blob/1.21/src/main/java/com/buuz135/replication/ReplicationRegistry.java
MatterTypeBuilder -> https://github.com/Buuz135/Replication/blob/1.21/src/main/java/com/buuz135/replication/integration/kubejs/MatterTypeBuilder.java


Here is every way you can do it!
*/
StartupEvents.registry('replication:matter_types', event => {
    //The simplest way
    event.create("ultimatium") //Id of the matter, we will need it later
    .color(0.2, 0.7, 1.0, 1.0) //RGBA (from 0.0 to 1.0)
    .displayName("Ultimatum") //Display name
    .max(100) //can be any number, doesnt matter

    //The hard way but cool one!
    //First we need the builder, we need to load some classes first
    const MatterRegistry = Java.loadClass('com.buuz135.replication.ReplicationRegistry')
    const MatterTypeBuilder = Java.loadClass('com.buuz135.replication.integration.kubejs.MatterTypeBuilder')

    //You can do something similar as the builder on the matter_recipes.js on server_scripts
    let myBuilder = new MatterTypeBuilder('matter_to_add')
    myBuilder.color(0.5, 0.1, 0.9, 1.0).max(2500)
    event.add(MatterRegistry.MATTER_TYPES_KEY, myBuilder)

    //Another hard way
    //This time using a lambda, for customs colors!
    //This is how quantum matter type is done!
    
    //We need to load the classes first
    const CustomMatterType = Java.loadClass('com.buuz135.replication.integration.kubejs.CustomMatterType')
    const FloatArrayList = Java.loadClass('it.unimi.dsi.fastutil.floats.FloatArrayList');
    //Minecraft class is just to get the time of the day
    const Minecraft = Java.loadClass('net.minecraft.client.Minecraft');
    
    //In this case, we need an arrow function (a.k.a a lambda), useful if you need to have more complex setups, like this one
    //or quantum matter
    event.createCustom('matter_lambda', () => {
        let name = "matter_lambda" //To give a name, we need a lang file with the following "replication.matter_type.matter_lambda": "Matter Lambda",
        
        //Dynamic color depending on some conditions
        //In this case, depending on the time of the day and a period
        //while doing a rainbow

        //At the end, you must have the variable list with 4 values or crash
        //RGBA, then convert to float shown on the return
        let color = () => {
            let list = new FloatArrayList();
            try
            {
                let level = Minecraft.getInstance().level;
                if(level)
                {
                    let time = level.getDayTime(); //MC time, around 60k samples, more than enough for most
                    let period = 200; //full cycle every 10 seconds (20 ticks/sec)
                    let hue = (time % period) / period; // 0..1

                    //HSV to RGB (saturation=1, value=1)
                    let h = hue;
                    let s = 1.0;
                    let v = 1.0;

                    let r, g, b;
                    let i = Math.floor(h * 6);
                    let f = h * 6 - i;
                    let p = v * (1 - s);
                    let q = v * (1 - f * s);
                    let t = v * (1 - (1 - f) * s);

                    switch(i % 6)
                    {
                        case 0: r = v; g = t; b = p; break;
                        case 1: r = q; g = v; b = p; break;
                        case 2: r = p; g = v; b = t; break;
                        case 3: r = p; g = q; b = v; break;
                        case 4: r = t; g = p; b = v; break;
                        case 5: r = v; g = p; b = q; break;
                    }

                    list.add(r);
                    list.add(g);
                    list.add(b);
                    list.add(1.0); //alpha
                } 
                else
                {
                    //A default color just in case level is null
                    list.add(0.5);
                    list.add(0.5);
                    list.add(0.5);
                    list.add(1.0);
                }
            }
            catch(e)
            {
                //If any error (e.g., on server or you pass an string to a number), 
                //return a default color
                list.add(1.0);
                list.add(1.0);
                list.add(1.0);
                list.add(1.0);
                //console.log(e) //This spam the logs a lot but tells you a lot too
            }
            return list.toFloatArray();
        };

        let max = 5000
        return new CustomMatterType(name, color, max)
    })
})