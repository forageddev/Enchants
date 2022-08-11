package dev.foraged.enchants.enchant.result

import dev.foraged.enchants.enchant.Enchant
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.PaginatedResult

object EnchantPaginatedResult : PaginatedResult<Enchant>()
{
    override fun getHeader(page: Int, maxPages: Int) = "${CC.PRI}=== ${CC.SEC}Enchants ${CC.WHITE}($page/$maxPages) ${CC.PRI}==="

    override fun format(result: Enchant, resultIndex: Int): String
    {
        return " ${CC.GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${result.displayName} ${CC.GRAY}(Max: ${result.maxLevel})"
    }
}