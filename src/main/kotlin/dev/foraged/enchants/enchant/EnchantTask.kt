package dev.foraged.enchants.enchant

import dev.foraged.commons.annotations.runnables.Repeating
import dev.foraged.enchants.enchant.impl.RepairEnchant
import org.bukkit.Bukkit
import org.bukkit.Material

@Repeating(20L)
class EnchantTask : Runnable
{
    override fun run()
    {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            player.inventory.armorContents.filterNotNull().filter { it.type != Material.AIR }.forEach { item ->
                EnchantService.findEnchants(item).filter { it.key.tickWorn }.forEach {
                    it.key.tick(player, it.value)
                }
            }

            if (player.itemInHand != null && player.itemInHand.type != Material.AIR) {
                EnchantService.findEnchants(player.itemInHand).filter { it.key.tickHeld }.forEach {
                    it.key.tick(player, it.value)
                }
            }
        }
    }
}