package parenting

import scala.collection.mutable.Map

/** The class `Area` represents locations in a text adventure game world. A game world
  * consists of areas. In general, an “area” can be pretty much anything: a room, a building,
  * an acre of forest, or something completely different. What different areas have in
  * common is that players can be located in them and that they can have exits leading to
  * other, neighboring areas. An area also has a name and a description.
  * @param name         the name of the area
  * @param description  a basic description of the area (typically not including information about items) */
class Area(var name: String, var description: String):

  private val neighbors   = Map[String, Area]()
  private var items       = Map[String, Item]()
  private var babyproofed = false

  /** Returns the area that can be reached from this area by moving in the given direction. The result
    * is returned in an `Option`; `None` is returned if there is no exit in the given direction. */
  def neighbor(direction: String) = this.neighbors.get(direction)

  /** Adds an exit from this area to the given area. The neighboring area is reached by moving in
    * the specified direction from this area. */
  def setNeighbor(direction: String, neighbor: Area) =
    this.neighbors += direction -> neighbor

  /** Adds exits from this area to the given areas. Calling this method is equivalent to calling
    * the `setNeighbor` method on each of the given direction–area pairs.
    * @param exits  contains pairs consisting of a direction and the neighboring area in that direction
    * @see [[setNeighbor]] */
  def setNeighbors(exits: Vector[(String, Area)]) =
    this.neighbors ++= exits

  def addItem(item: Item): Unit =
    this.items += item.name -> item

  def removeItem(itemName: String): Option[Item] =
    if this.contains(itemName) then
      this.items.remove(itemName)
    else
      None

  def contains(itemName: String): Boolean = this.items.contains(itemName)

  /** Babyproofs the area. */
  def babyproof: Unit =
    this.babyproofed = true
    this.description += "\nThe cabin has been babyproofed."
  
  /** used to check whether the area is babyproof */
  def isBabyproof: Boolean = this.babyproofed
  
  /** Returns a multi-line description of the area as a player sees it. This includes a basic
    * description of the area as well as information about exits and items. If there are no
    * items present, the return value has the form "DESCRIPTION\n\nExits available:
    * DIRECTIONS SEPARATED BY SPACES". If there are one or more items present, the return
    * value has the form "DESCRIPTION\nYou see here: ITEMS SEPARATED BY SPACES\n\nExits available:
    * DIRECTIONS SEPARATED BY SPACES". The items and directions are listed in an arbitrary order. */
  def fullDescription =
    val itemList = "\nYou see here: " + this.items.keys.mkString(", ")
    val exitList = "\n\nExits available: " + this.neighbors.keys.mkString(" ")
    if this.items.nonEmpty then
      this.description + itemList + exitList
    else
      this.description + exitList


  /** Returns a single-line description of the area for debugging purposes. */
  override def toString = this.name + ": " + this.description.replaceAll("\n", " ").take(150)

end Area

/** Extension of the class area so that if the player enters the area carrying the forbidden item, the player loses
  * @param forbiddenItem  the item the player cannot carry in the area */
class AreaWithConflict(name: String, description: String, val forbiddenItem: Item) extends Area(name, description)