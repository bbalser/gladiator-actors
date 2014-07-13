package messages.attacks

import battle.Gladiator

class AttackMessage(attacker : Gladiator, roll : Int) {

  def successful(defender : Gladiator) : Boolean = {
    roll >= defender.armorClass
  }

  def damage(defender : Gladiator) : Int = {
    if (roll == 20) 2 else 1
  }


}

object AttackMessage {
  def apply(attacker : Gladiator, roll : Int) = new AttackMessage(attacker, roll)
}
