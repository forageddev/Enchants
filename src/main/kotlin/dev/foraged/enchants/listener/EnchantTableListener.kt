package dev.foraged.enchants.listener

import dev.foraged.commons.annotations.Listeners
import dev.foraged.enchants.enchant.EnchantService
import net.minecraft.server.v1_8_R3.Container
import net.minecraft.server.v1_8_R3.InventorySubcontainer
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventory
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftInventoryEnchanting
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import kotlin.random.Random

@Listeners
object EnchantTableListener : Listener
{
    @EventHandler
    fun onTable(event: EnchantItemEvent) {
        event.enchantsToAdd.forEach {
            val enchant = EnchantService.findEnchantByEnchantment(it.key) ?: return@forEach

            enchant.apply(event.item, it.value)
        }
        ((event.inventory as CraftInventoryEnchanting).inventory).update()
        event.enchanter.updateInventory()
    }
}