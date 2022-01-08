package com.prism.dataplatform.predef

sealed trait Either3[O1, O2, O3]

object Either3 {
  case class Out1[O1, O2, O3, O4, O5](o1: O1) extends Either3[O1, O2, O3]

  case class Out2[O1, O2, O3, O4, O5](o2: O2) extends Either3[O1, O2, O3]

  case class Out3[O1, O2, O3, O4, O5](o3: O3) extends Either3[O1, O2, O3]
}

