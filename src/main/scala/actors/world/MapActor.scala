package actors.world

import actors.world.MapActor.{MapChangedMessage, GetGladiatorCoordinates, MoveGladiatorMessage, AddGladiatorMessage}
import akka.actor.{Actor, ActorRef, Props}
import battle.GameBoard

class MapActor(worldActor : ActorRef) extends Actor {

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
    worldActor ! MapChangedMessage(board)
  }

  private def handleMoveGladiator(move : MoveGladiatorMessage) = {
    board.move(move.gladiatorActor, move.x, move.y)
    worldActor ! MapChangedMessage(board)
  }

}

object MapActor {

  def props(worldActor : ActorRef) = Props(classOf[MapActor], worldActor)

  case class AddGladiatorMessage(val gladiatorActor : ActorRef)
  case class MoveGladiatorMessage(val gladiatorActor : ActorRef, val x : Int, val y : Int)
  case class GetGladiatorCoordinates(val gladiatorActor : ActorRef)
  case class MapChangedMessage(val board: GameBoard)

}
