package battle.classes

import battle.Equippable.{Hitpoints, Attack}
import org.scalatest.{FlatSpecLike, Matchers}

class FighterSpec extends Matchers with FlatSpecLike {

  "A Fighter" should "have 1 added to attack roll" in {
    Fighter.adjustment(Attack, null, null) should be (1)
  }

  it should "have 10 hitpoints" in {
    Fighter.adjustment(Hitpoints, null, null) should be (5)
  }

}
