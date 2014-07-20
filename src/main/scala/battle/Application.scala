package battle

import actors.characters.GladiatorActor
import actors.world.{WorldActor, MapActor}
import actors.world.MapActor._
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import battle.GameBoard.Coordinate
import util.FutureHelper._

import scala.concurrent.duration._
import scala.io.StdIn


object Application extends App {

  def mapChanged(event: MapChangedMessage): Unit = {
    drawBoard(event.board)
  }

  def drawBoard(board: GameBoard) : Unit = {

    val separator = " -" * board.width + " "

    var lists: List[List[String]] = List()
    (0 until board.height).foreach {y =>
      var internalList = List[String]()
      (0 until board.width).foreach { x =>
        internalList = internalList :+ (board.get(x, y) match {
          case Some(x) => "X"
          case None => " "
        })
      }
      lists = lists :+ internalList
    }

    println(separator)
    lists.foreach { list =>
      println("|" + list.mkString("|") + "|")
      println(separator)
    }
  }

  implicit val timeout = Timeout(5.seconds)

  val system = ActorSystem("mySystem")
  val world = system.actorOf(WorldActor.props(mapChanged))
  val gladiators = List(system.actorOf(GladiatorActor.props(Gladiator("John"))), system.actorOf(GladiatorActor.props(Gladiator("Mary"))))
  val map = system.actorOf(MapActor.props(world, gladiators(0), gladiators(1)))

  var exit = false

  val FindRegex = "find (\\d)".r
  val MoveRegex = "move (\\d) (\\d),(\\d)".r
  val UpRegex = "(\\d) up".r
  val DownRegex = "(\\d) down".r
  val LeftRegex = "(\\d) left".r
  val RightRegex = "(\\d) right".r

  Thread.sleep(500)

  while (!exit) {
    val line = StdIn.readLine("Command : ")

    line match {
      case FindRegex(x) => {
        val future = map ? GetGladiatorCoordinates(gladiators(x.toInt))
        val coord = future.waitOnResult[Coordinate]()
        println(s"${coord.x}, ${coord.y}")
      }
      case MoveRegex(pos, x, y) => map ! MoveGladiatorMessage(gladiators(pos.toInt), x.toInt, y.toInt)
      case UpRegex(pos) => map ! MoveUpMessage(gladiators(pos.toInt))
      case DownRegex(pos) => map ! MoveDownMessage(gladiators(pos.toInt))
      case LeftRegex(pos) => map ! MoveLeftMessage(gladiators(pos.toInt))
      case RightRegex(pos) => map ! MoveRightMessage(gladiators(pos.toInt))
      case "exit" => exit = true
      case x => println(s"Unknown Command : ${x}")
    }

    Thread.sleep(300)
  }

  system.shutdown()
  system.awaitTermination(10.seconds)

  println("Exit System")

}
