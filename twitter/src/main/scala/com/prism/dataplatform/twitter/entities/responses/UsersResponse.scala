package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{User, UserIncludes, Error}


case class UsersResponse(data: Array[User],
                         includes: Option[UserIncludes],
                         errors: Array[Error])
