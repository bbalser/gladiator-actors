package actors.world

import actors.characters.GladiatorActor
import actors.world.MapActor.{MoveGladiatorMessage, AddGladiatorMessage}
import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import battle.Gladiator
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}
import scala.concurrent.duration._

class MapActorSpec(_system : ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("GladiatorActorSpec"))

  override def afterAll : Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  "A Map Actor" should "contain a 10x10 grid of coordinates" in {
    val actorRef = TestActorRef(MapActor.props)
    actorRef.underlyingActor.asInstanceOf[MapActor].board.width should be (10)
    actorRef.underlyingActor.asInstanceOf[MapActor].board.height should be (10)
  }

  it should "add gladiator actor to start position 1 when Add Gladiator Message is received" in {
    val gladiator = TestActorRef(GladiatorActor.props(Gladiator()))
    val map = TestActorRef(MapActor.props)
    map ! AddGladiatorMessage(gladiator)
    map.underlyingActor.asInstanceOf[MapActor].board.get(2,3).get should be (gladiator)
  }

  it should "add second gladiator actor to start position 2 when Add Gladiator message is received for 2nd Gladiator" in {
    val gladiator1 = TestActorRef(GladiatorActor.props(Gladiator()))
    val gladiator2 = TestActorRef(GladiatorActor.props(Gladiator()))
    val map = TestActorRef(MapActor.props)
    map ! AddGladiatorMessage(gladiator1)
    map ! AddGladiatorMessage(gladiator2)

    map.underlyingActor.asInstanceOf[MapActor].board.get(2, 3).get should be (gladiator1)
    map.underlyingActor.asInstanceOf[MapActor].board.get(8, 7).get should be (gladiator2)
  }

  it should "move gladiator to new position" in {
    val gladiator = TestActorRef(GladiatorActor.props(Gladiator()))
    val map = TestActorRef(MapActor.props)
    map ! AddGladiatorMessage(gladiator)

    map ! MoveGladiatorMessage(gladiator, 4, 4)

    map.underlyingActor.asInstanceOf[MapActor].board.get(4, 4) should be (Some(gladiator))
    map.underlyingActor.asInstanceOf[MapActor].board.get(2, 3) should be (None)


  }


}
