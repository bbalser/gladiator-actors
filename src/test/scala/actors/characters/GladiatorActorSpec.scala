package actors.characters

import actors.characters.GladiatorActor.GladiatorChangedMessage
import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import battle.GameBoard.Coordinate
import battle.Gladiator
import messages.attacks.AttackMessage
import org.scalatest._

import scala.concurrent.duration._
import scala.language.implicitConversions

class GladiatorActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterEach
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("GladiatorActorSpec"))

  var parent: TestProbe = null
  override protected def beforeEach(): Unit = {
    parent = new TestProbe(system)
  }

  override def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  implicit def convertTestActorRefToGladiatorActor(actor: TestActorRef[Nothing]) = actor.underlyingActor.asInstanceOf[GladiatorActor]

  "A Gladiator Actor" should "wrap a Gladiator" in {
    val gladiator = Gladiator("John")
    val actorRef = TestActorRef(Props(classOf[GladiatorActor], gladiator), parent.ref, "GladiatorActor")
    actorRef.gladiator should be (gladiator)
  }

  it should "apply damage to wrapped gladiator when successful attack received" in {
    val defenderRef = TestActorRef(Props(classOf[GladiatorActor], Gladiator("John")), parent.ref, "GladiatorActor")
    defenderRef ! AttackMessage(Gladiator("John"), Coordinate(1,1), 10)

    defenderRef.gladiator.hitpoints should be (4)
  }

  it should "send GladiatorChangedMessage to parent when damage is done" in {
    val gladiator = Gladiator("John")
    val actorRef = TestActorRef(Props(classOf[GladiatorActor], gladiator), parent.ref, "GladiatorActor")

    actorRef ! AttackMessage(Gladiator("mary"), Coordinate(1,1), 10)

    val message = parent.receiveOne(5.seconds).asInstanceOf[GladiatorChangedMessage]
    message.gladiator should be (gladiator)
  }

  it should "not send GladiatorChangedMessage when attack fails" in {
    val gladiator = Gladiator("John")
    val actorRef = TestActorRef(Props(classOf[GladiatorActor], gladiator), parent.ref, "GladiatorActor")

    actorRef ! AttackMessage(Gladiator("mary"), Coordinate(1,1), 9)

    parent.expectNoMsg()
  }

}
