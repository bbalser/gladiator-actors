package actors.world

import actors.characters.GladiatorActor.GladiatorChangedMessage
import actors.world.MapActor.{MapChangedMessage, Up}
import actors.world.WorldActor.{AddGladiatorMessage, AddListener, MoveGladiatorMessage, StartMessage}
import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import akka.util.Timeout
import battle.GameBoard.Coordinate
import battle.Gladiator
import messages.attacks.AttackMessage
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration._
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

  override def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  "A World Actor" should "pass MapChangedMessage to all listeners" in {

    val probe = new TestProbe(system)
    val world = TestActorRef(WorldActor.props())

    world ! AddListener(probe.ref)

    val event = MapChangedMessage(null)
    world ! event

    probe.expectMsg(event)
  }

  it should "pass GladiatorChangedMessage to all listeners" in {
    val probe = new TestProbe(system)
    val world = TestActorRef(WorldActor.props())

    world ! AddListener(probe.ref)

    val event = GladiatorChangedMessage(null)
    world ! event

    probe.expectMsg(event)
  }

  it should "create Actor representing Gladiator on AddGladiatorMessage" in {
    val world = TestActorRef(WorldActor.props())
    val gladiator = Gladiator("John")

    world ! AddGladiatorMessage(gladiator)

    world.gladiators.size should be (1)
  }

  it should "start should create the map actor with gladiators on board" in {

    val world = TestActorRef(WorldActor.props())
    val gladiator1 = Gladiator("John")
    val gladiator2 = Gladiator("Mary")

    world ! AddGladiatorMessage(gladiator1)
    world ! AddGladiatorMessage(gladiator2)

    world ! StartMessage

    world.map should not be (null)
  }

  it should "Send Gladiator Move event to MapActor" in {

    val probe = new TestProbe(system)

    val world = TestActorRef(WorldActor.props())
    world ! AddListener(probe.ref)
    val gladiator = Gladiator("John")

    world ! AddGladiatorMessage(gladiator)
    world ! StartMessage

    val board = probe.receiveOne(5.seconds).asInstanceOf[MapChangedMessage].board

    board.move(gladiator, Coordinate(5,5))

    world ! MoveGladiatorMessage(gladiator, Up)

    awaitAssert({ board.find(gladiator) should be (Coordinate(5, 4)) }, 5.seconds)
  }

  it should "when attack message is received it will consult map actor and forward message to any actor at that position" in {

    val probe = new TestProbe(system)

    val world = TestActorRef(WorldActor.props())
    world ! AddListener(probe.ref)
    val gladiator1 = Gladiator("John")
    val gladiator2 = Gladiator("Mary")

    world ! AddGladiatorMessage(gladiator1)
    world ! AddGladiatorMessage(gladiator2)
    world ! StartMessage

    val board = probe.receiveOne(5.seconds).asInstanceOf[MapChangedMessage].board
    val targetCoordinate = board.find(gladiator2)

    val attack = AttackMessage(gladiator1, targetCoordinate, 10)

    world ! attack

    val gladiator = probe.receiveOne(5.seconds).asInstanceOf[GladiatorChangedMessage].gladiator
    gladiator.hitpoints should be (4)
  }

}
