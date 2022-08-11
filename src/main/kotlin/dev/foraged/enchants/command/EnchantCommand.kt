package dev.foraged.enchants.command

import dev.foraged.commons.acf.CommandHelp
import dev.foraged.commons.acf.ConditionFailedException
import dev.foraged.commons.acf.annotation.*
import dev.foraged.commons.annotations.commands.AutoRegister
import dev.foraged.commons.annotations.commands.customizer.CommandManagerCustomizer
import dev.foraged.commons.command.CommandManager
import dev.foraged.commons.command.GoodCommand
import dev.foraged.enchants.enchant.Enchant
import dev.foraged.enchants.enchant.EnchantService
import dev.foraged.enchants.enchant.result.EnchantPaginatedResult
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.ItemUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("enchant")
@CommandPermission("enchants.enchant.management")
@AutoRegister
object EnchantCommand : GoodCommand()
{
    @CommandManagerCustomizer
    fun customizer(manager: CommandManager) {
        manager.commandContexts.registerContext(Enchant::class.java) {
            val enchant = it.popFirstArg().lowercase()
            return@registerContext EnchantService.findEnchant(enchant) ?: throw ConditionFailedException("There is no enchant registered with the id \"${enchant}\".")
        }
    }

    @HelpCommand
    fun help(commandHelp: CommandHelp) {
        commandHelp.showHelp()
    }

    @Subcommand("star")
    fun star(sender: CommandSender, enchant: Enchant, level: Int, amount: Int, target: Player) {
        target.inventory.addItem(ItemBuilder.copyOf(enchant.buildEnchantStar(level)).amount(amount).build())
        sender.sendMessage("${CC.SEC}You have given ${target.displayName} ${CC.PRI}$amount${CC.SEC} level ${CC.PRI}${level} ${enchant.displayName}${CC.SEC} enchant star.")
    }

    @Subcommand("apply")
    fun apply(sender: Player, enchant: Enchant, level: Int) {
        if (sender.itemInHand == null) throw ConditionFailedException("You must be holding an item to apply an enchant.")

        enchant.apply(sender.itemInHand, level)
        sender.sendMessage("${CC.SEC}You have applied ${enchant.displayName}${CC.SEC} at level ${CC.PRI}$level ${CC.SEC}to ${CC.PRI}${ItemUtils.getChatName(sender.itemInHand)}${CC.SEC}.")
    }

    @Subcommand("list")
    fun list(sender: CommandSender, @Default("1") page: Int) {
        EnchantPaginatedResult.display(sender, EnchantService.enchants.values, page)
    }
}