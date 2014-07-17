package actors.world

import actors.world.MapActor.MapChangedMessage
import akka.actor.ActorSystem
import akka.testkit.{TestActorRef, TestKit, ImplicitSender}
import akka.util.Timeout
import org.scalatest.{Matchers, FlatSpecLike, BeforeAndAfterAll}
import scala.concurrent.Promise
import scala.concurrent.duration._
import util.FutureHelper._

class WorldActorSpec(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with Matchers
  with FlatSpecLike
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("GladiatorActorSpec"))

  implicit val timeout = new Timeout(5.seconds)

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

}
