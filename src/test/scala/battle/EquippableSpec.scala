package battle

import battle.Equippable.Attack
import org.scalatest.{Matchers, FlatSpecLike}

class EquippableSpec extends Matchers with FlatSpecLike {

  "An Equippable" should "add 1 to attackRoll when defined with attack + 1 " in {

    object TestSword extends Equippable {
      whenAttacking {
        attack + 1
      }
    }

    TestSword.adjustment(Attack) should be (1)
  }

  it should "be allowed to subtract one from attack as well" in {
    object TestSword extends Equippable {
      whenAttacking {
        attack - 1
      }
    }

    TestSword.adjustment(Attack) should be (-1)
  }

}

