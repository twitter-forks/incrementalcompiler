package sbt
package internal
package inc

import java.io.File

/**
 * Represents all configuration options for the incremental compiler itself and
 * not the underlying Java/Scala compiler.
 *
 * NOTE: This class used to be a case class but due to problems with retaining
 * binary compatibility while new fields are added it has been expanded to a
 * regular class. All compiler-generated methods for a case class has been
 * defined explicitly.
 */
final class IncOptions(
  /* After which step include whole transitive closure of invalidated source files. */
  val transitiveStep: Int,
  /*
   * What's the fraction of invalidated source files when we switch to recompiling
   * all files and giving up incremental compilation altogether. That's useful in
   * cases when probability that we end up recompiling most of source files but
   * in multiple steps is high. Multi-step incremental recompilation is slower
   * than recompiling everything in one step.
   */
  val recompileAllFraction: Double,
  /* Print very detailed information about relations, such as dependencies between source files. */
  val relationsDebug: Boolean,
  /*
   * Enable tools for debugging API changes. At the moment this option is unused but in the
   * future it will enable for example:
   *   - disabling API hashing and API minimization (potentially very memory consuming)
   *   - diffing textual API representation which helps understanding what kind of changes
   *     to APIs are visible to the incremental compiler
   */
  val apiDebug: Boolean,
  /*
   * Controls context size (in lines) displayed when diffs are produced for textual API
   * representation.
   *
   * This option is used only when `apiDebug == true`.
   */
  val apiDiffContextSize: Int,
  /*
   * The directory where we dump textual representation of APIs. This method might be called
   * only if apiDebug returns true. This is unused option at the moment as the needed functionality
   * is not implemented yet.
   */
  val apiDumpDirectory: Option[java.io.File],
  /* Creates a new ClassfileManager that will handle class file deletion and addition during a single incremental compilation run. */
  val newClassfileManager: () => ClassfileManager,
  /*
   * Determines whether incremental compiler should recompile all dependencies of a file
   * that contains a macro definition.
   */
  val recompileOnMacroDef: Boolean,
  /*
   * Determines whether incremental compiler uses the new algorithm known as name hashing.
   *
   * This flag is disabled by default so incremental compiler's behavior is the same as in sbt 0.13.0.
   *
   * IMPLEMENTATION NOTE:
   * Enabling this flag enables a few additional functionalities that are needed by the name hashing algorithm:
   *
   *   1. New dependency source tracking is used. See `sbt.inc.Relations` for details.
   *   2. Used names extraction and tracking is enabled. See `sbt.inc.Relations` for details as well.
   *   3. Hashing of public names is enabled. See `sbt.inc.AnalysisCallback` for details.
   *
   */
  val nameHashing: Boolean,
  /*
   * THE `antStyle` OPTION IS UNSUPPORTED, MAY GO AWAY AT ANY POINT.
   *
   * Enables "ant-style" mode of incremental compilation. This mode emulates what Ant's scalac command does.
   * The idea is to recompile just changed source files and not perform any invalidation of dependencies. This
   * is a very naive mode of incremental compilation that very often leads to broken binaries.
   *
   * The Ant-style mode has been introduced because Scala team needs it for migration of Scala compiler to sbt.
   * The name hashing algorithm doesn't work well with Scala compiler sources due to deep inheritance chains.
   * There's a plan to refactor compiler's code to use more composition instead of inheritance.
   *
   * Once Scala compiler sources are refactored to work well with name hashing algorithm this option will be
   * deleted immediately.
   */
  val antStyle: Boolean
) extends Serializable {

  /**
   * Secondary constructor introduced to make IncOptions to be binary compatible with version that didn't have
   * `recompileOnMacroDef` and `nameHashing` fields defined.
   */
  def this(transitiveStep: Int, recompileAllFraction: Double, relationsDebug: Boolean, apiDebug: Boolean,
    apiDiffContextSize: Int, apiDumpDirectory: Option[java.io.File], newClassfileManager: () => ClassfileManager) = {
    this(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, IncOptions.recompileOnMacroDefDefault, IncOptions.nameHashingDefault,
      IncOptions.antStyleDefault)
  }

  assert(!(antStyle && nameHashing), "Name hashing and Ant-style cannot be enabled at the same time.")

  def withTransitiveStep(transitiveStep: Int): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyle)
  }

  def withRecompileAllFraction(recompileAllFraction: Double): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyle)
  }

  def withRelationsDebug(relationsDebug: Boolean): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyle)
  }

  def withApiDebug(apiDebug: Boolean): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyle)
  }

  def withApiDiffContextSize(apiDiffContextSize: Int): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyle)
  }

  def withApiDumpDirectory(apiDumpDirectory: Option[File]): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyle)
  }

  def withNewClassfileManager(newClassfileManager: () => ClassfileManager): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyle)
  }

  def withRecompileOnMacroDef(recompileOnMacroDef: Boolean): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyle)
  }

  def withNameHashing(nameHashing: Boolean): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyle)
  }

  def withAntStyle(antStyle: Boolean): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyle)
  }

  override def hashCode(): Int = {
    import scala.runtime.Statics
    var acc: Int = -889275714
    acc = Statics.mix(acc, transitiveStep)
    acc = Statics.mix(acc, Statics.doubleHash(recompileAllFraction))
    acc = Statics.mix(acc, if (relationsDebug) 1231 else 1237)
    acc = Statics.mix(acc, if (apiDebug) 1231 else 1237)
    acc = Statics.mix(acc, apiDiffContextSize)
    acc = Statics.mix(acc, Statics.anyHash(apiDumpDirectory))
    acc = Statics.mix(acc, Statics.anyHash(newClassfileManager))
    acc = Statics.mix(acc, if (recompileOnMacroDef) 1231 else 1237)
    acc = Statics.mix(acc, if (nameHashing) 1231 else 1237)
    acc = Statics.mix(acc, if (antStyle) 1231 else 1237)
    Statics.finalizeHash(acc, 9)
  }

  override def toString(): String =
    s"""IncOptions(
       |  transitiveStep = $transitiveStep,
       |  recompileAllFraction = $recompileAllFraction,
       |  relationsDebug = $relationsDebug,
       |  apiDebug = $apiDebug,
       |  apiDiffContextSize = $apiDiffContextSize,
       |  apiDumpDirectory = $apiDumpDirectory,
       |  newClassfileManager = $newClassfileManager,
       |  recompileOnMacroDef = $recompileOnMacroDef,
       |  nameHashing = $nameHashing,
       |  antStyle = $antStyle
       |)""".stripMargin

  override def equals(x$1: Any): Boolean = {
    this.eq(x$1.asInstanceOf[Object]) || (x$1.isInstanceOf[IncOptions] && ({
      val IncOptions$1: IncOptions = x$1.asInstanceOf[IncOptions]
      transitiveStep == IncOptions$1.transitiveStep && recompileAllFraction == IncOptions$1.recompileAllFraction &&
        relationsDebug == IncOptions$1.relationsDebug && apiDebug == IncOptions$1.apiDebug &&
        apiDiffContextSize == IncOptions$1.apiDiffContextSize && apiDumpDirectory == IncOptions$1.apiDumpDirectory &&
        newClassfileManager == IncOptions$1.newClassfileManager &&
        recompileOnMacroDef == IncOptions$1.recompileOnMacroDef && nameHashing == IncOptions$1.nameHashing &&
        antStyle == IncOptions$1.antStyle
    }))
  }
  //- EXPANDED CASE CLASS METHOD END -//
}

