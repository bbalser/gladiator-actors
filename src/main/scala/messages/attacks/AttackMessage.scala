package messages.attacks

import battle.Ability.Strength
import battle.GameBoard.Coordinate
import battle.Gladiator

class AttackMessage(attacker: Gladiator, val coordinate: Coordinate, roll: Int) {

  def successful(defender: Gladiator): Boolean = {
    attackRoll >= defender.armorClass
  }

  def damage(defender: Gladiator): Int = {
    val multiplier = if (isCriticalHit) 2 else 1
    (baseDamage * multiplier).max(1)
  }

  private def baseDamage: Int = {
    1 + attacker.ability(Strength).modifier
  }

  private def attackRoll = roll + attacker.ability(Strength).modifier

  private def isCriticalHit = roll == 20

}

object AttackMessage {
  def apply(attacker: Gladiator, coordinate: Coordinate, roll: Int) = new AttackMessage(attacker, coordinate, roll)
}
