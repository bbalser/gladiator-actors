package actors.world

import actors.world.MapActor._
import akka.actor.{Actor, ActorRef, Props}
import battle.GameBoard
import battle.GameBoard.Coordinate

import scala.util.Random

class MapActor(val entities: Iterable[AnyRef]) extends Actor {

  val board = GameBoard(10,10)
  addEntitiesToBoard
  context.parent ! MapChangedMessage(board)

  override def receive: Receive = {
    case get : GetCoordinate => sender ! board.find(get.entity)
    case move : MoveMessage => handleMove(move.entity, move.direction)
  }

  private def handleMove(entity: AnyRef, direction: Direction): Unit = {
    val coordinate = board.find(entity)
    board.move(entity, direction.move(coordinate))
    context.parent ! MapChangedMessage(board)
  }

  private def randomCoordinate = Coordinate(Random.nextInt(board.width), Random.nextInt(board.height))

  private def addEntitiesToBoard = {
    entities.foreach { ref =>
      var coordinate = randomCoordinate
      var placed = false

      while (!placed) {
        board.get(coordinate) match {
          case None => board.put(ref, coordinate); placed = true
          case Some(exists) => coordinate = randomCoordinate
        }
      }
    }
  }

}

object MapActor {

  def props(entities: Iterable[AnyRef]) = Props(classOf[MapActor], entities)

  case class MoveMessage(entity : AnyRef, direction: Direction)
  case class GetCoordinate(entity : AnyRef)
  case class MapChangedMessage(board: GameBoard)


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
