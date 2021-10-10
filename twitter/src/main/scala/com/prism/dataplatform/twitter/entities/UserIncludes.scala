package com.prism.dataplatform.twitter.entities

case class UserIncludes(
                         tweets: Seq[Tweet],
                         users: Seq[User]
                       )
