package actors.characters

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit}
import battle.GameBoard.Coordinate
import battle.Gladiator
import messages.attacks.AttackMessage
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, Matchers}

import scala.concurrent.duration._
import scala.language.implicitConversions

class GladiatorActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("GladiatorActorSpec"))

  override def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  implicit def convertTestActorRefToGladiatorActor(actor: TestActorRef[Nothing]) = actor.underlyingActor.asInstanceOf[GladiatorActor]

  "A Gladiator Actor" should "wrap a Gladiator" in {
    val gladiator = Gladiator("John")
    val actorRef = TestActorRef(Props(classOf[GladiatorActor], gladiator))
    actorRef.gladiator should be (gladiator)
  }

  it should "apply damage to wrapped gladiator when successful attack received" in {
    val defenderRef = TestActorRef(Props(classOf[GladiatorActor], Gladiator("John")))
    defenderRef ! AttackMessage(Gladiator("John"), Coordinate(1,1), 10)

    defenderRef.gladiator.hitpoints should be (4)
  }

}
