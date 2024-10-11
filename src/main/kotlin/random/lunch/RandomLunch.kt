package com.example.random.lunch

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Err
import java.time.LocalDate
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrThrow
import java.time.LocalDateTime

// 初期のランダムランチリクエスト
data class InitRandomLunchRequest(
  val id: Int,
  val emails: List<Email>,
  val date: LocalDate,
  val applicationDate: LocalDate,
  val applicationBy: Participant,
  val amount: Int,
  val invoiceUrl: String,
)

// 参加者ID
data class EmployeeId(
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
  val name: String,
  val email: String,
)

data class Employee(
  val id: EmployeeId,
  val name: String,
  val email: String,
  val employeeType: EmployeeType,
  val departmentId: DepartmentId,
  val registerDate: LocalDate,
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
  val employeeIds: List<EmployeeId>,
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
  val employeeIds: List<EmployeeId>
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
  data object NoCrossDepartment: DeniedReason
  data object AmountOver: DeniedReason
}

typealias ApproveRandomLunch = (InitRandomLunchRequest) -> Result<ApprovedRandomLunchRequest, DeniedRandomLunchRequest>

// ランダムランチ履歴
data class RandomLunchHistory(
  val id: Int,
  val employeeIds: List<EmployeeId>,
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
    date = LocalDate.now(),
    emails = listOf(),
    applicationDate = LocalDate.now(),
    applicationBy = Participant(
      name = "name",
      email = "email",
    ),
    amount = 1000,
    invoiceUrl = "url"
  )
   val getEmployee: (Email) -> Employee = { mailAddress ->
     Employee(
       EmployeeId(1),
       mailAddress + "Name",
       mailAddress,
       EmployeeType.FULL_TIME,
       DepartmentId(2),
       LocalDate.now()
       )
   }

  val isNewFace: (LocalDate) -> Boolean = { TODO() }

  val result = verifyRandomLunchRequest(param, getEmployee ,,,,)
}

data class Week(
  val startDate: LocalDate,
  val endDate: LocalDate,
){}

fun verifyRandomLunchRequest(
  param: InitRandomLunchRequest,
  getEmployee: (Email) -> Employee?,
  isNewFace: (LocalDate) -> Boolean,
  getWeek: (LocalDate) -> Week,
  getParticipationCount: (Week, Set<EmployeeId>) -> Map<EmployeeId, Int>
): Result<VerifiedRandomLunchRequest, DeniedRandomLunchRequest> {

  // 参加人数4~6人か？
  val validatedMemberNums = validateMemberNums(param)
    .getOrThrow { throw RuntimeException() }

  // 参加者が社員か？
  // ParticipantsをEmployeeにする
  val validEmployees = param.emails
    .map { getEmployee(it) ?: return Err(DeniedRandomLunchRequest(param.id, DeniedReason.NoCrossDepartment)) }
    .filter { it.employeeType == EmployeeType.FULL_TIME }
  // 一人でも対象ではない人が申請されていたらエラー
  if (validEmployees.size != param.emails.size) {
    return Err(DeniedRandomLunchRequest(param.id, DeniedReason.NoCrossDepartment)) // TODO: エラー変更
  }

  // 2部署以上にまたがっているか
  // Employeeの所属部署(レポートライン)が被っていない人が1人でもいることを確認する
  if (validEmployees.map { it.departmentId }.toSet().size == 1) {
    return Err(DeniedRandomLunchRequest(param.id, DeniedReason.NoCrossDepartment))
  }

  // 週一回までか？（ニューフェイスなら無限）
  val isExistsNewFace = validEmployees.any { it ->
    isNewFace(it.registerDate)
  }
  if (!isExistsNewFace) {
    val week = getWeek(param.date)
    val employeeIdCountMap = getParticipationCount(week, validEmployees.map { it.id }.toSet())
    if (employeeIdCountMap.values.any { it != 0 }) {
      return Err(DeniedRandomLunchRequest(param.id, DeniedReason.NoCrossDepartment)) //TODO: エラー
    }
  }

  // 参加日が当月
  validateSameMont(param)
    .getOrThrow { throw RuntimeException() }

  // 当月に同じ社員とランダムランチをしているか？

  // 金額が人数*1,100まで
  if (param.amount > validEmployees.size * 1100) {
    return Err(DeniedRandomLunchRequest(param.id, DeniedReason.AmountOver))
  }

  TODO()
}



// sub step
// 参加者の  param.amount > employees.size * 1100チェック
// 被っていないかチェック


fun saveParticipants(approvedRandomLunch: ApproveRandomLunch, saveFun: () -> Result<Unit, RuntimeException>): Unit {

}

fun validateMemberNums(initRandomLunchRequest: InitRandomLunchRequest): Result<InitRandomLunchRequest, DeniedRandomLunchRequest> {
  return if (initRandomLunchRequest.emails.size in 4..6) {
    Ok(initRandomLunchRequest)
  } else {
    Err(DeniedRandomLunchRequest(initRandomLunchRequest.id, DeniedReason.Participants))
  }
}

fun validateSameMont(initRandomLunchRequest: InitRandomLunchRequest): Result<InitRandomLunchRequest, DeniedRandomLunchRequest> {
  return if (initRandomLunchRequest.date.month != LocalDate.now().month) {
    Ok(initRandomLunchRequest)
  } else {
    Err(DeniedRandomLunchRequest(initRandomLunchRequest.id, DeniedReason.NoTime))
  }
}

// work
fun isParticipantLimitSatisfied(
  request: VerifiedRandomLunchRequest
): Result<VerifiedRandomLunchRequest, DeniedRandomLunchRequest> {
  return if (request.employeeIds.size in 4..6) {
    Ok(request)
  } else {
    Err(
      DeniedRandomLunchRequest(
        request.id,
        DeniedReason.Participants
      )
    )
  }
}

fun confirmParticipantsAsEmployees(
  participantCount: Int,
  emails: List<Email>,
  getEmployee: (Email) -> Employee?
): Result<List<Employee>, DeniedReason> {
  val employees = emails.map { email ->
    getEmployee(email) ?: return Err(DeniedReason.NoCrossDepartment)
  }

  if (employees.size != emails.size) {
    return Err(DeniedReason.NoCrossDepartment)
  }

  return Ok(employees)
}

fun hasParticipatedWithinLastWeek(
  history: List<RandomLunchHistory>,
  request: VerifiedRandomLunchRequest,
  isNewFace: (Employee) -> Boolean
): Result<VerifiedRandomLunchRequest, DeniedReason.NoTime> {
  val oneWeekAgo = request.date.minusWeeks(1)

  val hasRecentParticipation = history.any { lunch ->
    lunch.date.isAfter(oneWeekAgo) && lunch.employeeIds.any { it in request.employeeIds }
  }

  return if (hasRecentParticipation) {
    Err(DeniedReason.NoTime)
  } else {
    Ok(request)
  }
}
