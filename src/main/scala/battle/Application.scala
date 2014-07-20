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

  def createGladiator(name: String) = name -> system.actorOf(GladiatorActor.props(Gladiator(name)))

  def drawBoard(board: GameBoard) : Unit = {

    val separator = " -" * board.width + " "

    var lists: List[List[String]] = List()
    (0 until board.height).foreach {y =>
      var internalList = List[String]()
      (0 until board.width).foreach { x =>
        internalList = internalList :+ (board.get(x, y) match {
          case Some(ref) => gladiators.find { case (k, v) => v == ref}.getOrElse(("X", null))._1.substring(0,1)
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

  val gladiators = Map(createGladiator("John"), createGladiator("Mary"))
  val map = system.actorOf(MapActor.props(world, gladiators("John"), gladiators("Mary")))

  var exit = false

  val FindRegex = "find (\\w+)".r
  val UpRegex = "up (\\w+)".r
  val DownRegex = "down (\\w+)".r
  val LeftRegex = "left (\\w+)".r
  val RightRegex = "right (\\w+)".r

  Thread.sleep(500)

  while (!exit) {
    val line = StdIn.readLine("Command : ")

    line match {
      case FindRegex(name) => {
        val future = map ? GetGladiatorCoordinates(gladiators(name))
        val coord = future.waitOnResult[Coordinate]()
        println(s"${coord.x}, ${coord.y}")
      }
      case UpRegex(name) => map ! MoveUpMessage(gladiators(name))
      case DownRegex(name) => map ! MoveDownMessage(gladiators(name))
      case LeftRegex(name) => map ! MoveLeftMessage(gladiators(name))
      case RightRegex(name) => map ! MoveRightMessage(gladiators(name))
      case "exit" => exit = true
      case x => println(s"Unknown Command : ${x}")
    }

    Thread.sleep(300)
  }

  system.shutdown()
  system.awaitTermination(10.seconds)

  println("Exit System")

}
