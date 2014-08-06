package actors.world

import actors.characters.GladiatorActor
import actors.characters.GladiatorActor.GladiatorChangedMessage
import actors.world.MapActor._
import actors.world.WorldActor.{AddListener, MoveGladiatorMessage, StartMessage, AddGladiatorMessage}
import akka.actor.{Actor, ActorRef, Props}
import akka.util.Timeout
import battle.Gladiator
import messages.attacks.AttackMessage
import akka.pattern.ask
import util.FutureHelper._
import scala.concurrent.duration._

class WorldActor extends Actor {

  implicit val timeout = new Timeout(5.seconds)

  var gladiators = Map[Gladiator, ActorRef]()
  var listeners: List[ActorRef] = Nil

  var map: ActorRef = null

  override def receive: Receive = {
    case add : AddListener => listeners = listeners :+ add.actorRef
    case add : AddGladiatorMessage => addGladiator(add.gladiator)
    case move: MoveGladiatorMessage => map ! MoveMessage(move.gladiator, move.direction)
    case StartMessage => map = context.actorOf(MapActor.props(gladiators.keys))
    case event : MapChangedMessage => forwardEvent(event)
    case event: GladiatorChangedMessage => forwardEvent(event)
    case attack : AttackMessage => handleAttack(attack)
  }

  def forwardEvent(event: AnyRef) = {
    listeners.foreach(_ ! event)
  }

  def handleAttack(attack: AttackMessage) = {
    val future = map ? GetEntity(attack.coordinate)
    val entity = future.waitOnResult[ReturnEntity]().entity
    entity.foreach{ x =>
      gladiators(x.asInstanceOf[Gladiator]) ! attack
    }
  }

  def addGladiator(gladiator: Gladiator) {
    val ref = context.actorOf(GladiatorActor.props(gladiator))
    gladiators = gladiators + (gladiator -> ref)
  }
}

object WorldActor {

  def props() = Props(classOf[WorldActor])

  case class AddGladiatorMessage(gladiator: Gladiator)

  case object StartMessage

  case class MoveGladiatorMessage(gladiator: Gladiator, direction: Direction)

  case class AddListener(actorRef: ActorRef)

}
