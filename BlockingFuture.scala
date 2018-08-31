import scala.concurrent.Await
import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinWorkerThread

object BlockingFuture extends App {
  def newForkJoinPool(prefix: String, maxThreadNumber: Int): ForkJoinPool = {
    // Custom factory to set thread names
    val factory = new ForkJoinPool.ForkJoinWorkerThreadFactory {
      override def newThread(pool: ForkJoinPool) =
        new ForkJoinWorkerThread(pool) {
          setName(prefix + "-" + super.getName)
        }
    }
    new ForkJoinPool(maxThreadNumber, factory,
      null, // handler
      false // asyncMode
    )
  }

  val forkJoinPool = newForkJoinPool("test-parmap", 20)

  val executionContext = ExecutionContext.fromExecutorService(forkJoinPool)
  // val executionContext = ExecutionContext.fromExecutorService(forkJoinPool, { e => throw e })
  implicit val ec: ExecutionContext = executionContext
  executionContext.shutdown()
  val future = Future {
    Thread.sleep(5000)
    println(1)
  }
  Await.result(future, Duration.Inf)
}
