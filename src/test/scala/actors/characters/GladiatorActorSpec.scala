package actors.characters

import akka.actor.{Props, ActorSystem}
import akka.testkit.{TestActorRef, ImplicitSender, TestKit}
import battle.Gladiator
import messages.attacks.AttackMessage
import org.scalatest.{FlatSpecLike, Matchers, BeforeAndAfterAll}
import scala.concurrent.duration._

class GladiatorActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("GladiatorActorSpec"))

  override def afterAll: Unit = {
    system.shutdown()
    system.awaitTermination(10.seconds)
  }

  "A Gladiator Actor" should "wrap a Gladiator" in {
    val gladiator = Gladiator()
    val actorRef = TestActorRef(Props(classOf[GladiatorActor], gladiator))
    actorRef.underlyingActor.asInstanceOf[GladiatorActor].gladiator should be (gladiator)
  }

  it should "apply damage to wrapped gladiator when successful attack received" in {
    val defenderRef = TestActorRef(Props(classOf[GladiatorActor], Gladiator()))
    defenderRef ! AttackMessage(Gladiator(), 10)

    defenderRef.underlyingActor.asInstanceOf[GladiatorActor].gladiator.hitpoints should be (4)
  }




}
