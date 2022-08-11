package dev.foraged.enchants.configuration

import dev.foraged.enchants.enchant.wrapper.VanillaEnchantWrapper
import xyz.mkotb.configapi.comment.Comment
import xyz.mkotb.configapi.comment.HeaderComment


@HeaderComment("Setup enchants for default override enchantments")
class MinecraftEnchantConfiguration
{

    @Comment("List of enchants to support")
    val enchants = mutableListOf<VanillaEnchantWrapper>()
}