package com.ubirch

import com.ubirch.controllers.CertificationController
import com.ubirch.services.execution.HttpClientProvider
import io.prometheus.client.CollectorRegistry
import monix.eval.Task
import monix.execution.Scheduler
import monix.reactive.Observable
import org.scalatest.OptionValues
import org.scalatest.concurrent.ScalaFutures
import org.scalatra.test.scalatest.ScalatraWordSpec
import sttp.client3.testing.RecordingSttpBackend
import sttp.client3.{ Request, Response }

import java.util.concurrent.Executors
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContext, ExecutionContextExecutor, Future }
import scala.util.Try

/**
  * Represents base for a convenient test
  */
trait TestBase extends ScalatraWordSpec with ScalaFutures with OptionValues with Awaits with ExecutionContextsTests {

  override def beforeAll(): Unit = {}
  override def afterAll(): Unit = {}

  def getRecordingBackend(injectorHelper: InjectorHelper): RecordingSttpBackend[Future, Any] = {
    val stubBackend = injectorHelper.get[HttpClientProvider]
    stubBackend.backend.asInstanceOf[RecordingSttpBackend[Future, Any]]
  }

  def getAllSendRequests(injectorHelper: InjectorHelper): List[Request[_, _]] = {
    getRecordingBackend(injectorHelper).allInteractions.map(_._1)
  }

  def getAllReceivedResponses(injectorHelper: InjectorHelper): List[Try[Response[_]]] = {
    getRecordingBackend(injectorHelper).allInteractions.map(_._2)
  }

  def testEnv[A](stubBackend: StubBackend = new StubResource())(testCode: InjectorHelper => A): A = {
    try {
      start()
      lazy val injector = new InjectorHelperImpl(stubBackend)
      CollectorRegistry.defaultRegistry.clear()
      val controller = injector.get[CertificationController]
      addServlet(controller, "")
      testCode(injector)
    } finally {
      stop()
    }
  }
}

trait ExecutionContextsTests {
  implicit lazy val ec: ExecutionContextExecutor = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))
  implicit lazy val scheduler: Scheduler = monix.execution.Scheduler(ec)
}

trait Awaits {

  def await[T](future: Future[T]): T = await(future, Duration.Inf)

  def await[T](future: Future[T], atMost: Duration): T = Await.result(future, atMost)

  def await[T](observable: Observable[T], atMost: Duration)(implicit scheduler: Scheduler): Seq[T] = {
    val future = observable.foldLeftL(Nil: Seq[T])((a, b) => a ++ Seq(b)).runToFuture
    Await.result(future, atMost)
  }

  def await[T](task: Task[T], atMost: Duration)(implicit scheduler: Scheduler): T = {
    val future = task.runToFuture
    Await.result(future, atMost)
  }

}
