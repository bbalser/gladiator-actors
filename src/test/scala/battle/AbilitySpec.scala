package battle

import battle.Ability._
import org.scalatest.{Matchers, FlatSpecLike}

class AbilitySpec extends Matchers with FlatSpecLike {

  "An ability" should "have a score" in {
    val ability = Ability(15)
    ability.score should be(15)
  }

  it should "have a modifier based on the score" in {
    val modifiers = Map(1 -> -5, 2 -> -4, 3 -> -4, 4 -> -3, 5 -> -3, 6 -> -2, 7 -> -2, 8 -> -1, 9 -> -1, 10 -> 0,
                        11 -> 0, 12 -> 1, 13 -> 1, 14 -> 2, 15 -> 2, 16 -> 3, 17 -> 3, 18 -> 4, 19 -> 4, 20 -> 5)

    modifiers.foreach { case (k, v) =>
      val ability = Ability(k)
      ability.modifier should be (v)
    }
  }

  it should "have a minimum of 1" in {
    val thrown = intercept[IllegalArgumentException] {
      Ability(0)
    }
    thrown.getMessage should be ("requirement failed: Ability must be 1 or higher")
  }

  it should "have a maximum of 20" in {
    val thrown = intercept[IllegalArgumentException] {
      Ability(21)
    }
    thrown.getMessage should be ("requirement failed: Ability must be 20 or lower")
  }

  it should "have a default score of 10" in {
    val ability = Ability()
    ability.score should be (10)
  }

  it should "be addable" in {
    val ability = Ability(5) + 10
    ability.score should be (15)
  }

  it should "be subtractable" in {
    val ability = Ability(10) - 3
    ability.score should be (7)
  }

  it should "have all ability types defined" in {
    Ability.types should be (List(Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma))
  }

}
