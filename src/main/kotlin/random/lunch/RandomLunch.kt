package com.example.random.lunch

import com.github.michaelbull.result.Err
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

typealias Email = String

// 参加者
data class Participant(
  val id: ParticipantId,
  val name: String,
  val email: String,
  val employeeType: EmployeeType,
)

data class Employee(
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

// 検証済みのランダムランチリクエスト
data class VerifiedRandomLunchRequest(
  val id: Int,
  val participantIds: List<ParticipantId>,
  val date: LocalDate,
  val applicationDate: LocalDate,
  val applicationBy: Participant,
  val amount: Int,
  val invoiceUrl: String,
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

fun main() {
  val param = InitRandomLunchRequest(
    id = 1,
    participantIds = listOf(ParticipantId(1), ParticipantId(2)),
    date = LocalDate.now(),
    applicationDate = LocalDate.now(),
    applicationBy = Participant(
      id = ParticipantId(1),
      name = "name",
      email = "email",
      employeeType = EmployeeType.FULL_TIME,
      departmentIds = listOf(DepartmentId(1))
    ),
    amount = 1000,
    invoiceUrl = "url"
  )
   val getEmployee: (Email) -> Employee = { mailAddres ->
     Employee()
   }
}

fun verifyRandomLunchRequest(
  param: InitRandomLunchRequest,
  getEmployee: (Email) -> Employee,
  getDepartment: ()
): Result<VerifiedRandomLunchRequest, DeniedRandomLunchRequest> {
  if (param.participantIds.size > 6) {
    return Err(DeniedRandomLunchRequest(param.id, DeniedReason.Participants))
  }
  TODO()
}

fun saveParticipants(approvedRandomLunch: ApproveRandomLunch, saveFun: () -> Result<Unit, RuntimeException>): Unit {

}

