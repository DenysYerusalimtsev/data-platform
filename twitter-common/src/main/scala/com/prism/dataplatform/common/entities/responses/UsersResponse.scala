package com.prism.dataplatform.common.entities.responses

import com.prism.dataplatform.common.entities.{User, UserIncludes, Error}


case class UsersResponse(data: Seq[User],
                         includes: Option[UserIncludes],
                         errors: Seq[Error])
