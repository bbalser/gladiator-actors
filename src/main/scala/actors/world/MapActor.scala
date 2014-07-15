package actors.world

import actors.world.MapActor.{GetGladiatorCoordinates, MoveGladiatorMessage, AddGladiatorMessage}
import akka.actor.{Actor, ActorRef, Props}
import battle.GameBoard

class MapActor extends Actor {

  val board = GameBoard(10,10)

  override def receive: Receive = {
    case get : GetGladiatorCoordinates => sender ! board.find(get.gladiatorActor)
    case add : AddGladiatorMessage => handleAddGladiator(add)
    case move: MoveGladiatorMessage => handleMoveGladiator(move)
  }

  private def handleAddGladiator(add : AddGladiatorMessage) = {
    board.get(2,3) match {
      case Some(_) => board.put(8, 7, add.gladiatorActor)
      case None => board.put(2, 3, add.gladiatorActor)
    }
  }

  private def handleMoveGladiator(move : MoveGladiatorMessage) = {
    board.move(move.gladiatorActor, move.x, move.y)
  }

}

object MapActor {

  def props = Props(classOf[MapActor])

  case class AddGladiatorMessage(val gladiatorActor : ActorRef)
  case class MoveGladiatorMessage(val gladiatorActor : ActorRef, val x : Int, val y : Int)
  case class GetGladiatorCoordinates(val gladiatorActor : ActorRef)

}