object IncOptions extends Serializable {
  private val recompileOnMacroDefDefault: Boolean = true
  private[sbt] val nameHashingDefault: Boolean = true
  private val antStyleDefault: Boolean = false
  val Default = IncOptions(
    //    1. recompile changed sources
    // 2(3). recompile direct dependencies and transitive public inheritance dependencies of sources with API changes in 1(2).
    //    4. further changes invalidate all dependencies transitively to avoid too many steps
    transitiveStep = 3,
    recompileAllFraction = 0.5,
    relationsDebug = false,
    apiDebug = false,
    apiDiffContextSize = 5,
    apiDumpDirectory = None,
    newClassfileManager = ClassfileManager.deleteImmediately,
    recompileOnMacroDef = recompileOnMacroDefDefault,
    nameHashing = nameHashingDefault
  )
  //- EXPANDED CASE CLASS METHOD BEGIN -//
  final override def toString(): String = "IncOptions"

  def apply(transitiveStep: Int, recompileAllFraction: Double, relationsDebug: Boolean, apiDebug: Boolean,
    apiDiffContextSize: Int, apiDumpDirectory: Option[java.io.File],
    newClassfileManager: () => ClassfileManager, recompileOnMacroDef: Boolean,
    nameHashing: Boolean): IncOptions = {
    new IncOptions(transitiveStep, recompileAllFraction, relationsDebug, apiDebug, apiDiffContextSize,
      apiDumpDirectory, newClassfileManager, recompileOnMacroDef, nameHashing, antStyleDefault)
  }

