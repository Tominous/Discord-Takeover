package com.perkelle.dev.takeover

import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.exceptions.PermissionException
import net.dv8tion.jda.core.exceptions.RateLimitedException
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.json.JSONObject
import java.awt.Color

class CommandListener: ListenerAdapter() {

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        val sender = e.author
        val message = e.message
        val channel = message.channel
        val guild = e.guild
        val content = message.rawContent
        val argsList = content.split(" ").toMutableList()
        argsList.removeAt(0)
        val args = argsList.toTypedArray()

        if(content.startsWith("!takeover", true)) {
            if(sender.idLong != 116974588692267010 && sender.idLong != 217617036749176833) {
                channel.sendMessage(EmbedBuilder()
                        .setColor(Color.RED)
                        .addField("Error", "Only cool kids can do pranks", true)
                        .build()).queue { it.waitToDelete(15) }
                return
            }
            if(guild.selfMember.hasPermission(Permission.MESSAGE_MANAGE)) message.waitToDelete(0)

            if(args.isEmpty()) {
                channel.sendMessage(EmbedBuilder()
                        .setColor(Color.RED)
                        .addField("Error", "You need to specify a new nickname for everyone", true)
                        .build()).queue { it.waitToDelete(15) }
                return
            }
            val nick = args.joinToString(" ")
            if(nick.length > 32) {
                channel.sendMessage(EmbedBuilder()
                        .setColor(Color.RED)
                        .addField("Error", "Nicknames must be 32 characters or less", true)
                        .build()).queue { it.waitToDelete(15) }
                return
            }

            channel.sendMessage(EmbedBuilder()
                    .setColor(Color.GREEN)
                    .addField("Success", "The slow takeover begins!", true)
                    .build()).queue { it.waitToDelete(15) }

            getConfig().set("guilds.${guild.id}", JSONObject())
            guild.members
                    .filter { it.effectiveName != it.user.name }
                    .filter { it.effectiveName != nick }
                    .forEach { getConfig().set("guilds.${guild.id}.${it.user.id}", it.effectiveName) }
            Main.cfg.save()

            thread {
                var fail = false
                var i = 0
                for(member in guild.members) {
                    try {
                        i++
                        if(member.nickname != null && member.nickname == nick) continue
                        Thread.sleep(10 * 1000)

                        if(!guild.selfMember.hasPermission(Permission.NICKNAME_MANAGE)) {
                            channel.sendMessage(EmbedBuilder()
                                    .setColor(Color.RED)
                                    .addField("Error", "I don't have permission for that. Cancelling...", true)
                                    .build()).queue { it.waitToDelete(15) }
                            fail = true
                            break
                        }
                        guild.controller.setNickname(member, nick).queue()
                    } catch(e: RateLimitedException) {
                        println("Ratelimited while processing guild ${guild.name}#${guild.id} (${guild.members.size} members)")

                        channel.sendMessage(EmbedBuilder()
                                .setColor(Color.RED)
                                .addField("Error", "Uh oh, we got ratelimited. Cancelling...", true)
                                .build()).queue { it.waitToDelete(15) }
                        fail = true
                        break
                    }  catch(_: PermissionException) {

                    } catch(e: Exception) {
                        e.printStackTrace()
                        break
                    }
                }

                if(!fail) channel.sendMessage(EmbedBuilder()
                        .setColor(Color.GREEN)
                        .addField("Success", "Bam! It is done!", true)
                        .build()).queue { it.waitToDelete(15) }
            }
        } else if(content.startsWith("!undo", true)) {
            if(sender.idLong != 116974588692267010 && sender.idLong != 217617036749176833) {
                channel.sendMessage(EmbedBuilder()
                        .setColor(Color.RED)
                        .addField("Error", "Only cool kids can do pranks", true)
                        .build()).queue { it.waitToDelete(15) }
                return
            }
            if(guild.selfMember.hasPermission(Permission.MESSAGE_MANAGE)) message.waitToDelete(0)

            channel.sendMessage(EmbedBuilder()
                    .setColor(Color.GREEN)
                    .addField("Success", "Rolling back...", true)
                    .build()).queue { it.waitToDelete(15) }

            thread {
                var i = 0
                for(member in guild.members) {
                    try {
                        i++
                        if(member.effectiveName == member.user.name && member.user.id !in getConfig().getGeneric("guilds.${guild.id}", JSONObject()).keySet()) {
                            Thread.sleep(200)
                            continue
                        }
                        Thread.sleep(10000)
                        if (member.user.id in getConfig().getGeneric("guilds.${guild.id}", JSONObject()).keySet()) {
                            guild.controller.setNickname(member, getConfig().getGeneric("guilds.${guild.id}.${member.user.id}", member.user.name)).queue()
                        } else guild.controller.setNickname(member, member.user.name).queue()
                    } catch (_: PermissionException) {
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                getConfig().remove("guilds.${guild.id}")
                Main.cfg.save()

                channel.sendMessage(EmbedBuilder()
                        .setColor(Color.GREEN)
                        .addField("Success", "Rolled back!", true)
                        .build()).queue { it.waitToDelete(15) }
            }
        }
    }

    fun Message.waitToDelete(seconds: Int) {
        thread {
            Thread.sleep(seconds * 1000L)
            this.delete().queue()
        }
    }

    fun thread(start: Boolean = true, block: () -> Unit): Thread {
        val t = Thread {
            block()
        }
        if(start) t.start()
        return t
    }
}