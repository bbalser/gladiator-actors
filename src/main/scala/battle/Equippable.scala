package battle

import battle.Equippable._

import scala.util.DynamicVariable

trait Equippable {

  var adjustments: List[Adjustment] = Nil
  private val dynamicDefender = new DynamicVariable[Gladiator](null)
  private val dynamicAttacker = new DynamicVariable[Gladiator](null)

  def defender = dynamicDefender.value
  def attacker = dynamicAttacker.value

  class Adjustment(val adjustmentType: AdjustmentType, thunk: => Int) {
    def value = thunk
  }

  class AdjustmentApi(adjustmentType: AdjustmentType) {
    def +(x: => Int): Unit = add(new Adjustment(adjustmentType, x))
    def -(x: => Int): Unit = add(new Adjustment(adjustmentType, -x))
  }

  def attack: AdjustmentApi = new AdjustmentApi(Attack)
  def defend: AdjustmentApi = new AdjustmentApi(Defense)
  def dexterity: AdjustmentApi = new AdjustmentApi(Dexterity)
  def strength: AdjustmentApi = new AdjustmentApi(Strength)
  def constitution: AdjustmentApi = new AdjustmentApi(Constitution)

  def adjustment(adjustmentType: AdjustmentType, attacker: Gladiator, defender: Gladiator): Integer = {
    dynamicAttacker.withValue(attacker) {
      dynamicDefender.withValue(defender) {
        adjustments.filter(_.adjustmentType == adjustmentType).map(_.value).sum
      }
    }
  }

  private def add(adjustment: Adjustment) = adjustments = adjustments :+ adjustment

}

object Equippable {

  sealed trait AdjustmentType
  object Attack extends AdjustmentType
  object Defense extends AdjustmentType
  object Dexterity extends AdjustmentType
  object Strength extends AdjustmentType
  object Constitution extends AdjustmentType

}

