package com.prism.dataplatform.common.entities.enums.fields

object PollFields extends Enumeration {
  type PollFields = Value

  val DurationMinutes = Value("duration_minutes")
  val EndDatetime = Value("end_datetime")
  val Id = Value("id")
  val Options = Value("options")
  val VotingStatus = Value("voting_status")
}
