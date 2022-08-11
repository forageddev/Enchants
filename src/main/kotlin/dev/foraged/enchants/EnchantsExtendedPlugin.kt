package dev.foraged.enchants

import dev.foraged.commons.ExtendedPaperPlugin
import dev.foraged.commons.annotations.container.ContainerDisable
import dev.foraged.commons.annotations.container.ContainerEnable
import dev.foraged.commons.config.ConfigContainer
import dev.foraged.commons.config.annotations.ContainerConfig
import dev.foraged.enchants.configuration.EffectEnchantConfiguration
import dev.foraged.enchants.configuration.MinecraftEnchantConfiguration
import dev.foraged.enchants.enchant.Enchant
import dev.foraged.enchants.enchant.EnchantService
import dev.foraged.enchants.enchant.override.VanillaOverride
import dev.foraged.enchants.enchant.wrapper.EffectEnchantWrapper
import dev.foraged.enchants.enchant.wrapper.VanillaEnchantWrapper
import me.lucko.helper.plugin.ap.Plugin
import me.lucko.helper.plugin.ap.PluginDependency
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentWrapper
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

@Plugin(
    name = "Enchants",
    version = "\${git.commit.id.abbrev}",
    depends = [
        PluginDependency("Commons")
    ]
)
@ContainerConfig(
    value = "minecraft",
    model = MinecraftEnchantConfiguration::class,
    crossSync = false
)
@ContainerConfig(
    value = "effect",
    model = EffectEnchantConfiguration::class,
    crossSync = false
)
class EnchantsExtendedPlugin : ExtendedPaperPlugin()
{
    companion object {
        lateinit var instance: EnchantsExtendedPlugin
    }

    @ContainerEnable
    fun containerEnable()
    {
        instance = this
        if (config<MinecraftEnchantConfiguration>().enchants.isEmpty())
        {
            Enchantment.values().filterNotNull().forEach {
                config<MinecraftEnchantConfiguration>().enchants.add(VanillaEnchantWrapper(EnchantmentWrapper(it.id)))
            }
            configContainerized.container<MinecraftEnchantConfiguration>().save()
        }

        if (config<EffectEnchantConfiguration>().enchants.isEmpty())
        {
            PotionEffectType.values().filterNotNull().forEach {
                config<EffectEnchantConfiguration>().enchants.add(EffectEnchantWrapper(it))
            }
            configContainerized.container<EffectEnchantConfiguration>().save()
        }

        reloadConfigurationEnchants()
    }

    @ContainerDisable
    fun containerDisable() {
        configContainerized.configContainers.forEach { (t, u) ->
            u.save()
        }
    }

    override fun configContainerUpdate(config: ConfigContainer) {
        reloadConfigurationEnchants()
    }

    fun reloadConfigurationEnchants() {
        config<MinecraftEnchantConfiguration>().enchants.forEach {
            it.enchant = Enchantment.getById(it.enchantId)
            it.color = ChatColor.valueOf(it.chatColor)

            EnchantService.registerEnchant(object :
                Enchant(it.name.lowercase(), it.displayName, it.color, it.maxLevel, it.enabled), VanillaOverride
            {
                override fun canEnchant(item: ItemStack): Boolean = it.enchant.canEnchantItem(item)
                override val override: Enchantment get() = it.enchant
            })
        }

        config<EffectEnchantConfiguration>().enchants.forEach {
            it.effect = PotionEffectType.getById(it.effectId)
            it.color = ChatColor.valueOf(it.chatColor)
            it.itemTypes = it.items.map { ItemUtils.ItemType.valueOf(it) }.toMutableList()

            EnchantService.registerEnchant(object : Enchant(
                it.name.lowercase(),
                it.displayName,
                it.color,
                it.maxLevel,
                it.enabled,
                tickHeld = it.tickHeld,
                tickWorn = it.tickWorn
            )
            {
                override fun canEnchant(item: ItemStack): Boolean {
                    for (type in it.itemTypes) if (type.isOfType(item)) return true
                    return false
                }

                override fun tick(player: Player, level: Int){
                    player.addPotionEffect(
                        PotionEffect(
                            it.effect,
                            it.durations[level.toString()] ?: return,
                            it.amplifiers[level.toString()] ?: return
                        ), true
                    )
                }
            })
        }
    }
}
