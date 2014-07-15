package battle

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
    board.put(2,3,obj)
    board.get(2,3).get should be (obj)
  }

  it should "move an object to any location" in {
    val obj = new Object()
    val board = GameBoard(5, 5)
    board.put(1,1, obj)
    board.move(obj, 4, 4)

    board.get(1, 1) should be (None)
    board.get(4, 4) should be (Some(obj))
  }


}
