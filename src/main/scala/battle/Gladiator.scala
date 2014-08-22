package battle

import battle.Ability.{Strength, Constitution, Dexterity, AbilityType}
import battle.Equippable.Attack
import battle.classes.{NoClass, GladiatorClass}
import battle.races.{Race, Human}

class Gladiator(val name: String,
                abilities: Map[AbilityType, Ability],
                val gladiatorClass: GladiatorClass,
                val race: Race) {

  private var _hitpoints = 5 + ability(Constitution).modifier

  def armorClass = 10 + ability(Dexterity).modifier

  def applyDamage(damage: Int): Unit = _hitpoints -= damage

  def hitpoints = _hitpoints

  def alive = hitpoints > 0

  def ability(abilityType: AbilityType): Ability = abilities.getOrElse(abilityType, Ability())

  def attackBonus(defender: Gladiator): Int = {
    List(gladiatorClass, race).map(x => x.adjustment(Attack, this, defender)).sum +
      ability(Strength).modifier
  }

}

object Gladiator {
  def apply(name: String,
            abilities: Map[AbilityType, Ability] = Map(),
            gladiatorClass: GladiatorClass = NoClass,
            race: Race = Human) = new Gladiator(name, abilities, gladiatorClass, race)
}
