package com.prism.dataplatform.predef

sealed trait Either5[O1, O2, O3, O4, O5]
object Either5 {
  case class Out1[O1, O2, O3, O4, O5](o1: O1) extends Either5[O1, O2, O3, O4, O5]
  case class Out2[O1, O2, O3, O4, O5](o2: O2) extends Either5[O1, O2, O3, O4, O5]
  case class Out3[O1, O2, O3, O4, O5](o3: O3) extends Either5[O1, O2, O3, O4, O5]
  case class Out4[O1, O2, O3, O4, O5](o4: O4) extends Either5[O1, O2, O3, O4, O5]
  case class Out5[O1, O2, O3, O4, O5](o5: O5) extends Either5[O1, O2, O3, O4, O5]
}
