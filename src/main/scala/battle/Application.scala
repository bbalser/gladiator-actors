package battle

import actors.characters.GladiatorActor
import actors.world.{WorldActor, MapActor}
import actors.world.MapActor.{MapChangedMessage, AddGladiatorMessage, GetGladiatorCoordinates, MoveGladiatorMessage}
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
  val map = system.actorOf(MapActor.props(world))

  val gladiators = List(system.actorOf(GladiatorActor.props(Gladiator())), system.actorOf(GladiatorActor.props(Gladiator())))


  map ! AddGladiatorMessage(gladiators(0))
  map ! AddGladiatorMessage(gladiators(1))

  var exit = false

  val FindRegex = "find (\\d)".r
  val MoveRegex = "move (\\d) (\\d),(\\d)".r


  while (!exit) {
    val line = StdIn.readLine("Command : ")

    line match {
      case FindRegex(x) => {
        val future = map ? GetGladiatorCoordinates(gladiators(x.toInt))
        val coord = future.waitOnResult[Coordinate]()
        println(s"${coord.x}, ${coord.y}")
      }
      case MoveRegex(pos, x, y) => {
        map ! MoveGladiatorMessage(gladiators(pos.toInt), x.toInt, y.toInt)
      }
      case "exit" => exit = true
      case x => println(s"Unknown Command : ${x}")
    }

    Thread.sleep(1000)
  }

  system.shutdown()
  system.awaitTermination(10.seconds)

  println("Exit System")

}
