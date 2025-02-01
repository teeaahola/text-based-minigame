package parenting

/** The class `Item` represents items in a text adventure game. Each item has a name
  * and a longer description. (In later versions of the adventure game, items may
  * have other features as well.)
  *
  * N.B. It is assumed, but not enforced by this class, that items have unique names.
  * That is, no two items in a game world have the same name.
  *
  * @param name         the item’s name
  * @param description  the item’s description*/
class Item(val name: String, val description: String):

  /** Returns a short textual representation of the item (its name, that is). */
  override def toString = this.name

end Item


/** Extends the class item, making a "wrapper" item that has another item concealed within.
  * @param wraps  the item concealed within this */
class WrapperItem(name: String, description: String, val wraps: Item) extends Item(name, description)


/** Extends the class item, creating a one-sided dependency between two items so that the item cannot
  * be picked up if the player is not carrying the required item.
  * @param requiredItem  the item this requires in the player's inventory to be picked up or carried */
class DependentItem(name: String, description: String, val requiredItem: Item) extends Item(name, description)