package com.prism.dataplatform.twitter.entities.responses

import com.prism.dataplatform.twitter.entities.{User, UserIncludes, Error}


case class UserResponse(data: Option[User],
                        includes: Option[UserIncludes],
                        errors: Seq[Error])
