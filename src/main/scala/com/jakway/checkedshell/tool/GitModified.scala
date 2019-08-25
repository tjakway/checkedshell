package com.jakway.checkedshell.tool

import java.io.File
import java.nio.file.{FileVisitOption, Files, Path}
import java.nio.file.attribute.BasicFileAttributes
import java.util.function.BiPredicate

import org.slf4j.{Logger, LoggerFactory}

class GitModified(val dir: File,
                  val maxSearchGitDirDepth: Option[Int]) {
  import GitModified._

  private val logger: Logger = LoggerFactory.getLogger(getClass)

  private def dirChecks(): Either[String, Unit] = {
    if(!dir.exists()) {
      Left(s"$dir does not exist")
    } else if(!dir.isDirectory) {
      Left(s"$dir is not a directory")
    } else if(!dir.canRead) {
      Left(s"$dir is not a directory")
    } else if(!dir.canExecute) {
      Left(s"Need execute permissions on $dir")
    } else {
      Right({})
    }
  }

  lazy val gitDirsPredicate: BiPredicate[Path, BasicFileAttributes] = new BiPredicate[Path, BasicFileAttributes] {
    override def test(path: Path, attrs: BasicFileAttributes): Boolean = {
      val res = path.toFile.getName == gitDirName &&
        attrs.isDirectory

      if(res && attrs.isOther) {
        logger.warn(s"Returning false for $path because attrs.isOther returned true")
        false
      } else {
        res
      }
    }
  }

  private def findGitDirs(): Set[Path] = {
    import scala.collection.JavaConverters
    val stream = Files.find(
      dir.toPath,
      maxSearchGitDirDepth.getOrElse(defaultMaxSearchDepth),
      gitDirsPredicate,
      FileVisitOption.FOLLOW_LINKS)

    JavaConverters
      .asScalaIterator(stream.iterator())
      .toSet
  }

  private def getChanges(gitDir: Path): Map[ChangeType, String] = {

  }

  def getResults(): Either[String, GitModified.Results] = {

  }

}

object GitModified {
  val gitDirName: String = ".git"
  val defaultMaxSearchDepth: Int = Int.MaxValue

  sealed trait ChangeType
  case object Modified extends ChangeType
  case object New extends ChangeType
  case class UnknownChangeType(porcelainSymbol: String) extends ChangeType

  object ChangeType {
    def parse(symbol: String): ChangeType = {
      val s = symbol.trim
      if(s == "??") {
        New
      } else if(s == "M") {
        Modified
      } else {
        UnknownChangeType(s)
      }
    }
  }

  class Results(val reportedResults: Map[File, Map[ChangeType, String]])

  object Results {
  }


  def main(args: Array[String]): Unit = {}

}
