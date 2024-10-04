package com.example.random.lunch

import java.time.LocalDate
import com.github.michaelbull.result.Result

// 初期のランダムランチリクエスト
data class InitRandomLunchRequest(
  val id: Int,
  val participantIds: List<ParticipantId>,
  val date: LocalDate,
  val applicationDate: LocalDate,
  val applicationBy: Participant,
  val amount: Int,
  val invoiceUrl: String,
)

// 参加者ID
data class ParticipantId(
  val id: Int,
)

// 従業員タイプ
enum class EmployeeType {
  FULL_TIME,
  PART_TIME,
  INTERN,
}

// 参加者
data class Participant(
  val id: ParticipantId,
  val name: String,
  val email: String,
  val employeeType: EmployeeType,
  val departmentIds: List<DepartmentId>
)

// 部署ID
data class DepartmentId(
  val id: Int
)

// 部署
data class Department(
  val id: DepartmentId,
  val name: String
)

// 承認されたランダムランチリクエストID
data class ApprovedRandomLunchRequestId(
  val id: Int
)

// 承認されたランダムランチリクエスト
data class ApprovedRandomLunchRequest(
  val id: Int,
  val participantIds: List<ParticipantId>
)

// 拒否されたランダムランチリクエスト
data class DeniedRandomLunchRequest(
  val id: Int,
  val deniedReason: DeniedReason,
)

// 拒否理由
sealed interface DeniedReason {
  data object Participants : DeniedReason
  data object NoRestaurants : DeniedReason
  data object NoTime : DeniedReason
}

typealias ApproveRandomLunch = (InitRandomLunchRequest) -> Result<ApprovedRandomLunchRequest, DeniedRandomLunchRequest>

// ランダムランチ履歴
data class RandomLunchHistory(
  val id: Int,
  val participantIds: List<ParticipantId>,
  val date: LocalDate,
  val applicationDate: LocalDate,
  val applicationBy: Participant,
  val amount: Int,
  val invoiceUrl: String,
  val approvedRandomLunchRequestId: ApprovedRandomLunchRequestId,
)
