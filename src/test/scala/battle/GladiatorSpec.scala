package battle

import org.scalatest.{FlatSpecLike, Matchers}

class GladiatorSpec extends Matchers with FlatSpecLike {

  "A gladiator" should "have default armor class of 10" in {
    val gladiator = Gladiator()
    gladiator.armorClass should be (10)
  }

  it should "have default hitPoits of 5" in {
    val gladiator = Gladiator()
    gladiator.hitpoints should be (5)
  }

  it should "lower hitpoints by damage" in {
    val gladiator = Gladiator()
    gladiator.applyDamage(1)
    gladiator.hitpoints should be (4)
  }

  it should "not be alizve if hitpoints are zero" in {
    val gladiator = Gladiator()
    gladiator.applyDamage(gladiator.hitpoints)
    gladiator.alive should be (false)
  }

  it should "be alive if hitpoints are greater than zero" in {
    val gladiator = Gladiator()
    gladiator.applyDamage(gladiator.hitpoints-1)
    gladiator.alive should be (true)
  }

  it should "not be alive if hitpoints are negative" in {
    val gladiator = Gladiator()
    gladiator.applyDamage(gladiator.hitpoints+1)
    gladiator.alive should be (false)
  }

}
