import scala.scalajs.js

object Hello extends js.JSApp {
  def main(): Unit = {
    js.Dynamic.global.jQuery("#scalajs-box").append("<p>Hello World</p>");
    js.Dynamic.global.jQuery("#scalajs-box").append(
      """<button onclick="stopScalaJSApp()" type="button">Close</button>""");
  }
}
