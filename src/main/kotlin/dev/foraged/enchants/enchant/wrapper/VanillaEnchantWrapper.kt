package dev.foraged.enchants.enchant.wrapper

import org.bukkit.ChatColor
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.potion.PotionEffectType

class VanillaEnchantWrapper(
    @Transient var enchant: Enchantment = Enchantment.PROTECTION_ENVIRONMENTAL,
    var enchantId: Int = enchant.id,
    var name: String = enchant.name.lowercase(),
    var displayName: String = name.replace("_", " ").capitalize(),
    @Transient var color: ChatColor = ChatColor.GRAY,
    var chatColor: String = color.name,
    var maxLevel: Int = enchant.maxLevel,
    var enabled: Boolean = true
)