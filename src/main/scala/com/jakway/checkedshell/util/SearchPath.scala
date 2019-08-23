package com.jakway.checkedshell.util

import java.io.File
import java.util.Formatter
import java.util.regex.Pattern

object SearchPath {
  sealed trait Result {
    def toEither: Either[String, File]
  }

  case class ExecutableNotFound(toFind: String,
                                pathVar: String,
                                messages: Seq[String])
    extends Result {
    override def toEither: Either[String, File] = {
      val fmt = new Formatter()
      fmt.format("Could not find %s on PATH < %s >", toFind, pathVar)

      if(messages.nonEmpty) {
        fmt.format("Relevant messages follow:")

        messages.foreach { thisMsg =>
          fmt.format("\t%s%n", thisMsg)
        }
      }

      Left(fmt.toString)
    }
  }

  case class FoundExecutable(location: File)
    extends Result {
    override def toEither: Either[String, File] = Right(location)
  }


  def searchPathOption(executable: String): Option[File] =
    searchPathEither(executable).toOption

  def searchPathEither(executable: String): Either[String, File] =
    searchPath(executable).toEither

  /**
   * see https://stackoverflow.com/a/23539220/389943 for the inspiration behind this implementation
   * @param executable to find
   * @return
   */
  def searchPath(executable: String): Result = {
    val pathVar = System.getenv("PATH")
    val pathEntries = pathVar.split(Pattern.quote(File.pathSeparator))

    val empty: (Seq[String], Option[File]) = (Seq.empty, None)
    val searchRes = pathEntries.foldLeft(empty) {
      //stop when we've found it
      case (skip@(_, Some(_)), _) => skip

      case (acc@(comments, None), thisEntryString) => {
        val thisEntry: File = new File(thisEntryString)

        val toFind: File = new File(thisEntry, executable)

        def errMsg(s: String) = s"Found candidate file $toFind, but " + s

        if(toFind.exists()) {
          //file does exist so even if we can't run it
          //make sure we tell the caller about it

          //check read bit
          if(toFind.canRead) {
            //check execute bit
            if(toFind.canExecute) {
              (comments, Some(toFind))
            } else {
              (comments :+ errMsg("don't have read permission"), None)
            }
          } else {
            (comments :+ errMsg("don't have execute permission"), None)
          }
        } else {
          //doesn't exist, don't report anything special
          acc
        }
      }
    }

    searchRes._2 match {
      case Some(loc) => FoundExecutable(loc)
      case None => ExecutableNotFound(executable, pathVar, searchRes._1)
    }
  }
}
