package dire.example

import dire._
import dire.swing._, Swing._, Shape._
import math.{Pi, sin, cos}
import java.awt.Color._
import scala.collection.immutable.{IndexedSeq ⇒ IxSeq}
import scalaz._, Scalaz._
import scalaz.std.indexedSeq._

/** A combination of simple animations */
object Animation extends SwingApp {

  implicit def SMonoid[A:Monoid] = Monoid.liftMonoid[SIn,A]

  def behavior(f: Frame) = for {
    scene ← Scene(background := BLACK)
    _     ← Elem(scene) prefDim (1000, 800) addTo f

    //Behavior:
    //Combine shape signals s1 to s5 through monoid append and display
    //then set title of frame to "Animation"
    sf    = ((s1 ⊹ s2 ⊹ s3 ⊹ s4 ⊹ s5(scene) ⊹ s6(scene)) to scene.display) >>
            (SF const "Animation" to f.title)
  } yield sf

  //time signal that updates every 10 milliseconds
  val ticks = SF.cached(SF ticks 10000L count, "ticks")

  // sine wave signal that updates every 10 milliseconds:
  // f: Frequency [Hz]
  // a: amplitude [Pixels]
  // offset [Pixels]
  // phi: phase [radians]
  def wave(f: Double, a: Double, offset: Double, phi: Double = 0D)
    : SIn[Double] =
    ticks map { t ⇒ sin(t * 2 * Pi * f / 100 + phi) * a + offset }

  //a red circle that rotates around point (200, 200)
  def s1: SIn[Shape] = ^(
    wave(0.7, 100, 200),
    wave(0.7, 100, 200, Pi / 2)
  ) { circle(_, _, 50, RED) }

  //a green square that moves back and forth horizontally
  def s2: SIn[Shape] =
    wave(0.2, 300, 500) ∘ { square(_, 75.0, 100, GREEN) }

  //a blue circle moving up and down while changing its size.
  //changing the size with the same frequency as the
  //vertical movement gives the illusion of a rotation
  //in the third dimension
  def s3: SIn[Shape]= ^(
    wave(0.5, 200, 200, Pi / 2),
    wave(0.5, 100, 150)
  ){ (y,r) ⇒ circle(420.0, y, r.toInt, BLUE) }

  //a yellow square bouncing up and down along
  //a diagonal trajectory
  def s4: SIn[Shape]= ^(
    wave(0.3, 300, 400, Pi / 2),
    wave(0.3, 200, 0)
  ){ (y,r) ⇒ square(y, y, r.abs.toInt, YELLOW) }

  //a cyan circle that follows the mouse pointer
  def s5(s: Scene): SIn[Shape]=
    s.mousePosition map { case (x, y) ⇒ circle(x-20, y-20, 40, CYAN) }

  //a ever growing poly line
  def s6(s: Scene): SIn[Shape]=
    (s.mousePosition on s.leftClicks).scanPlus[IxSeq]
                                     .map { polyLine(_, MAGENTA) }

}

// vim: set ts=2 sw=2 et:
