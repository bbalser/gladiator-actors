package battle

import battle.Ability.Strength
import battle.classes.Fighter
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

}
