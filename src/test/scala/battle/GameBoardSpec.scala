package battle

import battle.GameBoard.Coordinate
import org.scalatest.{FlatSpecLike, Matchers}

class GameBoardSpec extends Matchers with FlatSpecLike {

  "A Game Board" should "have a height and width" in {
    val board = GameBoard(5, 5)
    board.height should be (5)
    board.width should be (5)
  }

  it should "store an object at any location" in {
    val obj = new Object()
    val board = GameBoard(5,5)
    board.put(obj, Coordinate(2, 3))
    board.get(Coordinate(2, 3)).get should be (obj)
  }

  it should "move an object to any location" in {
    val obj = new Object()
    val board = GameBoard(5, 5)
    board.put(obj, Coordinate(1, 1))
    board.move(obj, Coordinate(4, 4))

    board.get(Coordinate(1, 1)) should be (None)
    board.get(Coordinate(4, 4)) should be (Some(obj))
  }


}
