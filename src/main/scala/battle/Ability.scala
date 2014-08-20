package battle

case class Ability(val score: Int = Ability.DEFAULT_SCORE) {
  require(score > 0, "Ability must be 1 or higher")
  require(score < 21, "Ability must be 20 or lower")

  def modifier = (score / 2) - 5

}

object Ability {
  val DEFAULT_SCORE = 10
}