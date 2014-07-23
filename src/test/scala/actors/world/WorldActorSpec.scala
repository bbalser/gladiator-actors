package actors.world

import actors.characters.GladiatorActor.GladiatorChangedMessage
import actors.world.MapActor.{GetCoordinate, MapChangedMessage, Up}
import actors.world.WorldActor.{AddGladiatorMessage, MoveGladiatorMessage, StartMessage}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import akka.util.Timeout
import battle.GameBoard.Coordinate
import battle.{GameBoard, Gladiator}
import messages.attacks.AttackMessage
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import util.FutureHelper._

import scala.concurrent.Promise
import scala.concurrent.duration._
import scala.language.implicitConversions
import akka.pattern.ask

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

    def world = TestActorRef(WorldActor.props(captureEvent, null))
    val event = MapChangedMessage(null)
    world ! event

    promise.future.waitOnResult[MapChangedMessage]() should be (event)
  }
  
  it should "pass GladiatorChangedMessage to gladiatorChangedFunction" in {
    val promise = Promise[GladiatorChangedMessage]
    
    def captureEvent(gladiatorChanged: GladiatorChangedMessage): Unit = {
      promise.success(gladiatorChanged)
    }
    
    def world = TestActorRef(WorldActor.props(null, captureEvent))
    val event = GladiatorChangedMessage(null)
    world ! event
    
    promise.future.waitOnResult[GladiatorChangedMessage]() should be (event)
    
    
  }

  it should "create Actor representing Gladiator on AddGladiatorMessage" in {
    val world = TestActorRef(WorldActor.props(mapEvent, gladiatorEvent))
    val gladiator = Gladiator("John")

    world ! AddGladiatorMessage(gladiator)

    world.gladiators.size should be (1)
  }

  it should "start should create the map actor with gladiators on board" in {

    val world = TestActorRef(WorldActor.props(mapEvent, gladiatorEvent))
    val gladiator1 = Gladiator("John")
    val gladiator2 = Gladiator("Mary")

    world ! AddGladiatorMessage(gladiator1)
    world ! AddGladiatorMessage(gladiator2)

    world ! StartMessage

    world.map should not be (null)
  }

  it should "Send Gladiator Move event to MapActor" in {

    val promise = Promise[MapChangedMessage]
    def captureBoard(event : MapChangedMessage): Unit = {
      if (!promise.isCompleted) promise.success(event)
    }

    val world = TestActorRef(WorldActor.props(captureBoard, gladiatorEvent))
    val gladiator = Gladiator("John")

    world ! AddGladiatorMessage(gladiator)
    world ! StartMessage

    val board = promise.future.waitOnResult[MapChangedMessage]().board
    board.move(gladiator, Coordinate(5,5))

    world ! MoveGladiatorMessage(gladiator, Up)

    awaitAssert({ board.find(gladiator) should be (Coordinate(5, 4)) }, 5.seconds)
  }

  it should "when attack message is received it will consult map actor and forward message to any actor at that position" in {
    val gladiatorPromise = Promise[Gladiator]
    def captureGladiator(event: GladiatorChangedMessage): Unit = {
      gladiatorPromise.success(event.gladiator)
    }

    val boardPromise = Promise[GameBoard]
    def captureBoard(event: MapChangedMessage): Unit = {
      boardPromise.success(event.board)
    }

    val world = TestActorRef(WorldActor.props(captureBoard, captureGladiator))
    val gladiator1 = Gladiator("John")
    val gladiator2 = Gladiator("Mary")

    world ! AddGladiatorMessage(gladiator1)
    world ! AddGladiatorMessage(gladiator2)
    world ! StartMessage

    val targetCoordinate = boardPromise.future.waitOnResult[GameBoard]().find(gladiator2)

    val attack = AttackMessage(gladiator1, targetCoordinate, 10)

    world ! attack

    gladiatorPromise.future.waitOnResult[Gladiator]().hitpoints should be (4)
  }

  def mapEvent(event: MapChangedMessage): Unit = Unit
  def gladiatorEvent(event: GladiatorChangedMessage): Unit = Unit
  

}
