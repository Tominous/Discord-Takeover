package com.perkelle.dev.takeover

import net.dv8tion.jda.core.AccountType
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.entities.Game
import net.dv8tion.jda.core.entities.TextChannel
import java.util.*

fun main(args: Array<String>) {
    Main().run()
}

fun getConfig() = Main.cfg.config

class Main: Runnable {

    companion object {
        lateinit var jda: JDA
        val cfg = Config()
    }

    override fun run() {
        cfg.load()

        if (!getConfig().contains("token")) {
            println("Token not found, shutting down")
            System.exit(-1)
        }

        val token = getConfig().getGeneric("token", "")

        try {
            jda = JDABuilder(AccountType.BOT)
                    .addEventListener(CommandListener())
                    .setToken(token)
                    .setGame(Game.of(getConfig().getGeneric("game", "I enjoy dabbot")))
                    .buildBlocking()
        } catch (ex: Exception) {
            println("An error occurred, shutting down")
            ex.printStackTrace()
            System.exit(-1)
        }

        val scanner = Scanner(System.`in`)
        var mode = Mode.CHANNEL
        var channel: TextChannel? = null
        while(true) {
            if (mode == Mode.CHANNEL) {
                print("Enter a channel ID: ")
                val idStr = scanner.nextLine()
                if (!idStr.isLong()) {
                    println("Invalid channel ID")
                    continue
                }

                val id = idStr.toLong()
                channel = jda.getTextChannelById(id)
                if (channel == null) {
                    println("Invalid channel ID")
                    continue
                }

                mode = Mode.MESSAGE
            } else if(mode == Mode.MESSAGE) {
                print("Enter a message to send: ")
                val message = scanner.nextLine()
                if(message.isNotEmpty()) channel?.sendMessage(message)?.queue()
                println("Sent!")
                mode = Mode.CHANNEL
            }
        }
    }

    fun String.isLong() = this.none { !it.isDigit() }

    enum class Mode {
        CHANNEL, MESSAGE
    }

    @FileName
    class Config: JSON()
}