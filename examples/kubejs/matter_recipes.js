/*
Needs to be on the server_scripts folder!

This is the main part about this little guide, how to add matter to items and blocks
There are 2 ways (only for smelting and crafting recipes), they are assigned automatically or manually
By default, you can get calculations up to 11 depth, that means that from log to final item,
there can only be 11 recipes

For all the other items, you need to add them manually, which is a bit of a bummer,
but thats why we are here

This example uses Powah's items and blocks, but you can use any other mod!
It applies the same principles!

Links:
Replication global object -> https://github.com/Buuz135/Replication/blob/1.21/src/main/java/com/buuz135/replication/integration/kubejs/ReplicationKubeJSGateway.java
Registry -> https://github.com/Buuz135/Replication/blob/1.21/src/main/java/com/buuz135/replication/ReplicationRegistry.java

Currently there are these types:
earth, nether, ender, organic, metallic, precious, living, quantum

To disable automatic recipes, check configuration (ReplicationConfig.RecipeCalculation) and set MAX_RECIPE_DEPTH to 0
*/

ServerEvents.recipes(event => {

    const ENERGY_TO_MATTER_FACTOR = 0.01;//Since energy is free in most packs, there is a low "efficiency"

    //Lets take dry ice as example, but you can do for another things:
    /*{
        "type": "powah:energizing", //recipe type if we need want all recipes from a particular machine
        "energy": 120000, //energy cost
        "ingredients": [ //An array of items, which you can get the 
            {
            "tag": "c:rods/blaze"
            }
        ],
        "result": {
            "count": 1,
            "id": "powah:crystal_blazing"
        }
    }
    */

    //You can have a Set to store the values to store the items you previous did
    //to concat them or add compact to mods!
    //let matter = new WeakSet()

    //Thats for more advanced setups, if you have recipes calculation on
    //Here is getting all recipes from Powah!'s Energizing, which you can add compact this way
    
    //Here we use the event.custom
    //Below this, on another event, there is a builder, sort of nicer way to add matter if you like them!
    event.forEachRecipe({ type: 'powah:energizing' }, recipe => { //forEachRecipe is a way to iterate all recipes from a type
        let json = recipe.json;

        let energy = json.get('energy');
        if(!energy)
        {
            console.warn(`Recipe without energy: ${recipe.getId()}`);
            return;
        }

        //Result
        let result = json.get('result');
        if(!result)
        {
            console.warn(`Recipe without results: ${recipe.getId()}`);
            return;
        }

        let idElement = result.get('id');
        if(!idElement)
        {
            console.warn(`No output: ${recipe.getId()}`);
            return;
        }
        let itemId = idElement.getAsString ? idElement.getAsString() : String(idElement).replace("\"", "");

        let count = 1;
        if(result.has('count'))
        {
            count = result.get('count');
        }

        let matterAmount = energy * ENERGY_TO_MATTER_FACTOR;
        let matterMap = {
            'metallic': matterAmount
        };
        event.custom(Replication.matterValueForItem(itemId, matterMap));
    });
});

ServerEvents.recipes(event => {

    const MatterBuilder = () => {
        let _input = null;
        let _inputType = null; // 'item', 'tag', 'ingredient'
        let _matterMap = {};
        let _customId = null;

        const defaultId = () => {
            if(!_input) return 'unknown';
            let inputStr = _input;
            if(typeof _input === 'object')
            {
                inputStr = _input.item || _input.tag || JSON.stringify(_input);
            }
            let cleaned = inputStr.toString().replace(/[#]/g, '').replace(/[^a-z0-9_.-]/gi, '_').toLowerCase();
            let matterPart = Object.keys(_matterMap).join('_');
            return `replication:matter_value/${cleaned}_${matterPart}`;
        };

        return {
            /**
             * @param {string} itemId - ID of the item (e.g: 'minecraft:stone')
             */
            withItem(itemId)
            {
                _input = itemId;
                _inputType = 'item';
                return this;
            },

            /**
             * @param {string} tag - Tag (e.g: '#c:iron_ingots' or 'c:iron_ingots')
             */
            withTag(tag)
            {
                _input = tag.startsWith('#') ? tag.substring(1) : tag;
                _inputType = 'tag';
                return this;
            },

            /**
             * @param {Object} ingredient - e.g: { item: 'minecraft:stone' } or { tag: 'c:ingots' }
             */
            withIngredient(ingredient)
            {
                _input = ingredient;
                _inputType = 'ingredient';
                return this;
            },

            /**
             * Add matter to a recipe
             * @param {string} type - Matter key (ej: 'earth', 'metallic')
             * @param {number} amount - Amount of matter, must be an integer, will round up for numbers with decimals
             */
            addMatter(type, amount)
            {
                _matterMap[type] = amount;
                return this;
            },

            /**
             * Asign a id to the recipe and register it.
             * @param {string} id - ID of the recipe 'namespace:path' (e.g mymodpack:replication/matter/...)
             */
            withId(id)
            {
                _customId = id;
                return this.register()
            },

            /**
             * Generate the recipe with default ID, you can use withId to do the same but with an recipe id
             * @returns void
             */
            register()
            {
                if(!_input || !_inputType)
                {
                    console.error('[MatterValueBuilder] No input specified. Use withItem, withTag or withIngredient. Skipping');
                    return;
                }

                if(typeof Replication === 'undefined')
                {
                    console.error('[MatterValueBuilder] Replication object is not available. Is the mod loaded?');
                    return;
                }

                let recipeJson;
                if(_inputType === 'item')
                {
                    recipeJson = Replication.matterValueForItem(_input, _matterMap); //Given a item and a map of matters with amount, return a json object with those matters and item
                }
                else if(_inputType === 'tag')
                {
                    recipeJson = Replication.matterValueForTag(_input, _matterMap); //Given a tag and a map of matters with amount, return a json object with those matters and tag
                }
                else
                {
                    recipeJson = Replication.matterValue(_input, _matterMap); //Given an ingredient and a map of matters with amount, return a json object with those matters and ingredient
                }

                if(_customId)
                {
                    event.custom(recipeJson).id(_customId);
                } 
                else
                {
                    //KubeJS generate a random recipe id each time, so we would need
                    //to give an id ourselft or an generated one from inputs
                    event.custom(recipeJson).id(defaultId());
                }
            }
        };
    };

    //This is how to use the builder
    MatterBuilder()
        .withItem('guideme:guide')
        .addMatter('earth', 3.0) //This is fine, since it will transform to 3
        .addMatter('metallic', 1.5) //This is mostly fine, it will transform to 2
        .withId('mypack:replication/guide');

    //But you can do more with the builder, remember the first setup? Well, we have more, you can use the builder
    //to give more or less, depending on what you want!
    let a_value = 0 //Lets say that we add a value depending on a variable
    let builder = MatterBuilder()
        .withItem('replication:matter_blueprint')
        .addMatter('earth', 3.0) //This is fine, since it will transform to 3
        .addMatter('metallic', 1.5) //This is mostly fine, it will transform to 2
    
    if(a_value < 5)
    {
        builder.addMatter('ender', 4)
    }

    builder.register()
})