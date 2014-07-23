package actors.world

import actors.characters.GladiatorActor
import actors.characters.GladiatorActor.GladiatorChangedMessage
import actors.world.MapActor.{MoveMessage, Direction, MapChangedMessage}
import actors.world.WorldActor.{MoveGladiatorMessage, StartMessage, AddGladiatorMessage}
import akka.actor.{Actor, ActorRef, Props}
import battle.Gladiator

class WorldActor(mapChanged: MapChangedMessage => Unit,
                 gladiatorChanged: GladiatorChangedMessage => Unit) extends Actor {

  var gladiators = Map[Gladiator, ActorRef]()
  var map: ActorRef = null

  override def receive: Receive = {
    case add : AddGladiatorMessage => addGladiator(add.gladiator)
    case move: MoveGladiatorMessage => map ! MoveMessage(move.gladiator, move.direction)
    case start : StartMessage => map = context.actorOf(MapActor.props(gladiators.keys))
    case mapEvent : MapChangedMessage => mapChanged(mapEvent)
    case gladiatorEvent: GladiatorChangedMessage => gladiatorChanged(gladiatorEvent)

  }

  def addGladiator(gladiator: Gladiator) {
    val ref = context.actorOf(GladiatorActor.props(gladiator))
    gladiators = gladiators + (gladiator -> ref)
  }
}

object WorldActor {

  def props(mapChanged: MapChangedMessage => Unit, gladiatorChanged: GladiatorChangedMessage => Unit) = Props(classOf[WorldActor], mapChanged, gladiatorChanged)
  case class AddGladiatorMessage(gladiator: Gladiator)

  class StartMessage

  case class MoveGladiatorMessage(gladiator: Gladiator, direction: Direction)

}
