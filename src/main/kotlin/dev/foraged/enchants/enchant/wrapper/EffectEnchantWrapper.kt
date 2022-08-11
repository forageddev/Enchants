package dev.foraged.enchants.enchant.wrapper

import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.ChatColor
import org.bukkit.potion.PotionEffectType

class EffectEnchantWrapper(
    @Transient var effect: PotionEffectType = PotionEffectType.SPEED,
    var effectId: Int = effect.id,
    var name: String = effect.name.lowercase(),
    var displayName: String = name.replace("_", " ").capitalize(),
    @Transient var color: ChatColor = ChatColor.YELLOW,
    var chatColor: String = color.name,
    var maxLevel: Int = 2,
    var enabled: Boolean = true,
    @Transient var itemTypes: MutableList<ItemUtils.ItemType> = mutableListOf(ItemUtils.ItemType.ARMOR),
    var items: List<String> = itemTypes.map { it.name },

    var amplifiers: MutableMap<String, Int> = mutableMapOf("1" to 0, "2" to 1),
    var durations: MutableMap<String, Int> = mutableMapOf("1" to 60, "2" to 120),

    var tickHeld: Boolean = true,
    var tickWorn: Boolean = true
)