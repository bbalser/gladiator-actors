package battle

import actors.characters.GladiatorActor
import actors.world.WorldActor.{MoveGladiatorMessage, StartMessage, AddGladiatorMessage}
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
        internalList = internalList :+ (board.get(Coordinate(x, y)) match {
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

  def move(name: String, direction: Direction): Unit = {
    if (gladiators.contains(name)) {
      world ! MoveGladiatorMessage(gladiators(name), direction)
    } else {
      println(s"Unknown Character : ${name}")
    }
  }

  implicit val timeout = Timeout(5.seconds)

  val system = ActorSystem("mySystem")
  val world = system.actorOf(WorldActor.props(mapChanged))

  val gladiators = Map("John" -> Gladiator("John"), "Mary" -> Gladiator("Mary"))
  world ! AddGladiatorMessage(gladiators("John"))
  world ! AddGladiatorMessage(gladiators("Mary"))
  world ! new StartMessage

  var exit = false

  val UpRegex = "up (\\w+)".r
  val DownRegex = "down (\\w+)".r
  val LeftRegex = "left (\\w+)".r
  val RightRegex = "right (\\w+)".r

  Thread.sleep(500)

  while (!exit) {
    val line = StdIn.readLine("Command : ")

    line match {
      case UpRegex(name) => move(name, Up)
      case DownRegex(name) => move(name, Down)
      case LeftRegex(name) => move(name, Left)
      case RightRegex(name) => move(name, Right)
      case "exit" => exit = true
      case x => println(s"Unknown Command : ${x}")
    }

    Thread.sleep(300)
  }

  system.shutdown()
  system.awaitTermination(10.seconds)

  println("Exit System")

}
