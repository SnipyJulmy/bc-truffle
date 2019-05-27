import java.io.{File, FileOutputStream}

import org.graalvm.polyglot.{Context, Source}
import org.scalatest.{FlatSpec, Matchers}

import scala.language.{postfixOps, reflectiveCalls}

class BcFileTest extends FlatSpec with Matchers {

  def using[A, B <: {def close() : Unit}](closeable: B)(f: B => A): A =
    try {
      f(closeable)
    } finally {
      closeable.close()
    }

  private val bcStdSuffix = ".bc"
  private val bcTruffleSuffix = ".bcout"
  private val outSuffix = ".out"

  private val resourceDir = new File(getClass.getResource("/bc").getPath)
  private val bcFiles = resourceDir.listFiles().filter(f => f.isFile && f.getName.endsWith(bcStdSuffix))

  behavior of "bc-truffle"

  for {
    bcFile <- bcFiles
    bcFilename = bcFile.getName
    name = bcFilename.substring(0, bcFilename.length - bcStdSuffix.length)
    bcTruffleOutputFilename = s"$name$bcTruffleSuffix"
    outputFilename = s"$name$outSuffix"
  } {
    it should s"generate the same output that standard bc for file $bcFilename" in {
      // generate the output file
      val outputFile = s"${resourceDir.getAbsolutePath}/$bcTruffleOutputFilename"
      val context = Context.newBuilder("bc")
        .in(System.in)
        .out(new FileOutputStream(outputFile))
        .build()
      val bcSource = scala.io.Source.fromFile(bcFile)
      val bcSourceStr = bcSource.getLines().mkString("\n")
      bcSource.close()

      val source = Source.newBuilder("bc", bcSourceStr, name).build()
      context.eval(source)

      val bcTruffleOut: String = using(scala.io.Source.fromFile(s"${resourceDir.getAbsolutePath}/$bcTruffleOutputFilename")) {
        _.getLines()
          .mkString("") // remove \n to read properly potential test assertion error
          .replaceAll("""\\""","") // normal bc add \ whem the line is to long
      }

      val bcOut: String = using(scala.io.Source.fromFile(s"${resourceDir.getAbsolutePath}/$outputFilename")) {
        _.getLines()
          .mkString("") // remove \n to read properly potential test assertion error
          .replaceAll("""\\""","") // normal bc add \ whem the line is to long
      }

      bcTruffleOut shouldBe bcOut
    }
  }
}
