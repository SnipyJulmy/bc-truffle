import com.oracle.truffle.api.nodes.RootNode
import com.oracle.truffle.api.source.Source
import node._

import scala.util.parsing.combinator.{PackratParsers, RegexParsers}

object BCParser {
  def parse(language: BCLanguage, source: Source): RootNode = ???
}

class BCParser(source: Source) extends RegexParsers with PackratParsers {

  lazy val statement: PackratParser[BcStatementNode] = {
    scale <~ EOS
  }

  lazy val scale: PackratParser[ScaleNode] = "scale" ~> integer ^^ { value => new ScaleNode(value) }

  lazy val number: PackratParser[BcNumberNode] = "([1-9][0-9]*)|([0-9]+\\.[0-9]+?)".r ^^ { value => new BcNumberNode(value.toDouble) }

  // TODO : lazy val string: PackratParser[BcStringNode] = ???

  lazy val integer: PackratParser[Int] = "[0-9]+".r ^^ { str => str.toInt }


  lazy val EOS: PackratParser[String] = sc | nl
  lazy val nl = "\n"
  lazy val lp = "("
  lazy val rp = ")"
  lazy val lb = "{"
  lazy val rb = "}"
  lazy val sc = ";"

}
