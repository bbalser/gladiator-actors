package messages.attacks

import battle.GameBoard.Coordinate
import battle.Gladiator

class AttackMessage(attacker : Gladiator, val coordinate: Coordinate, roll : Int) {

  def successful(defender : Gladiator) : Boolean = {
    roll >= defender.armorClass
  }

  def damage(defender : Gladiator) : Int = {
    if (roll == 20) 2 else 1
  }

}

object AttackMessage {
  def apply(attacker : Gladiator, coordinate: Coordinate, roll : Int) = new AttackMessage(attacker, coordinate, roll)
}
