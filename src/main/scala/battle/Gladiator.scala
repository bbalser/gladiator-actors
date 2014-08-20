package battle

import battle.Ability.AbilityType

class Gladiator(val name: String, abilities: Map[AbilityType, Ability]) {

  private var _hitpoints = 5
  val armorClass = 10

  def applyDamage(damage : Int) : Unit = {
    _hitpoints -= damage
  }

  def hitpoints = {
    _hitpoints
  }

  def alive = {
    hitpoints > 0
  }

  def ability(abilityType: AbilityType): Ability = abilities.getOrElse(abilityType, Ability())

}

object Gladiator {
  def apply(name: String, abilities: Map[AbilityType, Ability] = Map()) = new Gladiator(name, abilities)
}
