package com.prism.dataplatform.common.entities.responses

import com.prism.dataplatform.common.entities.{User, UserIncludes, Error}


case class UserResponse(data: Option[User],
                        includes: Option[UserIncludes],
                        errors: Seq[Error])
