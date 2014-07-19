package messages.attacks

import battle.Gladiator
import org.scalatest.{FlatSpecLike, Matchers}

class AttackMessageSpec
  extends Matchers
  with FlatSpecLike {

  "An AttackMessage" should "should be success full if attack roll is equal to armor class" in {
    val attack = AttackMessage(Gladiator("John"), 10)
    attack.successful(Gladiator("John")) should be (true)
  }

  it should "be unsuccessful when attack roll is less than armor class" in {
    val attack = AttackMessage(Gladiator("John"), 9)
    attack.successful(Gladiator("John")) should be (false)
  }

  it should "be successful when attack roll is greater than armore class" in {
    val attack = AttackMessage(Gladiator("John"), 11)
    attack.successful(Gladiator("John")) should be (true)
  }

  it should "have default damage be 1" in {
    val attack = AttackMessage(Gladiator("John"), 10)
    attack.damage(Gladiator("John")) should be (1)
  }

  it should "have damage of 2 when roll is 20" in {
    val attack = AttackMessage(Gladiator("John"), 20)
    attack.damage(Gladiator("John")) should be (2)
  }
}
