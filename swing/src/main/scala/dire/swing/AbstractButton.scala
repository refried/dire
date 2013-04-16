package dire.swing

import dire._
import javax.swing.{AbstractButton ⇒ JAbstractButton}

trait AbstractButton[A<:JAbstractButton]
  extends Component[A]
  with BlockedSignal {
  import AbstractButton._

  def clicks: SIn[Unit] = SF cachedSrc this

  //@TODO use blocked value
  def value: SIn[Boolean] =
    clicks map { _ ⇒ peer.isSelected } hold peer.isSelected

  def text: Sink[String] = sink(peer.setText, this)
}

object AbstractButton {

  implicit def ButtonSource[A<:JAbstractButton]
    : Source[AbstractButton[A],Unit] = eventSrc { b ⇒ o ⇒ 
    val a = ali(o)
    b.peer.addActionListener(a)
    _ ⇒ b.peer.removeActionListener(a)
  }
}

// vim: set ts=2 sw=2 et: