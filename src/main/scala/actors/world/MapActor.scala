package actors.world

import actors.world.MapActor._
import akka.actor.{Actor, Props}
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
    case get : GetEntity => {
      sender ! ReturnEntity(board.get(get.coordinate))
    }
  }

  private def handleMove(entity: AnyRef, direction: Direction): Unit = {
    val coordinate = board.find(entity)
    board.move(entity, direction.move(coordinate))
    context.parent ! MapChangedMessage(board)
  }

  private def addEntitiesToBoard = {
    def randomCoordinate = Coordinate(Random.nextInt(board.width), Random.nextInt(board.height))
    def randomGenerator: Stream[Coordinate] = Stream.cons(randomCoordinate,randomGenerator)
    val randomList = randomGenerator

    entities.foreach { ref =>
      val coordinate = randomList.find(c => board.get(c).isEmpty ).get
      board.put(ref, coordinate)
    }
  }

}

object MapActor {

  def props(entities: Iterable[AnyRef]) = Props(classOf[MapActor], entities)

  case class MoveMessage(entity : AnyRef, direction: Direction)
  case class GetCoordinate(entity : AnyRef)
  case class MapChangedMessage(board: GameBoard)
  case class GetEntity(coordinate: Coordinate)
  case class ReturnEntity(entity: Option[AnyRef])


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
