package actors.world

import actors.world.MapActor._
import akka.actor.{Actor, ActorRef, Props}
import battle.GameBoard
import battle.GameBoard.Coordinate

class MapActor(worldActor : ActorRef, val gladiatorActor1: ActorRef, val gladiatorActor2: ActorRef) extends Actor {

  val board = GameBoard(10,10)
  board.put(2, 3, gladiatorActor1)
  board.put(8, 7, gladiatorActor2)
  worldActor ! MapChangedMessage(board)

  override def receive: Receive = {
    case get : GetGladiatorCoordinates => sender ! board.find(get.gladiatorActor)
    case up : MoveUpMessage => handleMove(up.gladiator, Up)
    case down: MoveDownMessage => handleMove(down.gladiator, Down)
    case left: MoveLeftMessage => handleMove(left.gladiator, Left)
    case right: MoveRightMessage => handleMove(right.gladiator, Right)
  }

  private def handleAddGladiator(add : AddGladiatorMessage) = {
    board.get(2,3) match {
      case Some(_) => board.put(8, 7, add.gladiatorActor)
      case None => board.put(2, 3, add.gladiatorActor)
    }
    worldActor ! MapChangedMessage(board)
  }

  private def handleMove(gladiator: ActorRef, direction: Direction): Unit = {
    val coordinate = board.find(gladiator)
    board.move(gladiator, direction.move(coordinate))
    worldActor ! MapChangedMessage(board)
  }

}

object MapActor {

  def props(worldActor : ActorRef, gladiatorRef1: ActorRef, gladiatorRef2: ActorRef) = Props(classOf[MapActor], worldActor, gladiatorRef1, gladiatorRef2)

  case class AddGladiatorMessage(gladiatorActor : ActorRef)
  case class MoveGladiatorMessage(gladiatorActor : ActorRef, x : Int, y : Int)
  case class GetGladiatorCoordinates(gladiatorActor : ActorRef)
  case class MapChangedMessage(board: GameBoard)

  case class MoveUpMessage(gladiator: ActorRef)
  case class MoveDownMessage(gladiator: ActorRef)
  case class MoveLeftMessage(gladiator: ActorRef)
  case class MoveRightMessage(gladiator: ActorRef)

  sealed abstract class Direction {
    def move(coord: Coordinate): Coordinate
  }

  object Up extends Direction {
    override def move(coord: Coordinate): Coordinate = {
      Coordinate(coord.x, coord.y-1)
    }
  }

  object Down extends Direction {
    override def move(coord: Coordinate): Coordinate = {
      Coordinate(coord.x, coord.y+1)
    }
  }

  object Left extends Direction {
    override def move(coord: Coordinate): Coordinate = {
      Coordinate(coord.x-1, coord.y)
    }
  }

  object Right extends Direction {
    override def move(coord: Coordinate): Coordinate = {
      Coordinate(coord.x+1, coord.y)
    }
  }


}
