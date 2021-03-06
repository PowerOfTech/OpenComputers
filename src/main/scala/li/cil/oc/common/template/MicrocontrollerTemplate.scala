package li.cil.oc.common.template

import cpw.mods.fml.common.event.FMLInterModComms
import li.cil.oc.Settings
import li.cil.oc.api
import li.cil.oc.api.internal
import li.cil.oc.common.Slot
import li.cil.oc.common.Tier
import li.cil.oc.common.template.TabletTemplate.hasComponent
import li.cil.oc.common.template.TabletTemplate.hasFileSystem
import li.cil.oc.util.ExtendedNBT._
import li.cil.oc.util.ItemUtils
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList

object MicrocontrollerTemplate extends Template {
  override protected val suggestedComponents = Array(
    "BIOS" -> hasComponent("eeprom") _)

  override protected def hostClass = classOf[internal.Microcontroller]

  def select(stack: ItemStack) = api.Items.get(stack) == api.Items.get("microcontrollerCase")

  def validate(inventory: IInventory): Array[AnyRef] = validateComputer(inventory)

  def assemble(inventory: IInventory) = {
    val items = (0 until inventory.getSizeInventory).map(inventory.getStackInSlot)
    val data = new ItemUtils.MicrocontrollerData()
    data.components = items.drop(1).filter(_ != null).toArray
    val stack = api.Items.get("microcontroller").createItemStack(1)
    data.save(stack)
    val energy = Settings.get.microcontrollerBaseCost + complexity(inventory) * Settings.get.microcontrollerComplexityCost

    Array(stack, double2Double(energy))
  }

  def register() {
    val nbt = new NBTTagCompound()
    nbt.setString("name", "Microcontroller")
    nbt.setString("select", "li.cil.oc.common.template.MicrocontrollerTemplate.select")
    nbt.setString("validate", "li.cil.oc.common.template.MicrocontrollerTemplate.validate")
    nbt.setString("assemble", "li.cil.oc.common.template.MicrocontrollerTemplate.assemble")
    nbt.setString("hostClass", "li.cil.oc.api.internal.Microcontroller")

    val upgradeSlots = new NBTTagList()
    upgradeSlots.appendTag(Map("tier" -> Tier.Any))
    nbt.setTag("upgradeSlots", upgradeSlots)

    val componentSlots = new NBTTagList()
    componentSlots.appendTag(Map("type" -> Slot.Card, "tier" -> Tier.One))
    componentSlots.appendTag(Map("type" -> Slot.Card, "tier" -> Tier.One))
    componentSlots.appendTag(new NBTTagCompound())
    componentSlots.appendTag(Map("type" -> Slot.CPU, "tier" -> Tier.One))
    componentSlots.appendTag(Map("type" -> Slot.Memory, "tier" -> Tier.One))
    componentSlots.appendTag(new NBTTagCompound())
    componentSlots.appendTag(Map("type" -> Slot.EEPROM, "tier" -> Tier.Any))
    nbt.setTag("componentSlots", componentSlots)

    FMLInterModComms.sendMessage("OpenComputers", "registerAssemblerTemplate", nbt)
  }

  override protected def maxComplexity(inventory: IInventory) = 4

  override protected def caseTier(inventory: IInventory) = if (select(inventory.getStackInSlot(0))) Tier.One else Tier.None
}
