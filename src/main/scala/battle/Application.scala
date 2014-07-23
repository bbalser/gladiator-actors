package battle

import actors.characters.GladiatorActor.GladiatorChangedMessage
import actors.world.MapActor._
import actors.world.WorldActor
import actors.world.WorldActor.{AddGladiatorMessage, MoveGladiatorMessage, StartMessage}
import akka.actor.ActorSystem
import akka.util.Timeout
import battle.GameBoard.Coordinate
import messages.attacks.AttackMessage

import scala.concurrent.duration._
import scala.io.StdIn


object Application extends App {

  def mapChanged(event: MapChangedMessage): Unit = {
    drawBoard(event.board)
  }

  def gladiatorChanged(event: GladiatorChangedMessage): Unit = {
    showGladiator(event.gladiator)
  }

  def showGladiator(gladiator: Gladiator): Unit = {
    println(s"Name: ${gladiator.name} --> HitPoints: ${gladiator.hitpoints}")
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

  def attack(name: String, target: Coordinate) = {
    if (gladiators.contains(name)) {
      world ! AttackMessage(gladiators(name), target, 10)
    } else {
      println(s"Unknown Character : ${name}")
    }
  }

  implicit val timeout = Timeout(5.seconds)

  val system = ActorSystem("mySystem")
  val world = system.actorOf(WorldActor.props(mapChanged, gladiatorChanged))

  val gladiators = Map("John" -> Gladiator("John"), "Mary" -> Gladiator("Mary"))
  world ! AddGladiatorMessage(gladiators("John"))
  world ! AddGladiatorMessage(gladiators("Mary"))
  world ! StartMessage

  var exit = false

  val UpRegex = "up (\\w+)".r
  val DownRegex = "down (\\w+)".r
  val LeftRegex = "left (\\w+)".r
  val RightRegex = "right (\\w+)".r
  val AttackRegex = "(\\w+) attacks (\\d),(\\d)".r

  Thread.sleep(500)

  while (!exit) {
    val line = StdIn.readLine("Command : ")

    line match {
      case UpRegex(name) => move(name, Up)
      case DownRegex(name) => move(name, Down)
      case LeftRegex(name) => move(name, Left)
      case RightRegex(name) => move(name, Right)
      case AttackRegex(name, x, y) => attack(name, Coordinate(x.toInt, y.toInt))
      case "exit" => exit = true
      case x => println(s"Unknown Command : ${x}")
    }

    Thread.sleep(300)
  }

  system.shutdown()
  system.awaitTermination(10.seconds)

  println("Exit System")

}
