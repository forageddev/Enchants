package dev.foraged.enchants.enchant

import dev.foraged.commons.annotations.Listeners
import dev.foraged.commons.persist.PluginService
import dev.foraged.enchants.EnchantsExtendedPlugin
import dev.foraged.enchants.enchant.impl.*
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

@Service
@Listeners
object EnchantService : Listener, PluginService
{
    val enchants = mutableMapOf<String, Enchant>()

    @Configure
    override fun configure() {
        registerEnchant(RepairEnchant)
        registerEnchant(RecoverEnchant)
    }

    fun registerEnchant(enchant: Enchant) {
        if (findEnchant(enchant.id) != null) {
            EnchantsExtendedPlugin.instance.logger.info("[Enchantment] Updated enchant with id ${enchant.id}.")
        } else {
            EnchantsExtendedPlugin.instance.logger.info("[Enchantment] Registered new enchant with id ${enchant.id}.")
        }
        enchants[enchant.id] = enchant
    }

    fun findEnchantByLine(line: String) : Enchant? {
        return enchants.values.firstOrNull {
            line.startsWith(it.getFormattedLoreName())
        }
    }

    fun findEnchantByEnchantment(enchantment: Enchantment) : Enchant? {
        return enchants[enchantment.name.lowercase()]
    }

    fun findEnchant(name: String) : Enchant? {
        return enchants[name]
    }

    fun findEnchants(item: ItemStack?) : Map<Enchant, Int> {
        if (item == null || item.itemMeta == null || !item.itemMeta.hasLore() || item.itemMeta.lore.size <= 0) return emptyMap()
        val enchants = mutableMapOf<Enchant, Int>()

        item.itemMeta.lore.forEach {
            if (it.contains(Constants.DOUBLE_ARROW_RIGHT)) {
                val enchant = findEnchantByLine(it) ?: return@forEach
                val split = ChatColor.stripColor(it).split(" ")
                val level = split[split.size - 1].toInt()

                enchants[enchant] = level
            }
        }

        return enchants
    }

    fun isEnchantStar(item: ItemStack?) : Boolean {
        return getEnchantFromStar(item) != null
    }

    fun getEnchantFromStar(item: ItemStack?) : Enchant? {
        if (item == null) return null
        for (enchant in enchants.values) {
            if (enchant.isEnchantStar(item)) return enchant
        }
        return null
    }


    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onClick(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        if (event.clickedInventory == null) return
        if (event.cursor == null || event.cursor.type != Material.FIREWORK_CHARGE) return
        if (event.currentItem == null) return
        if (event.currentItem.type == Material.AIR) return
        if (event.cursor.amount > 1) return
        if (!isEnchantStar(event.cursor)) return

        val enchant = getEnchantFromStar(event.cursor) ?: return
        if (!enchant.enabled) {
            player.sendMessage("${CC.RED}This enchant is currently disabled.")
            return
        }
        if (!enchant.canEnchant(event.currentItem)) return

        val starLevel = enchant.getStarLevel(event.cursor)
        if (enchant.getEnchantLevel(event.currentItem) + starLevel > enchant.maxLevel) {
            player.sendMessage("${CC.RED}You cannot apply this enchant onto this item as it would become to powerful to behold.")
            return
        }

        enchant.apply(event.currentItem, starLevel)
        event.cursor = null
        event.isCancelled = true
    }
}