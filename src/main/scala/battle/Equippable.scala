package battle

import battle.Equippable.{Attack, AdjustmentType}

trait Equippable {

  class Adjustment(val adjustmentType: AdjustmentType, val value: Int)

  class AdjustmentApi(adjustmentType: AdjustmentType) {
    def +(x: Int): Adjustment = new Adjustment(adjustmentType, x)
    def -(x: Int): Adjustment = new Adjustment(adjustmentType, -x)
  }

  def attack: AdjustmentApi = {
    new AdjustmentApi(Attack)
  }

  object whenAttacking {
    def apply(newAdjustments: Adjustment*) = adjustments = adjustments ++ newAdjustments
  }

  var adjustments: List[Adjustment] = Nil
  def adjustment(adjustmentType: AdjustmentType): Integer = {
    adjustments.filter(_.adjustmentType == adjustmentType).map(_.value).sum
  }

}

object Equippable {

  sealed trait AdjustmentType
  object Attack extends AdjustmentType

}

