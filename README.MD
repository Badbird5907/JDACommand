h  JDACommand

## Slash Commands rewrite

## Outline:
 - Slash commands should be able to be registered as both global and server specific commands.
 - Commands annotated with @Global should be registered as global commands
 - They should be able to be registered in batches (to reduce api calls)
 - They should be able to be registered on the fly
 - Should be able to unregister slash commands from servers
 - Slash commands should be registered to servers when the bot starts
 - Should have sub commands too