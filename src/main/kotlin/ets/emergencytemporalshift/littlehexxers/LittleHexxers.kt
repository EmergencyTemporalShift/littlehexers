package ets.emergencytemporalshift.littlehexxers

import net.fabricmc.api.ModInitializer
import ets.emergencytemporalshift.littlehexxers.registry.LittleHexxersActions
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.minecraft.text.Text


class LittleHexxers : ModInitializer {

    override fun onInitialize() {
        LittleHexxersActions.register()
        registerPlayerJoinEvent()
    }
}

fun registerPlayerJoinEvent() {
    ServerPlayConnectionEvents.JOIN.register { handler, packetSender, server ->
        val message = Text.literal("§aWelcome to the game!")
        handler.player?.sendMessage(message, false)
    }
}
