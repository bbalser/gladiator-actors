package actors.world

import actors.characters.GladiatorActor
import actors.world.MapActor.{MoveMessage, Direction, MapChangedMessage}
import actors.world.WorldActor.{MoveGladiatorMessage, StartMessage, AddGladiatorMessage}
import akka.actor.{Actor, ActorRef, Props}
import battle.Gladiator

class WorldActor(mapChanged: MapChangedMessage => Unit) extends Actor {

  var gladiators = Map[Gladiator, ActorRef]()
  var map: ActorRef = null

  override def receive: Receive = {
    case add : AddGladiatorMessage => {
      val ref = context.actorOf(GladiatorActor.props(add.gladiator))
      gladiators = gladiators + (add.gladiator -> ref)
    }
    case move: MoveGladiatorMessage => map ! MoveMessage(move.gladiator, move.direction)
    case start : StartMessage => map = context.actorOf(MapActor.props(gladiators.keys))
    case mapEvent : MapChangedMessage => mapChanged(mapEvent)

  }
}

object WorldActor {

  def props(mapChanged: MapChangedMessage => Unit) = Props(classOf[WorldActor], mapChanged)
  case class AddGladiatorMessage(gladiator: Gladiator)

  class StartMessage

  case class MoveGladiatorMessage(gladiator: Gladiator, direction: Direction)

}
