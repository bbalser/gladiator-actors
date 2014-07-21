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
import util.FutureHelper._

import scala.concurrent.duration._
import scala.language.implicitConversions

class MapActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll
  with BeforeAndAfterEach {

  def this() = this(ActorSystem("MapActorSpec"))

  implicit val timeout = Timeout(5.seconds)

  var parent: TestProbe = null

  override protected def beforeEach(): Unit = {
    parent = new TestProbe(system)
  }

  override def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  implicit def convertTestActorRefToMapActorHelper(map: TestActorRef[Nothing]) = map.underlyingActor.asInstanceOf[MapActor]

  "A Map Actor" should "contain a 10x10 grid of coordinates" in {
    val map = TestActorRef(MapActor.props(Nil), parent.ref, "Child Actor")
    map.board.width should be(10)
    map.board.height should be(10)
  }

  it should "add gladiator to random spots on board" in {
    val gladiators = createGladiators
    val map = createMap(gladiators)

    map.board.find(gladiators(0)) should not be (null)
    map.board.find(gladiators(1)) should not be (null)

    assertMapChangeEvent(1, map)
  }

  it should "return current coordinates when asked" in {
    val map = createMap(createGladiators)
    val gladiator = map.entities.head

    val future = map ? GetCoordinate(gladiator)
    future.waitOnResult[Coordinate]() should not be (null)
  }

  it should "move gladiator to 1 more on y axis when given the down direction" in {
    val map = createMap(createGladiators)
    val gladiator = map.entities.head

    map.board.move(gladiator, Coordinate(5, 5))

    map ! MoveMessage(gladiator, Down)
    val future = map ? GetCoordinate(gladiator)

    future.waitOnResult[Coordinate]() should be(Coordinate(5, 6))
    assertMapChangeEvent(2, map)
  }

  it should "move gladiator to 1 less on y axis when given the up direction" in {
    val map = createMap(createGladiators)
    val gladiator = map.entities.head

    map.board.move(gladiator, Coordinate(5, 5))

    map ! MoveMessage(gladiator, Up)
    val future = map ? GetCoordinate(gladiator)

    future.waitOnResult[Coordinate]() should be(Coordinate(5, 4))
    assertMapChangeEvent(2, map)
  }

  it should "move gladiator to 1 less on x axis when given the left direction" in {
    val map = createMap(createGladiators)
    val gladiator = map.entities.head
    map.board.move(gladiator, Coordinate(5, 5))

    map ! MoveMessage(gladiator, Left)
    val future = map ? GetCoordinate(gladiator)

    future.waitOnResult[Coordinate]() should be(Coordinate(4, 5))
    assertMapChangeEvent(2, map)
  }

  it should "move gladiator to 1 more on x axis when given the right direction" in {
    val map = createMap(createGladiators)
    val gladiator = map.entities.head
    map.board.move(gladiator, Coordinate(5, 5))

    map ! MoveMessage(gladiator, Right)
    val future = map ? GetCoordinate(gladiator)

    future.waitOnResult[Coordinate]() should be(Coordinate(6, 5))
    assertMapChangeEvent(2, map)
  }


  private def createMap(gladiators: List[TestActorRef[Nothing]]): TestActorRef[Nothing] = {
    TestActorRef(MapActor.props(gladiators), parent.ref, "MapActor")
  }

  private def createGladiators: List[TestActorRef[Nothing]] = {
    List(TestActorRef(GladiatorActor.props(Gladiator("John"))), TestActorRef(GladiatorActor.props(Gladiator("Mary"))))
  }

  private def assertMapChangeEvent[T <: Actor](num: Int, map: TestActorRef[T]): Unit = {
    val messages = parent.receiveN(num, 5.seconds)
    messages.forall(x => x.asInstanceOf[MapChangedMessage].board == map.underlyingActor.asInstanceOf[MapActor].board) should be(true)
  }

}
