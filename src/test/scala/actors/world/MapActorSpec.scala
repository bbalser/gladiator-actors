package actors.world

import actors.characters.GladiatorActor
import actors.world.MapActor._
import akka.actor.{Actor, ActorSystem}
import akka.pattern.ask
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import akka.util.Timeout
import battle.GameBoard.Coordinate
import battle.Gladiator
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import util.FutureHelper._

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

  implicit def convertTestActorRefToMapActorHelper(map: TestActorRef[Nothing]) = map.underlyingActor.asInstanceOf[MapActor]

  "A Map Actor" should "contain a 10x10 grid of coordinates" in {
    val map = createMap()
    map.board.width should be (10)
    map.board.height should be (10)
  }

  it should "add gladiator actor to start position 1 when Add Gladiator Message is received" in {
    val map = createMap()
    val gladiator1 = map.underlyingActor.asInstanceOf[MapActor].gladiatorActor1
    val gladiator2 = map.underlyingActor.asInstanceOf[MapActor].gladiatorActor2
    map.underlyingActor.asInstanceOf[MapActor].board.get(2,3).get should be (gladiator1)
    map.underlyingActor.asInstanceOf[MapActor].board.get(8, 7).get should be (gladiator2)
    assertMapChangeEvent(1, map)
  }

  it should "return current coordinates when asked" in {
    val map = createMap()
    val gladiator = map.underlyingActor.asInstanceOf[MapActor].gladiatorActor1

    val future = map ? GetGladiatorCoordinates(gladiator)
    future.waitOnResult[Coordinate]() should be (Coordinate(2, 3))
  }

  it should "move gladiator to 1 more on y axis when given the down direction" in {
    val map = createMap()

    map ! MoveDownMessage(map.gladiatorActor1)

    map.board.find(map.gladiatorActor1) should be (Coordinate(2, 4))
    assertMapChangeEvent(2, map)
  }

  it should "move gladiator to 1 less on x axis when given the left direction" in {
    val map = createMap()

    map ! MoveLeftMessage(map.gladiatorActor1)

    map.board.find(map.gladiatorActor1) should be (Coordinate(1, 3))
    assertMapChangeEvent(2, map)
  }

  it should "move gladiator to 1 more on x axis when given the right direction" in {
    val map = createMap()

    map ! MoveRightMessage(map.gladiatorActor1)

    map.board.find(map.gladiatorActor1) should be (Coordinate(3, 3))
    assertMapChangeEvent(2, map)
  }

  private def createMap() : TestActorRef[Nothing] = TestActorRef(MapActor.props(worldProbe.ref, createGladiator(), createGladiator()))

  private def createGladiator(): TestActorRef[Nothing] = TestActorRef(GladiatorActor.props(Gladiator("John")))

  private def assertMapChangeEvent[T <: Actor](num: Int, map: TestActorRef[T]): Unit = {
    val messages = worldProbe.receiveN(num, 5.seconds)
    messages.forall(x => x.asInstanceOf[MapChangedMessage].board == map.underlyingActor.asInstanceOf[MapActor].board) should be (true)
  }

}
