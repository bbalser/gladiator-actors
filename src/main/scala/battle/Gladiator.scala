package battle

class Gladiator(val name: String) {

  private var _hitpoints = 5
  val armorClass = 10

  def applyDamage(damage : Int) : Unit = {
    _hitpoints -= damage
  }

  def hitpoints = {
    _hitpoints
  }

  def alive = {
    hitpoints > 0
  }

}

object Gladiator {
  def apply(name: String) = new Gladiator(name)
}
