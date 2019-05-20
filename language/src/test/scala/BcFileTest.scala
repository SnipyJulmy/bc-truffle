import java.io.{File, FileOutputStream}

import org.graalvm.polyglot.{Context, Source}
import org.scalatest.FlatSpec

class BcFileTest extends FlatSpec {

  private val bcStdSuffix = ".bc"
  private val bcTruffleSuffix = ".bcout"

  private val resourceDir = new File(getClass.getResource("/bc").getPath)
  private val bcFiles = resourceDir.listFiles().filter(f => f.isFile && f.getName.endsWith(bcStdSuffix))

  behavior of "bc-truffle"

  for {
    bcFile <- bcFiles
    bcFileName = bcFile.getName
    name = bcFileName.substring(0, bcFileName.length - bcStdSuffix.length)
    outputFileName = s"$name$bcTruffleSuffix"
  } {
    it should s"generate the same output that standard bc for file $bcFileName" in {
      // generate the output file
      val outputFile = s"${resourceDir.getAbsolutePath}/$outputFileName"
      val context = Context.newBuilder("bc")
        .in(System.in)
        .out(new FileOutputStream(outputFile))
        .build()
      val bcSource = scala.io.Source.fromFile(bcFile)
      val bcSourceStr = bcSource.getLines().mkString("\n")
      bcSource.close()

      val source = Source.newBuilder("bc", bcSourceStr, name).build()
      context.eval(source)
    }
  }
}
