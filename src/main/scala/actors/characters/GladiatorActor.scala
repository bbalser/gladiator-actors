package actors.characters

import actors.characters.GladiatorActor.GladiatorChangedMessage
import akka.actor.{Actor, Props}
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
      context.parent ! GladiatorChangedMessage(gladiator)
    }
  }

}

object GladiatorActor {
  def props(gladiator : Gladiator) = Props(classOf[GladiatorActor], gladiator)

  case class GladiatorChangedMessage(gladiator: Gladiator)

}
