package messages.attacks

import battle.Gladiator
import org.scalatest.{FlatSpecLike, Matchers}

class AttackMessageSpec
  extends Matchers
  with FlatSpecLike {

  "An AttackMessage" should "should be success full if attack roll is equal to armor class" in {
    val attack = AttackMessage(Gladiator(), 10)
    attack.successful(Gladiator()) should be (true)
  }

  it should "be unsuccessful when attack roll is less than armor class" in {
    val attack = AttackMessage(Gladiator(), 9)
    attack.successful(Gladiator()) should be (false)
  }

  it should "be successful when attack roll is greater than armore class" in {
    val attack = AttackMessage(Gladiator(), 11)
    attack.successful(Gladiator()) should be (true)
  }

  it should "have default damage be 1" in {
    val attack = AttackMessage(Gladiator(), 10)
    attack.damage(Gladiator()) should be (1)
  }

  it should "have damage of 2 when roll is 20" in {
    val attack = AttackMessage(Gladiator(), 20)
    attack.damage(Gladiator()) should be (2)
  }
}
