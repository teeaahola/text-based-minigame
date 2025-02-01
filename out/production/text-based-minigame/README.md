# Witchy Parenting -- A Text-based Minigame

## Description

You are a witch that has promised a man eternal youth in exhange for his firstborn.
The time of the exchange has come, but you are not prepared and must perform tasks
to prepare for the baby.

You have to babyproof your cabin and gather a binky, a baby bottle, a book on parenting,
a pram and the baby to finish. The binky, baby bottle, books and pram can be collected in any
order, but the baby cannot be picked up before the pram. Initially your inventory contains 
"babyproofing items" and "watch". To win, you must bring the baby home before running out of time.

## Game Design

The game map consists of nine different areas, the cabin, post office, playground, the clinet's 
house, a swamp, an empty parking lot, two stores and the player's mother's house. The player can 
move from are to area according to the available exits. The items the player needs to complete 
the game are scattered in different parts of the map.

<img width="1000" alt="A map of the game." scr="/map.png"/>

A sample successful walkthrough of the game is provided in [walkthough.txt](https://github.com/teeaahola/text-based-minigame/blob/main/walkthrough.txt).

Available player actions:
- help - gives the pleyer some hints on completing the game. If called multiple times, gives 
progressively more details on recommended actions
- go DIRECTION - move to specified direction if available
- get ITEM - adds the item to player's inventory if possible. The baby cannot be added to the
inventory before the inventory contains a pram
- examine ITEM - gives a description of an item in the inventory. Tells the player if the item 
can be used
- use ITEM - uses an item in the player's inventory if it has a predetermined use case. Only the 
package, watch and babyproofing items can be used
- drop ITEM - removes an item from the player's inventory and adds it to the current location
- inventory - lists the items currently in the player's inventory. The player's inventory is 
initialized with two items, "watch" and "babyproofing items" in it
- spell - adds whimsy by casting "Bibbidi-Bobbidi-Boo."
- rest - do nothing for one turn
- quit - quits the game

Run the ParentingTextUI to play the game.