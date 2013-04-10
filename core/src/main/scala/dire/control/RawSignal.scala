package dire.control

import dire._
import scalaz._, Scalaz._, effect.IO

/** The inner workings of signals
  *
  * A signal consists of a node that represents its dependencies
  * in the reactive graph and a method to request the latest
  * change that happened in the signal
  */
sealed trait RawSignal[+A] { self ⇒ 
  private[control] def node: Node

  private[control] def last: Change[A]
}

/** Represents a signal that never changes */
private[dire] case class Const[+A](a: A) extends RawSignal[A] {
  val node = Isolated
  val last = Change(T0, a)
}

private[dire] class RawSource[A](s: RSource[A]) extends RawSignal[A] {
  def node = s.node
  def last = s.last
}

object RawSignal {

  def const[A](a: ⇒ A): IO[RawSignal[A]] = IO(Const(a))

  /** Creates a derived signal from an existing one.
    *
    * The new signal is updated synchronously whenever its parent
    * changes.
    */
  private[dire] def sync1[A,B]
    (a: RawSignal[A])
    (ini: Change[A] ⇒ B)
    (next: (Change[A],Change[B]) ⇒ Option[B])
    : IO[RawSignal[B]] = IO {
    new RawSignal[B] {
      private[control] var last: Change[B] = 
        Change(a.last.at, ini(a.last))

      private[control] val node: ChildNode = new ChildNode {
        protected def doCalc(t: Time) = {
          val newV = next(a.last, last)
          last = newV cata (Change(a.last.at, _), last)
          //println(s"new value: $last; changed: ${last.at == t}")
          last.at == t
        }

        protected def doClean() {}
      }

      a.node connectChild node
    }
  }

  /** Combines two signals to create a new signal that is
    * updated synchronously whenever one or both of the
    * input signals change their state
    */
  private[dire] def sync2[A,B,C]
    (a: RawSignal[A], b: RawSignal[B])
    (ini: (Change[A],Change[B]) ⇒ C)
    (next: (Change[A],Change[B],Change[C]) ⇒ Option[C])
    : IO[RawSignal[C]] = IO {
    new RawSignal[C] {
      private[control] var last: Change[C] =
        Change(a.last.at, ini(a.last, b.last))

      private[control] val node: ChildNode = new ChildNode {
        protected def doCalc(t: Time) = {
          val newV = next(a.last, b.last, last)
          //print(s"Time: $t; a: ${a.last}; b: ${b.last}; last: $last; ")
          last = newV cata (Change(a.last.at max b.last.at, _), last)
          //println(s"new value: $last; changed: ${last.at == t}")
          last.at == t
        }

        protected def doClean() {}
      }

      a.node connectChild node
      b.node connectChild node
    }
  }
}

// vim: set ts=2 sw=2 et:
