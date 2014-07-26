package battle

import battle.Equippable.{Defense, Attack}
import org.scalatest.{Matchers, FlatSpecLike}

class EquippableSpec extends Matchers with FlatSpecLike {

  "An Equippable" should "add 1 to attackRoll when defined with attack + 1 " in {

    object TestSword extends Equippable {
      attack + 1
    }

    TestSword.adjustment(Attack, null, null) should be (1)
  }

  it should "be allowed to subtract one from attack as well" in {
    object TestSword extends Equippable {
      attack - 1
    }

    TestSword.adjustment(Attack, null, null) should be (-1)
  }

  it should "get access to defender to help in decision making" in {
    object TestSword extends Equippable {
      attack + {defender.name match {
          case "John" => 2
          case _ => 1
      }}
    }

    TestSword.adjustment(Attack, null, Gladiator("John")) should be (2)
    TestSword.adjustment(Attack, null, Gladiator("David")) should be (1)
  }

  it should "allow additions to Defense" in {
    object TestShield extends Equippable {
      defend + 1
    }

    TestShield.adjustment(Defense, null, null) should be (1)
  }

  it should "have access to attacker to help make decisions" in {
    object TestShield extends Equippable {
      defend + {attacker.hitpoints match {
        case x if x > 3 => 2
        case _ => 1
      }}
    }

    val attacker = Gladiator("John")
    TestShield.adjustment(adjustmentType = Defense, attacker = attacker, defender = Gladiator("Mary")) should be (2)
    attacker.applyDamage(2)
    TestShield.adjustment(adjustmentType = Defense, attacker = attacker, defender = Gladiator("Mary")) should be (1)
  }

}

