import java.io.{File, FileOutputStream}

import org.graalvm.polyglot.{Context, Source}
import org.scalatest.{FlatSpec, Matchers}
import org.scalatestplus.scalacheck.Checkers

trait BcTestSpec extends FlatSpec with Checkers with Matchers {

  protected val outputDir = new File("target")
  protected val outputFileName = "test.out"
  lazy val outputFile = s"${outputDir.getAbsolutePath}/$outputFileName"

  protected val context: Context = {
    if (!outputDir.exists())
      if (!outputDir.mkdirs())
        throw new Exception(s"can't create $outputDir")

    Context.newBuilder("bc")
      .in(System.in)
      .out(new FileOutputStream(outputFile))
      .build()
  }

  protected def mkSource(source: String, name: String = "???"): Source =
    Source.newBuilder("bc", source, name).build()

  protected def B(value: Double): BigDecimal = BigDecimal(value)

  protected def B(value: Int): BigDecimal = BigDecimal(value)
}
