package spire.algebra

import spire.implicits._

import org.scalacheck.{Arbitrary, Prop}
import org.scalacheck.Prop._

object GroupLaws {
  def apply[A : Eq : Arbitrary] = new GroupLaws[A] {
    def Equ = Eq[A]
    def Arb = implicitly[Arbitrary[A]]
  }
}

trait GroupLaws[A] extends Laws {

  implicit def Equ: Eq[A]
  implicit def Arb: Arbitrary[A]


  // groups

  def semigroup(implicit A: Semigroup[A]) = new GroupProperties(
    name = "semigroup",
    parent = None,
    "associative" → forAll((x: A, y: A, z: A) =>
      ((x |+| y) |+| z) === (x |+| (y |+| z))
    )
  )

  def monoid(implicit A: Monoid[A]) = new GroupProperties(
    name = "monoid",
    parent = Some(semigroup),
    "left identity" → forAll((x: A) =>
      (A.id |+| x) === x
    ),
    "right identity" → forAll((x: A) =>
      (x |+| A.id) === x
    )
  )

  def group(implicit A: Group[A]) = new GroupProperties(
    name = "group",
    parent = Some(monoid),
    "left inverse" → forAll((x: A) =>
      A.id === (x.inverse |+| x)
    ),
    "right inverse" → forAll((x: A) =>
      A.id === (x |+| x.inverse)
    )
  )

  def abGroup(implicit A: AbGroup[A]) = new GroupProperties(
    name = "abelian group",
    parent = Some(group),
    "commutative" → forAll((x: A, y: A) =>
      (x |+| y) === (y |+| x)
    )
  )


  // additive groups

  def additiveSemigroup(implicit A: AdditiveSemigroup[A]) = new AdditiveProperties(
    base = semigroup(A.additive),
    parent = None
  )

  def additiveMonoid(implicit A: AdditiveMonoid[A]) = new AdditiveProperties(
    base = monoid(A.additive),
    parent = Some(additiveSemigroup)
  )

  def additiveGroup(implicit A: AdditiveGroup[A]) = new AdditiveProperties(
    base = group(A.additive),
    parent = Some(additiveMonoid),
    "minus consistent" → forAll((x: A, y: A) =>
      (x - y) === (x + (-y))
    )
  )

  def additiveAbGroup(implicit A: AdditiveAbGroup[A]) = new AdditiveProperties(
    base = abGroup(A.additive),
    parent = Some(additiveGroup)
  )


  // property classes

  class GroupProperties(
    name: String,
    parent: Option[GroupProperties],
    props: (String, Prop)*
  ) extends DefaultProperties(name, parent, props: _*)

  class AdditiveProperties(
    val base: GroupProperties,
    val parent: Option[AdditiveProperties],
    val props: (String, Prop)*
  ) extends SpireProperties with HasOneParent {
    val name = base.name
    val bases = Seq("base" → base)
  }

}

// vim: expandtab:ts=2:sw=2
