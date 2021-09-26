package com.prism.dataplatform.common.entities

case class UserIncludes(
                         tweets: Seq[Tweet],
                         users: Seq[User]
                       )
