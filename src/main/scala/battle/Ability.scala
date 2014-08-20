package battle

case class Ability(score: Int = Ability.DEFAULT_SCORE) {
  require(score > 0, "Ability must be 1 or higher")
  require(score < 21, "Ability must be 20 or lower")

  def modifier = (score / 2) - 5

  def +(x: Int): Ability = new Ability(score + x)
  
  def -(x: Int): Ability = new Ability(score - x)

}

object Ability {
  val DEFAULT_SCORE = 10

  sealed class AbilityType
  case object Strength extends AbilityType
  case object Dexterity extends AbilityType
  case object Constitution extends AbilityType
  case object Intelligence extends AbilityType
  case object Wisdom extends AbilityType
  case object Charisma extends AbilityType

  def types: List[AbilityType] = List(Strength, Dexterity, Constitution, Intelligence, Wisdom, Charisma)

}