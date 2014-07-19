package util

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.language.implicitConversions

class FutureHelper[T](future : Future[T]) {

  def waitOnResult[X](atMost: Duration = 5.seconds) : X = {
    Await.result(future, atMost).asInstanceOf[X]
  }

}

object FutureHelper {

  implicit def convertFutureToFutureHelper[T](future: Future[T]) : FutureHelper[T] = new FutureHelper[T](future)

}
