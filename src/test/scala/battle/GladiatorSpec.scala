package battle

import battle.Ability.{Constitution, Strength}
import battle.classes.{GladiatorClass, Fighter}
import battle.races.Race
import org.scalatest.{FlatSpecLike, Matchers}

class GladiatorSpec extends Matchers with FlatSpecLike {

  "A gladiator" should "have default armor class of 10" in {
    val gladiator = Gladiator("John")
    gladiator.armorClass should be (10)
  }

  it should "have default hitPoits of 5" in {
    val gladiator = Gladiator(null)
    gladiator.hitpoints should be (5)
  }

  it should "add constituion modifier to hitpoints" in {
    val gladiator = Gladiator(name = "John", abilities = Map(Constitution -> Ability(12)))
    gladiator.hitpoints should be (6)
  }

  it should "lower hitpoints by damage" in {
    val gladiator = Gladiator(null)
    gladiator.applyDamage(1)
    gladiator.hitpoints should be (4)
  }

  it should "not be alizve if hitpoints are zero" in {
    val gladiator = Gladiator(null)
    gladiator.applyDamage(gladiator.hitpoints)
    gladiator.alive should be (false)
  }

  it should "be alive if hitpoints are greater than zero" in {
    val gladiator = Gladiator(null)
    gladiator.applyDamage(gladiator.hitpoints-1)
    gladiator.alive should be (true)
  }

  it should "not be alive if hitpoints are negative" in {
    val gladiator = Gladiator(null)
    gladiator.applyDamage(gladiator.hitpoints+1)
    gladiator.alive should be (false)
  }

  it should "have a name" in {
    val gladiator = Gladiator("John")

    gladiator.name should be ("John")
  }

  it should "have a strength ability" in {
    val gladiator = Gladiator("Howard", Map(Strength -> Ability(11)))
    gladiator.ability(Strength) should be (Ability(11))
  }

  it should "have a default strength ability of 10" in {
    val gladiator = Gladiator("Joel")
    gladiator.ability(Strength) should be (Ability())
  }

  it should "have a GladitorClass" in {
    val gladiator = Gladiator(name = "John", gladiatorClass = Fighter)
    gladiator.gladiatorClass should be (Fighter)
  }

  it should "have an attack bonus that adds strength modifier" in {
    val gladiator = Gladiator(name = "attacker", abilities = Map(Strength -> Ability(12)))
    gladiator.attackBonus(Gladiator("defender")) should be (1)
  }

  "The Attack roll" should "include gladiator class" in {
    object FakeClass extends GladiatorClass {
      attack + 2
    }

    val gladiator = Gladiator(name = "George", gladiatorClass = FakeClass, abilities = Map(Strength -> Ability(12)))
    gladiator.attackBonus(Gladiator("defender")) should be (3)
  }

  it should "include race" in {
    object FakeRace extends Race {
      attack + 2
    }

    val gladiator = Gladiator(name = "attacker", race = FakeRace)
    gladiator.attackBonus(Gladiator("defender")) should be (2)
  }



}


