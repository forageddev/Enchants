package dev.foraged.enchants.configuration

import dev.foraged.enchants.enchant.wrapper.EffectEnchantWrapper
import org.bukkit.potion.PotionEffectType
import xyz.mkotb.configapi.comment.Comment
import xyz.mkotb.configapi.comment.HeaderComment

@HeaderComment("Setup enchants for effects in the plugin")
class EffectEnchantConfiguration
{

    @Comment("List of enchants to support")
    val enchants = mutableListOf<EffectEnchantWrapper>()
}