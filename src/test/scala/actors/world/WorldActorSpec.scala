package actors.world

import actors.world.MapActor.{Up, MoveMessage, GetCoordinate, MapChangedMessage}
import actors.world.WorldActor.{MoveGladiatorMessage, StartMessage, AddGladiatorMessage}
import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit, ImplicitSender}
import akka.util.Timeout
import battle.GameBoard.Coordinate
import battle.Gladiator
import org.scalatest.{Matchers, FlatSpecLike, BeforeAndAfterAll}
import scala.concurrent.Promise
import scala.concurrent.duration._
import util.FutureHelper._
import akka.pattern.ask
import scala.language.implicitConversions

class WorldActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("WorldActorSpec"))

  implicit val timeout = new Timeout(5.seconds)
  implicit def convertActorRefToWorldActor(ref : TestActorRef[Nothing]) = ref.underlyingActor.asInstanceOf[WorldActor]

  "A World Actor" should "Pass MapChangeMessage to mapChangedFunction" in {

    val promise = Promise[MapChangedMessage]

    def captureEvent(mapChangedMessage : MapChangedMessage): Unit = {
      promise.success(mapChangedMessage)
    }

    def world = TestActorRef(WorldActor.props(captureEvent))
    val event = MapChangedMessage(null)
    world ! event

    promise.future.waitOnResult[MapChangedMessage]() should be (event)
  }

  it should "create Actor representing Gladiator on AddGladiatorMessage" in {
    val world = TestActorRef(WorldActor.props(changeEvent))
    val gladiator = Gladiator("John")

    world ! AddGladiatorMessage(gladiator)

    world.gladiators.size should be (1)
  }

  it should "start should create the map actor with gladiators on board" in {

    val world = TestActorRef(WorldActor.props(changeEvent))
    val gladiator1 = Gladiator("John")
    val gladiator2 = Gladiator("Mary")

    world ! AddGladiatorMessage(gladiator1)
    world ! AddGladiatorMessage(gladiator2)

    world ! new StartMessage

    world.map should not be (null)
  }

  it should "Send Gladiator Move event to MapActor" in {

    val promise = Promise[MapChangedMessage]
    def captureBoard(event : MapChangedMessage): Unit = {
      if (!promise.isCompleted) promise.success(event)
    }

    val world = TestActorRef(WorldActor.props(captureBoard))
    val gladiator = Gladiator("John")

    world ! AddGladiatorMessage(gladiator)
    world ! new StartMessage

    val board = promise.future.waitOnResult[MapChangedMessage]().board
    board.move(gladiator, Coordinate(5,5))

    world ! MoveGladiatorMessage(gladiator, Up)

    awaitAssert({ board.find(gladiator) should be (Coordinate(5, 4)) }, 5.seconds)
  }


  def changeEvent(event: MapChangedMessage): Unit = Unit

}
