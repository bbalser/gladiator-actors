package battle

import battle.Ability.{Constitution, Dexterity, AbilityType}
import battle.classes.{NoClass, GladiatorClass}

class Gladiator(val name: String,
                abilities: Map[AbilityType, Ability],
                val gladiatorClass: GladiatorClass) {

  private var _hitpoints = 5 + ability(Constitution).modifier

  def armorClass = 10 + ability(Dexterity).modifier

  def applyDamage(damage: Int): Unit = {
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
  def apply(name: String,
            abilities: Map[AbilityType, Ability] = Map(),
            gladiatorClass: GladiatorClass = NoClass) = new Gladiator(name, abilities, gladiatorClass)
}
