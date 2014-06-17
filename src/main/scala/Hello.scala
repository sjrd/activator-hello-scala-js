import scala.scalajs.js

import scala.scalajs.js.annotation.JSExport

object Hello extends js.JSApp {

  var int: js.Any = _

  def main(): Unit = {
    js.Dynamic.global.jQuery("#scalajs-box").append("<p>Hello World</p>");
    int = js.Dynamic.global.setInterval(tick _, 1000)
  }

  @JSExport
  def teardown(): Unit = {
    js.Dynamic.global.clearInterval(int)
  }

  def tick(): Unit = {
    js.Dynamic.global.jQuery("#scalajs-box").append("<p style='color: green'>Tick</p>");
  }
}
