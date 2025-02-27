package dev.curseforged.strawberryChat

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.LingeringPotionSplashEvent
import org.bukkit.event.entity.PotionSplashEvent
import org.bukkit.potion.PotionEffectType

class PotionUseListener : Listener {
    @EventHandler
    fun onPotionUse(event: PotionSplashEvent) {
        val potion = event.potion
        val effects = potion.potionMeta.customEffects
        if (effects.any { it.type == PotionEffectType.INSTANT_HEALTH && it.amplifier >= 124 }) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onLingeringPotionSplash(event: LingeringPotionSplashEvent) {
        val potion = event.entity
        val effects = potion.potionMeta.customEffects
        if (effects.any { it.type == PotionEffectType.INSTANT_HEALTH && it.amplifier >= 124 }) {
            event.isCancelled = true
        }
    }
}
