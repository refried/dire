package dire.swing

import javax.swing.SwingConstants._
import scalaz.Show
import scalaz.effect.IO

/** Type class representing a widget has a way of horizontally
  * and vertically align the text it displays
  */
trait TextAlign[-A] {
  def setHAlign(a: A, h: HAlign): IO[Unit]
  def setHTextPos(a: A, h: HAlign): IO[Unit]
  def setVAlign(a: A, v: VAlign): IO[Unit]
  def setVTextPos(a: A, v: VAlign): IO[Unit]

  final def hAlign(a: A): Sink[HAlign] = sinkIO(setHAlign(a,_))

  final def hTextPos(a: A): Sink[HAlign] = sinkIO(setHTextPos(a,_))

  final def vAlign(a: A): Sink[VAlign] = sinkIO(setVAlign(a,_))

  final def vTextPos(a: A): Sink[VAlign] = sinkIO(setVTextPos(a,_))
}

sealed abstract class HAlign(val v: Int)

object HAlign {
  case object Center extends HAlign(CENTER)
  case object Leading extends HAlign(LEADING)
  case object Left extends HAlign(LEFT)
  case object Right extends HAlign(RIGHT)
  case object Trailing extends HAlign(TRAILING)
}

sealed abstract class VAlign(val v: Int)

object VAlign {
  case object Bottom extends VAlign(BOTTOM)
  case object Center extends VAlign(CENTER)
  case object Top extends VAlign(TOP)
}

// vim: set ts=2 sw=2 et:
