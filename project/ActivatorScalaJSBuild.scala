import sbt._
import Keys._

import scala.scalajs.sbtplugin.ScalaJSPlugin
import ScalaJSPlugin._
import ScalaJSKeys._

import scala.scalajs.tools.classpath._

import javax.servlet.http._;
import org.eclipse.jetty.server._
import org.eclipse.jetty.server.handler._
import org.eclipse.jetty.util.resource.Resource;

import java.io.File

object ActivatorScalaJSBuild extends Build {

  lazy val activatorScalaJSSettings = (
    scalaJSSettings ++
    inConfig(Compile)(activatorRunSettings)
  )

  val activatorRunSettings = Seq(
    run <<= Def.inputTask {
      val cp = fastOptJS.value
      sendRunCommand(cp,
        mainClass.value.getOrElse(sys.error("No main class found")),
        crossTarget.value)
    },

    runMain <<= {
      // Implicits for parsing
      import sbinary.DefaultProtocol.StringFormat
      import Cache.seqFormat

      val parser = Defaults.loadForParser(discoveredMainClasses)((s, names) =>
        Defaults.runMainParser(s, names getOrElse Nil))

      Def.inputTask {
        val mainClass = parser.parsed._1
        val cp = fastOptJS.value
        sendRunCommand(cp, mainClass, crossTarget.value)
      }
    }
  )

  private[this] var runId: Int = 1
  def sendRunCommand(cp: CompleteCIClasspath,
      mainClass: String, targetDir: File) = {
    val server = startServer(targetDir)
    println(s"Launching Scala.js application: $mainClass (run $runId)")
    runId += 1
    server.join()
  }

  private def startServer(targetDir: File): Server = {
    val server = new Server(8889)

    val resourceHandler = new CrossDomainResourceHandler();
    resourceHandler.setResourceBase(targetDir.getAbsolutePath)

    val handlers = new HandlerList();
    handlers.setHandlers(Array(
      new ShutdownHandler(server),
      resourceHandler,
      new DefaultHandler()
    ));
    server.setHandler(handlers);

    server.start()
    server
  }

  class CrossDomainResourceHandler extends ResourceHandler {
    override protected def doResponseHeaders(
      response: HttpServletResponse, resource: Resource, mimeType: String): Unit = {
      super.doResponseHeaders(response, resource, mimeType)
      response.setHeader("Access-Control-Allow-Origin", "*")
    }
  }

  class ShutdownHandler(server: Server) extends AbstractHandler {
    def handle(target: String, baseRequest: Request,
      request: HttpServletRequest, response: HttpServletResponse): Unit = {
      if (target == "/done") {
        response.setStatus(HttpServletResponse.SC_OK)
        response.setHeader("Access-Control-Allow-Origin", "*")
        baseRequest.setHandled(true)
        new Thread {
          override def run(): Unit = {
            Thread.sleep(1000L)
            server.stop()
          }
        }.start()
      }
    }
  }

}
