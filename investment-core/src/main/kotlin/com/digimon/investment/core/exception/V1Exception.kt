package com.digimon.investment.core.exception

class V1Exception : RuntimeException {
    enum class Kind(val code: Int) {
        UNKNOWN(99), USER(10), PRODUCT(11), INVESTMENT(12)
    }

    enum class Status(val code: Int) {
        BAD_REQUEST(400), UNAUTHORIZED(401), FORBIDDEN(403), NOT_FOUND(404), UNPROCESSABLE_ENTITY(422), TOO_MANY_REQUESTS(429),
        INTERNAL_SERVER_ERROR(500),
        NOT_ENOUGH(601), SOLD_OUT(602), ALREADY_INVESTED(603);

        companion object {
            fun of(code: Int): Status {
                return runCatching {
                    Status.values().first { it.code == code }
                }.getOrDefault(INTERNAL_SERVER_ERROR)
            }
        }
    }

    private var kind: Kind
    private var status: Status

    constructor() : super() {
        status = Status.INTERNAL_SERVER_ERROR
        kind = Kind.UNKNOWN
        errorMessage = null
    }

    constructor(cause: Throwable?) : super(cause) {
        status = Status.INTERNAL_SERVER_ERROR
        kind = Kind.UNKNOWN
        errorMessage = null
    }

    constructor(kind: Kind, status: Status) {
        this.kind = kind
        this.status = status
        errorMessage = null
    }

    constructor(kind: Kind, status: Status, message: String) : super(message) {
        this.kind = kind
        this.status = status
        errorMessage = null
    }

    constructor(kind: Kind, status: Status, cause: Throwable) : this(kind, status) {
        initCause(cause)
    }

    fun getKind(): Int {
        return kind.code
    }

    val errorCode: Int
        get() = -(kind.code * 1000 + status.code)

    var errorMessage: String? = null
        get() = if (field.isNullOrBlank()) "[${kind.name}] ${status.name}" else field

    override val message: String?
        get() = if (super.message.isNullOrBlank()) "${kind}:${status}" else super.message
}
