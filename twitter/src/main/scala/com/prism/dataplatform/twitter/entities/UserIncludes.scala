package com.prism.dataplatform.twitter.entities

case class UserIncludes(
                         tweets: Array[Tweet],
                         users: Array[User]
                       )
