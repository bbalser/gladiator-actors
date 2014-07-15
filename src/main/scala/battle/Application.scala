package battle

import actors.characters.GladiatorActor
import actors.world.MapActor
import actors.world.MapActor.{MoveGladiatorMessage, GetGladiatorCoordinates, AddGladiatorMessage}
import akka.actor.{ActorRef, ActorSystem}
import akka.pattern.ask
import akka.util.Timeout
import battle.GameBoard.Coordinate

import scala.concurrent.Await
import scala.io.StdIn
import scala.concurrent.duration._

object Application extends App {

  implicit val timeout = Timeout(5.seconds)

  val system = ActorSystem("mySystem")
  val map = system.actorOf(MapActor.props)

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
        val coord = Await.result(future, 5.seconds).asInstanceOf[Coordinate]
        println(s"${coord.x}, ${coord.y}")
      }
      case MoveRegex(pos, x, y) => {
        map ! MoveGladiatorMessage(gladiators(pos.toInt), x.toInt, y.toInt)
      }
      case "exit" => exit = true
      case x => println(s"Unknown Command : ${x}")
    }

  }

  system.shutdown()
  system.awaitTermination(10.seconds)

  println("Exit System")

}
