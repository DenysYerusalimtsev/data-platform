package com.prism.dataplatform

package object predef {

  trait Supplier[+O] extends (() => O) with Serializable

}
