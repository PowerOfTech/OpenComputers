package li.cil.oc.integration.nei

import codechicken.nei.NEIClientConfig
import codechicken.nei.api.API
import codechicken.nei.api.IConfigureNEI
import codechicken.nei.config.OptionToggleButton
import codechicken.nei.guihook.GuiContainerManager
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import li.cil.oc.integration.util.NEI
import net.minecraft.item.ItemStack

@SideOnly(Side.CLIENT)
class NEIOpenComputersConfig extends IConfigureNEI {
  override def getName = "OpenComputers"

  override def getVersion = "1.1.0"

  override def loadConfig() {
    // Non-alphabetic order haunts my OCD, but I want the "Manual" to show up
    // before the API doc.
    API.registerUsageHandler(new GeneralUsageHandler())
    API.registerUsageHandler(new CallbackDocHandler())

    // Add option to show items' ore dictionary name in tooltips.
    NEIClientConfig.global.config.getTag("inventory.oredict").getBooleanValue(false)
    val oreDictOption = new OptionToggleButton("inventory.oredict", true)
    GuiContainerManager.addTooltipHandler(new OredictTooltipHandler())
    API.addOption(oreDictOption)

    for (block <- NEI.hiddenBlocks) {
      API.hideItem(new ItemStack(block))
    }
  }
}
