package parenting

class Parenting:

  /** the name of the game */
  val title = "Witchy Parenting"

  val pram        = Item("pram",           "It's a pram to move the baby around in.")
  val postPackage = WrapperItem("package", "It's a package with your name on it.", pram)
  val book        = Item("book",           "It's an old book on parenting.")
  val baby        = DependentItem("baby",  "It's your new child. (´･ᴗ･`)", pram)
  val binky       = Item("binky",          "It's a binky for the baby.")
  val babyBottle  = Item("bottle",         "It's a baby bottle to feed the baby with.")

  private val cabin       = Area("Cabin",               "You are home in your cabin in the woods. There are a lot of potions and spellbooks here.")
  private val swamp       = AreaWithConflict("Swamp",   "You are at a mysterious swamp.\nBe careful, the ground does not look too stable.", baby)
  private val mumsHouse   = Area("Your mother's house", "You are at your childhood home where your mother still lives.\nYou have some fond memories here.")
  private val postOffice  = Area("The post office",     "You are at the local post office.")
  private val playground  = Area("A playground",        "You are at a playground.\nThere are swingsets and slides here.")
  private val emptyLot    = Area("An empty lot",        "You are at a deserted parking lot.\nThe sky looks grey.")
  private val store1      = Area("A store",             "You are at a local small store.")
  private val store2      = Area("A large store",       "You are at a hypermarket. Surely there's something here you can buy.")
  private val house       = Area("A house",             "You are at the house where your client lives.")
  private val destination = cabin

  cabin      .setNeighbors(Vector("north" -> swamp,                            "south" -> mumsHouse,   "west" -> postOffice ))
  swamp      .setNeighbors(Vector("north" -> swamp,      "east" -> emptyLot,   "south" -> cabin,       "west" -> playground ))
  mumsHouse  .setNeighbors(Vector("north" -> cabin,      "east" -> store2,                             "west" -> mumsHouse  ))
  postOffice .setNeighbors(Vector("north" -> playground, "east" -> cabin                                                    ))
  playground .setNeighbors(Vector("north" -> emptyLot,   "east" -> swamp,                              "west" -> house      ))
  emptyLot   .setNeighbors(Vector(                                             "south" -> store1,      "west" -> swamp      ))
  store1     .setNeighbors(Vector("north" -> emptyLot,   "east" -> store2,                             "west" -> cabin      ))
  store2     .setNeighbors(Vector("north" -> store1,                                                   "west" -> mumsHouse  ))
  house      .setNeighbor("east", playground)

  postOffice .addItem(postPackage)
  mumsHouse  .addItem(book)
  house      .addItem(baby)
  store1     .addItem(binky)
  store2     .addItem(babyBottle)

  /** The character that the player controls in the game. */
  val player = Player(cabin)

  /** The number of turns that have passed since the start of the game. */
  var turnCount: Int = 0
  /** The maximum number of turns that this adventure game allows before time runs out. */
  val timeLimit: Int = this.player.maxTurns

  /** Determines if the fail condition has been fulfilled */
  def failed: Boolean = this.player.fail

  /** Determines if the adventure is complete, that is, if the player has won. */
  def isComplete: Boolean =
    val finishingItems: Vector[String] = Vector("pram", "book", "baby", "binky", "bottle")
    this.player.location == this.destination
      && finishingItems.forall(this.player.inventory.contains(_))
      && this.player.location.isBabyproof

  /** Determines whether the player has won, lost, quit, or failed, thereby ending the game. */
  def isOver: Boolean = this.isComplete || this.player.hasQuit || this.turnCount == this.timeLimit || this.failed

  /** Returns a message that is to be displayed to the player at the beginning of the game. */
  def welcomeMessage: String =
    "You are a witch relaxing at home.\n\n" +
    "You have promised a local man eternal youth in exchange for his firstborn.\n" +
    "The baby has been born and is waiting to be picked up at the man's house, but\n" +
    "you've been so caught up in developing a new potion that you forgot to do any\n" +
    "preparations for the baby.\n" +
    "Before bringing the baby home there are many errands to run.\n\n" +
    "Time to hurry up."


  /** Returns a message that is to be displayed to the player at the end of the game. The message
    * will be different depending on whether or not the player has completed their quest. */
  def goodbyeMessage =
    if this.isComplete then
      "Home again, but this time ready to be a parent!\nWell done!"
    else if this.turnCount == this.timeLimit then
      "Oh no! Time's up. You took too long and the man has decided to keep the baby and grow old alongside it.\nGame over!"
    else if this.failed then
      "Oh no! There are no good paths for a pram in the swamp. Crying, the baby sinks into a bog hole.\nGame over!"
    else // game over due to player quitting
      "You become the second parent that has given up on the baby...\nNot a promising start for a life. Shame on you!"


  /** Plays a turn by executing the given in-game command, such as “go west”. Returns a textual
    * report of what happened, or an error message if the command was unknown. In the latter
    * case, no turns elapse. */
  def playTurn(command: String) =
    val action = Action(command)
    val outcomeReport = action.execute(this.player)
    if outcomeReport.isDefined then
      this.player.turnCounter += 1
      this.turnCount += 1
    outcomeReport.getOrElse(s"""Unknown command: "$command".""")

end Parenting

