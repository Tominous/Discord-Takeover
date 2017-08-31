# OwO, what's this?
Discord Takeover is a simple discord bot that allows you to rename everyone in your server to something you specify. It's probably against the TOS soon. So that we don't annoy discord, 1 name is changed per 10 seconds.

# Ok, how do I use it?
You must host Discord Takeover yourself. It's extremely simple to do too. First, either download a release from [here](https://github.com/Dot-Rar/Discord-Takeover), or compile it from source by running
```bash
git clone https://github.com/Dot-Rar/Discord-Takeover.git
chmod +x gradlew
./gradlew fatJar
mv build/libs/Nick* ./Discord-Takeover.jar
```

So, now you should have a copy of Discord-Takeover. You can then run it with `java -jar Discord-Takeover.jar`. The first time it is run, it'll create a config.json and shutdown. In here, you can change the bot's game, but you also need to set it's token. To get a token, you need to create a bot. Firstly, go [here](https://discordapp.com/developers/applications/me) and click the `New App` button. Give it a name and then click `Create App`. You'll be redirected to a new page. Click `Create a Bot User` and confirm. Then, you'll be shown a screen with your bot's settings. You'll need to get 2 values from here - `Token` and `Client ID` (save the client ID for later). Then, go back to your config.json and paste in the token you just copied. You can then start the bot once more using `java -jar Discord-Takeover.jar`. You'll probably notice an terminal open and ask for a channel ID. You can ignore this, as it is only used for sending messages as the bot (which you don't need to do). Next, go to [this site](https://discordapi.com/permissions.html). Tick the `Manage Nicknames` box and paste your client ID (that you got earlier) into the tiny `Insert Client ID here` button at the bottom. Your setup should look something like this:
![Screenshot](https://owo.is-pretty.cool/9b6272.png)

Then, click the invite link and add the bot to your server. Once in your server, the fun starts. There are 2 commands:
`!takeover <name>` - This is the command to rename everyone in your server to whatever you specify. This command is uncancellable (apart from turning off the bot), do NOT run a second `!takeover` without running `!undo` first
`!undo` - Resets all names & nicknames to what they were before
