package battle

import battle.GameBoard.Coordinate

class GameBoard(val width: Int, val height: Int) {

  private val grid = Array.ofDim[AnyRef](width, height)

  def put(x: Int, y: Int, obj: AnyRef): Unit = {
    grid(x)(y) = obj
  }

  def get(x: Int, y: Int): Option[AnyRef] = {
    if (grid(x)(y) == null) None
    else Some(grid(x)(y))
  }

  def move(obj: AnyRef, x: Int, y: Int): Unit = {
    val coordinate = find(obj)
    grid(coordinate.x)(coordinate.y) = null
    grid(x)(y) = obj
  }

  private def find(obj: AnyRef) : Coordinate = {
    val col = for {
      xCoord <- 0 until width
      yCoord <- 0 until height
      if (grid(xCoord)(yCoord) == obj)
    } yield Coordinate(xCoord, yCoord)
    col(0)
  }

}

object GameBoard {
  def apply(width: Int, height: Int) = new GameBoard(width, height)

  case class Coordinate(val x: Int, val y: Int)

}
