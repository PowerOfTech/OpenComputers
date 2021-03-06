package li.cil.oc.common

import cpw.mods.fml.common.network.IGuiHandler
import li.cil.oc.common.init.Items
import li.cil.oc.common.inventory.{DatabaseInventory, ServerInventory}
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World

abstract class GuiHandler extends IGuiHandler {
  override def getServerGuiElement(id: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int) =
    world.getTileEntity(x, y, z) match {
      case t: tileentity.Adapter if id == GuiType.Adapter.id =>
        new container.Adapter(player.inventory, t)
      case t: tileentity.Assembler if id == GuiType.Assembler.id =>
        new container.Assembler(player.inventory, t)
      case t: tileentity.Charger if id == GuiType.Charger.id =>
        new container.Charger(player.inventory, t)
      case t: tileentity.Case if id == GuiType.Case.id =>
        new container.Case(player.inventory, t)
      case t: tileentity.Disassembler if id == GuiType.Disassembler.id =>
        new container.Disassembler(player.inventory, t)
      case t: tileentity.DiskDrive if id == GuiType.DiskDrive.id =>
        new container.DiskDrive(player.inventory, t)
      case t: tileentity.Raid if id == GuiType.Raid.id =>
        new container.Raid(player.inventory, t)
      case t: tileentity.RobotProxy if id == GuiType.Robot.id =>
        new container.Robot(player.inventory, t.robot)
      case t: tileentity.ServerRack if id == GuiType.Rack.id =>
        new container.ServerRack(player.inventory, t)
      case t: tileentity.Switch if id == GuiType.Switch.id =>
        new container.Switch(player.inventory, t)
      case _ => Items.multi.subItem(player.getCurrentEquippedItem) match {
        case Some(database: item.UpgradeDatabase) if id == GuiType.Database.id =>
          new container.Database(player.inventory, new DatabaseInventory {
            override def tier = database.tier

            override def container = player.getCurrentEquippedItem

            override def isUseableByPlayer(player: EntityPlayer) = player == player
          })
        case Some(server: item.Server) if id == GuiType.Server.id =>
          new container.Server(player.inventory, new ServerInventory {
            override def tier = server.tier

            override def container = player.getCurrentEquippedItem

            override def isUseableByPlayer(player: EntityPlayer) = player == player
          })
        case _ => null
      }
    }
}