  private def readResolve(): Object = IncOptions
  //- EXPANDED CASE CLASS METHOD END -//

  private val transitiveStepKey = "transitiveStep"
  private val recompileAllFractionKey = "recompileAllFraction"
  private val relationsDebugKey = "relationsDebug"
  private val apiDebugKey = "apiDebug"
  private val apiDumpDirectoryKey = "apiDumpDirectory"
  private val apiDiffContextSizeKey = "apiDiffContextSize"
  private val recompileOnMacroDefKey = "recompileOnMacroDef"
  private val nameHashingKey = "nameHashing"
  private val antStyleKey = "antStyle"

  def fromStringMap(m: java.util.Map[String, String]): IncOptions = {
    // all the code below doesn't look like idiomatic Scala for a good reason: we are working with Java API
    def getTransitiveStep: Int = {
      val k = transitiveStepKey
      if (m.containsKey(k)) m.get(k).toInt else Default.transitiveStep
    }
    def getRecompileAllFraction: Double = {
      val k = recompileAllFractionKey
      if (m.containsKey(k)) m.get(k).toDouble else Default.recompileAllFraction
    }
    def getRelationsDebug: Boolean = {
      val k = relationsDebugKey
      if (m.containsKey(k)) m.get(k).toBoolean else Default.relationsDebug
    }
    def getApiDebug: Boolean = {
      val k = apiDebugKey
      if (m.containsKey(k)) m.get(k).toBoolean else Default.apiDebug
    }
    def getApiDiffContextSize: Int = {
      val k = apiDiffContextSizeKey
      if (m.containsKey(k)) m.get(k).toInt else Default.apiDiffContextSize
    }
    def getApiDumpDirectory: Option[java.io.File] = {
      val k = apiDumpDirectoryKey
      if (m.containsKey(k))
        Some(new java.io.File(m.get(k)))
      else None
    }
    def getRecompileOnMacroDef: Boolean = {
      val k = recompileOnMacroDefKey
      if (m.containsKey(k)) m.get(k).toBoolean else Default.recompileOnMacroDef
    }
    def getNameHashing: Boolean = {
      val k = nameHashingKey
      if (m.containsKey(k)) m.get(k).toBoolean else Default.nameHashing
    }

    def getAntStyle: Boolean = {
      val k = antStyleKey
      if (m.containsKey(k)) m.get(k).toBoolean else Default.antStyle
    }

    new IncOptions(getTransitiveStep, getRecompileAllFraction, getRelationsDebug, getApiDebug, getApiDiffContextSize,
      getApiDumpDirectory, ClassfileManager.deleteImmediately, getRecompileOnMacroDef, getNameHashing, getAntStyle)
  }

  def toStringMap(o: IncOptions): java.util.Map[String, String] = {
    val m = new java.util.HashMap[String, String]
    m.put(transitiveStepKey, o.transitiveStep.toString)
    m.put(recompileAllFractionKey, o.recompileAllFraction.toString)
    m.put(relationsDebugKey, o.relationsDebug.toString)
    m.put(apiDebugKey, o.apiDebug.toString)
    o.apiDumpDirectory.foreach(f => m.put(apiDumpDirectoryKey, f.toString))
    m.put(apiDiffContextSizeKey, o.apiDiffContextSize.toString)
    m.put(recompileOnMacroDefKey, o.recompileOnMacroDef.toString)
    m.put(nameHashingKey, o.nameHashing.toString)
    m
  }
}
