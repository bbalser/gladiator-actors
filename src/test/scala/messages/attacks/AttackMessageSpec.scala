package messages.attacks

import battle.Ability.{Dexterity, Strength}
import battle.GameBoard.Coordinate
import battle.{Ability, Gladiator}
import org.scalatest.{FlatSpecLike, Matchers}

class AttackMessageSpec
  extends Matchers
  with FlatSpecLike {

  "An AttackMessage" should "should be success full if attack roll is equal to armor class" in {
    val attack = AttackMessage(Gladiator("John"), Coordinate(1,1), 10)
    attack.successful(Gladiator("John")) should be (true)
  }

  it should "be unsuccessful when attack roll is less than armor class" in {
    val attack = AttackMessage(Gladiator("John"), Coordinate(1,1), 9)
    attack.successful(Gladiator("John")) should be (false)
  }

  it should "be successful when attack roll is greater than armor class" in {
    val attack = AttackMessage(Gladiator("John"), Coordinate(1,1), 11)
    attack.successful(Gladiator("John")) should be (true)
  }

  it should "have default damage be 1" in {
    val attack = AttackMessage(Gladiator("John"), Coordinate(1,1), 10)
    attack.damage(Gladiator("John")) should be (1)
  }

  it should "have damage of 2 when roll is 20" in {
    val attack = AttackMessage(Gladiator("John"), Coordinate(1,1), 20)
    attack.damage(Gladiator("John")) should be (2)
  }

  it should "add strength modifier of attacker to attack roll" in {
    val attack = AttackMessage(Gladiator(name = "John", abilities = Map(Strength -> Ability(12))), null, 9)
    attack.successful(Gladiator("defender")) should be (true)
  }

  it should "add strength modifier of attacker to damage dealt" in {
    val attack = AttackMessage(Gladiator(name = "J", abilities = Map(Strength -> Ability(12))), null, 10)
    attack.damage(Gladiator("defender")) should be (2)
  }

  it should "add double strength modifier of attacker to damage dealt when hit is critical" in {
    val attack = AttackMessage(Gladiator(name = "J", abilities = Map(Strength -> Ability(12))), null, 20)
    attack.damage(Gladiator("defender")) should be (4)
  }

  it should "have a minimum damage of 1 regardless of strength modifier" in {
    val attack = AttackMessage(Gladiator(name = "J", abilities = Map(Strength -> Ability(9))), null, 10)
    attack.damage(Gladiator("defender")) should be (1)
  }

  it should "add dexterity modifier from defender to armor class" in {
    val attack = AttackMessage(Gladiator("attacker"), null, 10)
    attack.successful(Gladiator(name = "defender", abilities = Map(Dexterity -> Ability(12)))) should be (false)
  }

}
