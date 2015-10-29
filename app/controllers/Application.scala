package controllers

import java.lang.management.ManagementFactory

import com.ning.http.client.AsyncHttpClientConfig
import play.api._
import play.api.libs.ws.ning.NingWSClient
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class Application extends Controller {
  println(s"CREATING APPLICATION $this")

  val leakyWsConfig = new AsyncHttpClientConfig.Builder()
    .setAllowPoolingConnections(true)
    .setAllowPoolingSslConnections(true)
    .setMaxRequestRetry(0)
    .setConnectionTTL(4000)
    .setReadTimeout(30000)
    .setRequestTimeout(30000)
    .setFollowRedirect(false)

  val client = new NingWSClient(leakyWsConfig.build())

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

  def leak = Action.async {
    client.url("https://playframework.com/documentation/2.4.x/Migration24").get().map { unused =>
      Ok("Connection leak")
    }
  }

  def pid = Action {
    val pid = ManagementFactory.getRuntimeMXBean().getName()
    Ok(s"Pid is: $pid")
  }

}
