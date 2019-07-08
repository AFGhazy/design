package callcenter
import java.util.Queue
import java.util.LinkedList

enum class Rank(val value: Int) {
	Director(2),
	Manager(1),
	Respondent(0);
	
	companion object {
		fun getRank(value: Int): Rank {
			for(v in values()) {
				if(v.value == value) return v
			}
			return Director
		}
	}
}

abstract class Employee(val callHandler: CallHandler) {
	abstract val rank: Rank
	private var call: Call? = null
	
	fun isFree(): Boolean = call == null
	
	fun acceptCall(call: Call) {
		this.call = call
	}
	
	fun endCall() {
		call = null
		callHandler.notifyCallEnd()
	}
	
	fun escalate() {
		val call = this.call
		this.call = null
		
		call?.let { callHandler.escalate(it) }
	}
	
	override fun toString() = rank.toString() + ": " + call
}

data class Call(val phoneNumber: String, val rank: Rank)

class CallHandler() {
	
	val employees = hashMapOf<Rank, MutableList<Employee>>().apply {
		set(Rank.Respondent, mutableListOf<Employee>())
		set(Rank.Manager, mutableListOf<Employee>())
		set(Rank.Director, mutableListOf<Employee>())
	}
	
	val calls: HashMap<Rank, Queue<Call> > = hashMapOf<Rank, Queue<Call> >().apply {
		set(Rank.Respondent, LinkedList<Call>())
		set(Rank.Manager, LinkedList<Call>())
		set(Rank.Director, LinkedList<Call>())
	}
	
	fun getHandler(call: Call): Employee? {
		var rank = call.rank
		do {
			for(employee in employees[rank] ?: mutableListOf()) {
				if(employee.isFree()) return employee
			}
			rank = Rank.getRank(rank.value + 1)
		} while(rank != Rank.Director)
		
		return null
	}
	
	fun acceptCall(call: Call) {
		calls[call.rank]?.add(call)
		allocateCallToHandler()
	}
	
	fun notifyCallEnd() {
		allocateCallToHandler()
	}
	
	fun allocateCallToHandler() {
		for((_, callz) in calls) {
			while(!callz.isEmpty()) {
				val call = callz.peek()
				
				val handler = getHandler(call)
				if(handler == null) break
				
				callz.poll()
				handler.acceptCall(call)
				println(handler)
			}
		}
	}
	
	fun escalate(call: Call) {
		val rank = Rank.getRank(call.rank.value + 1)
		acceptCall(call.copy(rank = rank))
		
		allocateCallToHandler()
	}
	
	fun addHandler(employee: Employee) {
		employees[employee.rank]?.add(employee)
	}
}

class Respondent(callHandler: CallHandler) : Employee(callHandler) {
	override val rank = Rank.Respondent
}

class Manager(callHandler: CallHandler) : Employee(callHandler) {
	override val rank = Rank.Manager
}

class Director(callHandler: CallHandler) : Employee(callHandler) {
	override val rank = Rank.Director
}

fun main() {
	val callHandler = CallHandler()
	val respondent = Respondent(callHandler)
	val manager = Manager(callHandler)
	val director = Director(callHandler)
	callHandler.addHandler(respondent)
	callHandler.addHandler(manager)
	callHandler.addHandler(director)
	callHandler.acceptCall(Call("+11111", Rank.Respondent))
	respondent.escalate()
	manager.escalate()
	director.escalate()
	director.escalate()
	director.escalate()
	director.escalate()
	director.escalate()
	
	
	callHandler.acceptCall(Call("+22222", Rank.Respondent))
	callHandler.acceptCall(Call("+33333", Rank.Respondent))
	callHandler.acceptCall(Call("+44444", Rank.Respondent))
}