package battle

import battle.GameBoard.Coordinate

class GameBoard(val width: Int, val height: Int) {

  private val grid = Array.ofDim[AnyRef](width, height)

  def put(obj: AnyRef, coordinate: Coordinate): Unit = {
    grid(coordinate.x)(coordinate.y) = obj
  }

  def get(coordinate: Coordinate): Option[AnyRef] = {
    if (grid(coordinate.x)(coordinate.y) == null) None
    else Some(grid(coordinate.x)(coordinate.y))
  }

  def move(obj: AnyRef, newCoordinate: Coordinate): Unit = {
    val coordinate = find(obj)
    grid(coordinate.x)(coordinate.y) = null
    grid(newCoordinate.x)(newCoordinate.y) = obj
  }

  def find(obj: AnyRef) : Coordinate = {
    val col = for {
      xCoord <- 0 until width
      yCoord <- 0 until height
      if grid(xCoord)(yCoord) == obj
    } yield Coordinate(xCoord, yCoord)
    col(0)
  }

}

object GameBoard {
  def apply(width: Int, height: Int) = new GameBoard(width, height)

  case class Coordinate(x: Int, y: Int)

}
