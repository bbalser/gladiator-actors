package actors.world

import actors.world.MapActor.MapChangedMessage
import akka.actor.{Props, Actor}
import akka.actor.Actor.Receive

class WorldActor(mapChanged: MapChangedMessage => Unit) extends Actor {
  override def receive: Receive = {
    case mapEvent : MapChangedMessage => mapChanged(mapEvent)
  }
}

object WorldActor {

  def props(mapChanged: MapChangedMessage => Unit) = Props(classOf[WorldActor], mapChanged)

}
