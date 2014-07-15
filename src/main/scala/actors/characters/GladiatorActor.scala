package actors.characters

import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive
import battle.Gladiator
import messages.attacks.AttackMessage

class GladiatorActor(val gladiator : Gladiator) extends Actor {

  override def receive: Receive = {
    case attack : AttackMessage => handleAttack(attack)
  }

  def handleAttack(attack : AttackMessage) : Unit = {
    if (attack.successful(gladiator)) {
      val damage = attack.damage(gladiator)
      gladiator.applyDamage(damage)
    }
  }

}

object GladiatorActor {
  def props(gladiator : Gladiator) = Props(classOf[GladiatorActor], gladiator)
}
