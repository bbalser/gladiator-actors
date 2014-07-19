package actors.world

import actors.characters.GladiatorActor
import actors.world.MapActor.{AddGladiatorMessage, GetGladiatorCoordinates, MapChangedMessage, MoveGladiatorMessage}
import akka.actor.ActorSystem
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import akka.util.Timeout
import battle.GameBoard.Coordinate
import battle.Gladiator
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

class MapActorSpec(_system : ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll
  with BeforeAndAfterEach {

  def this() = this(ActorSystem("GladiatorActorSpec"))

  implicit val timeout = Timeout(5.seconds)

  var worldProbe: TestProbe = null


  override protected def beforeEach(): Unit = {
    worldProbe = new TestProbe(system)
  }

  override def afterAll() : Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  "A Map Actor" should "contain a 10x10 grid of coordinates" in {
    val actorRef = TestActorRef(MapActor.props(worldProbe.ref))
    actorRef.underlyingActor.asInstanceOf[MapActor].board.width should be (10)
    actorRef.underlyingActor.asInstanceOf[MapActor].board.height should be (10)
  }

  it should "add gladiator actor to start position 1 when Add Gladiator Message is received" in {
    val gladiator = TestActorRef(GladiatorActor.props(Gladiator("John")))
    val map = TestActorRef(MapActor.props(worldProbe.ref))
    map ! AddGladiatorMessage(gladiator)
    map.underlyingActor.asInstanceOf[MapActor].board.get(2,3).get should be (gladiator)
  }

  it should "add second gladiator actor to start position 2 when Add Gladiator message is received for 2nd Gladiator" in {
    val gladiator1 = TestActorRef(GladiatorActor.props(Gladiator("John")))
    val gladiator2 = TestActorRef(GladiatorActor.props(Gladiator("John")))
    val map = TestActorRef(MapActor.props(worldProbe.ref))
    map ! AddGladiatorMessage(gladiator1)
    map ! AddGladiatorMessage(gladiator2)

    map.underlyingActor.asInstanceOf[MapActor].board.get(2, 3).get should be (gladiator1)
    map.underlyingActor.asInstanceOf[MapActor].board.get(8, 7).get should be (gladiator2)
  }

  it should "move gladiator to new position" in {
    val gladiator = TestActorRef(GladiatorActor.props(Gladiator("John")))
    val map = TestActorRef(MapActor.props(worldProbe.ref))
    map ! AddGladiatorMessage(gladiator)

    map ! MoveGladiatorMessage(gladiator, 4, 4)

    map.underlyingActor.asInstanceOf[MapActor].board.get(4, 4) should be (Some(gladiator))
    map.underlyingActor.asInstanceOf[MapActor].board.get(2, 3) should be (None)
  }

  it should "return current coordinates when asked" in {
    val gladiator = TestActorRef(GladiatorActor.props(Gladiator("John")))
    val map = TestActorRef(MapActor.props(worldProbe.ref))

    map ! AddGladiatorMessage(gladiator)
    val future = map ? GetGladiatorCoordinates(gladiator)
    val coordinate = Await.result(future, 5.seconds).asInstanceOf[Coordinate]
    coordinate should be (Coordinate(2, 3))
  }

  it should "Send MapUpdateMessage to World Actor when gladiator added to map" in {
    val gladiator = TestActorRef(GladiatorActor.props(Gladiator("John")))
    val map = TestActorRef(MapActor.props(worldProbe.ref))

    map ! AddGladiatorMessage(gladiator)

    val message = worldProbe.receiveOne(5.seconds)
    message.asInstanceOf[MapChangedMessage].board should be (map.underlyingActor.asInstanceOf[MapActor].board)
  }

  it should "Send MapUpdateMessage to World Actor when gladiator moves on map" in {
    val gladiator = TestActorRef(GladiatorActor.props(Gladiator("John")))
    val map = TestActorRef(MapActor.props(worldProbe.ref))

    map ! AddGladiatorMessage(gladiator)
    map ! MoveGladiatorMessage(gladiator, 5, 5)

    val messages = worldProbe.receiveN(2, 5.seconds)
    messages.forall(x => x.asInstanceOf[MapChangedMessage].board == map.underlyingActor.asInstanceOf[MapActor].board) should be (true)
  }

}
