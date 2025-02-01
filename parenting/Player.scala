package parenting

import scala.collection.mutable.{Map, Buffer}

/** A `Player` object represents a player character controlled by the real-life user
  * of the program.
  *
  * A player object’s state is mutable: the player’s location and possessions can change,
  * for instance.
  *
  * @param startingArea  the player’s initial location */
class Player(startingArea: Area):

  private val babyproofingItems = Item("babyproofing items", "They're some things to make the cabin safer for the baby")
  private val watch             = Item("watch", "It's your trusty old time piece. It surely knows how much time you have left.")
  private var items             = Map[String, Item](babyproofingItems.name -> babyproofingItems, watch.name -> watch)

  private var currentLocation   = startingArea        // gatherer: changes in relation to the previous location
  private var quitCommandGiven  = false              // one-way flag
  private var helpCalled        = 0

  var turnCounter               = 0
  val maxTurns                  = 50

  /** Determines if the player has indicated a desire to quit the game. */
  def hasQuit = this.quitCommandGiven

  /** Returns the player’s current location. */
  def location = this.currentLocation


  /** Determines if there is an item to pick up, and if there is, whether that item can be picked up
    * i.e. is the item a normal item or does the player's inventory contain the item required to pick this up */
  def get(itemName: String): String =
    this.location.removeItem(itemName) match

      case Some(item) =>
        item match

          case dependentItem: DependentItem =>

            if this.items.contains(dependentItem.requiredItem.name) then
              items += itemName -> item
              s"You pick up the $itemName."
            else
              this.location.addItem(item)
              s"You cannot pick up the $itemName. You need a ${dependentItem.requiredItem.name} to pick it up."

          case _ =>
            this.items += itemName -> item
            s"You pick up the $itemName."

      case None =>
        s"There is no $itemName here to pick up."


  /** Checks if the given item is in the player's inventory. */
  def has(itemName: String): Boolean = this.items.contains(itemName)


  /** Examines the given item and gives the player a description of it. */
  def examine(itemName: String): String =
    this.items.get(itemName) match
      case Some(item) =>
        item match
          case container: WrapperItem =>
            s"You look closely at the $itemName.\n${container.description} Perhaps you could try to open it."
          case _ =>
            s"You look closely at the $itemName.\n${item.description}"
      case None =>
        "If you want to examine something, you need to pick it up first."


  /** If possible, drops an item the player is holding. If the dropped item is another item's requiredItem,
    * both items are dropped. */
  def drop(itemName: String): String =
    if this.has(itemName) then
      val dependentItems = Buffer[DependentItem]()

      for (name, item) <- items do
        item match
          case item: DependentItem =>
            dependentItems += item
          case _ =>

      if dependentItems.isEmpty || dependentItems.forall(_.requiredItem.name != itemName) then
        this.location.addItem(this.items(itemName))
        this.items.remove(itemName)
        s"You drop the $itemName."
      else
        val anotherItem: Item = dependentItems.filter(_.requiredItem.name == itemName).head
        this.location.addItem(this.items(itemName))
        this.location.addItem(anotherItem)
        this.items.remove(itemName)
        this.items.remove(anotherItem.name)
        s"You drop the $itemName and the ${anotherItem.name}."

    else
      "You don't have that!"


  def inventory: String =
    if this.items.isEmpty then
      "You are empty-handed."
    else
      "You are carrying:\n" + this.items.keys.mkString("\n")


  /** Opens an item by retrieving the item contained within and adds it to the player's
    * inventory, discarding the wrapper item */
  private def unwrap(wrapper: WrapperItem): String =
    this.items += wrapper.wraps.name -> wrapper.wraps
    this.items.remove(wrapper.name)
    s"You open the ${wrapper.name}. It contains a ${wrapper.wraps.name}."


  /** Babyproofs the Cabin. */
  private def babyproof: String =
    if this.location.name == "Cabin" then
      this.location.babyproof
      this.items.remove("babyproofing items")
      "You babyproof the cabin to be ready for the baby."
    else
      "You can't babyproof this area."


  /** Tells the player how big the current time pressure is. */
  private def timer: String =
    val turnsLeft: Int = this.maxTurns - this.turnCounter
    if turnsLeft >= 40 then
      "There's still a lot of time left. No need to hurry."
    else if turnsLeft >= 30 then
      "Time waits for no one. Better get a move on."
    else if turnsLeft >= 20 then
      "You want the baby to come home, right? Stop dilly-dallying."
    else if turnsLeft >= 10 then
      "What are you waiting for? Chop chop!"
    else
      "HURRY UP!!"


  /** Uses the given item if the player has it and has a predetermined use. */
  def use(itemName: String): String =
    this.items.get(itemName) match

      case Some(item) =>
        item match

          case wrapper: WrapperItem =>
            this.unwrap(wrapper)

          case _ =>
            if itemName == "babyproofing items" then
              this.babyproof
            else if itemName == "watch" then
              this.timer
            else
              "Using this item does nothing."

      case None =>
        "You don't have that."


  /** The game ends if the player goes into an area carrying an item that has been "forbidden" in the area */
  def fail: Boolean =
    this.currentLocation match
      case location: AreaWithConflict => this.items.contains(location.forbiddenItem.name)
      case _ => false


  /** Gives the player some hints on what to do. If help has been called multiple times,
    * increases the clarity of the hints. */
  def help: String =
    this.helpCalled += 1
    if this.helpCalled == 1 then
      "Collect all the items available, babyproof the house and bring the baby home.\n\n" +
      "Some of the items can be used by calling \"use [item]\".\n\n" +
      "Beware! One of the areas cannot be traversed while pushing the baby in a pram."
    else if this.helpCalled == 2 then
      "Collect all the items available, babyproof the house and bring the baby home.\n\n" +
      "Three of the items can be used by calling \"use [item]\".\n\n" +
      "Did you notice that you do not start with an empty inventory?\n\n" +
      "You cannot caryy the baby by itself. You need something else to pick it up.\n\n" +
      "Beware! One of the areas cannot be traversed while pushing the baby in a pram."
    else if this.helpCalled == 3 then
      "Collect all the items available, babyproof the house and bring the baby home.\n\n" +
      "\"babyproofing items\", \"package\" and \"watch\" can be used by calling \"use [item]\".\n\n" +
      "Did you notice that you do not start with an empty inventory?\n\n" +
      "You need a pram to pick up the baby.\n\n" +
      "Beware! The swamp cannot be traversed while pushing the baby in a pram."
    else
      "Collect all the items available, babyproof the house and bring the baby home.\n\n" +
      "\"babyproofing items\", \"package\" and \"watch\" can be used by calling \"use [item]\".\n" +
      "Notice that one of the items can only be used in the cabin.\n\n" +
      "Initially you have a watch and some babyproofing items in your inventory.\n\n" +
      "Get the pram from the package to pick up the baby.\n\n" +
      "Do not enter the swamp while carrying the baby in your inventory."


  /** Attempts to move the player in the given direction. This is successful if there
    * is an exit from the player’s current location towards the direction name. Returns
    * a description of the result: "You go DIRECTION." or "You can't go DIRECTION." */
  def go(direction: String) =
    val destination = this.location.neighbor(direction)
    this.currentLocation = destination.getOrElse(this.currentLocation)
    if destination.isDefined then "You go " + direction + "." else "You can't go " + direction + "."


  /** Causes the player to rest for a short while (this has no substantial effect in game terms).
    * Returns a description of what happened. */
  def rest() =
    "You take a small breather. Better get a move on, though. The baby won't adopt itself."


  /** Signals that the player wants to quit the game. Returns a description of what happened within
    * the game as a result (which is the empty string, in this case). */
  def quit() =
    this.quitCommandGiven = true
    ""

  /** Returns a brief description of the player’s state, for debugging purposes. */
  override def toString = "Now at: " + this.location.name

end Player

