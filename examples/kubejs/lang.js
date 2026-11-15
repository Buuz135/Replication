//Needs to be on client_scripts folder!
//Or you can have a client event to add the lang key without needing to do things with .json
//But it might be a more difficult way to translate it with external tools!

ClientEvents.lang('en_us', event => {
    event.add('replication.matter_type.matter_lambda', 'Esto es una tombola, tombola')
})