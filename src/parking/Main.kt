package parking

fun main() {
	val floor = Floor("1")
	floor.addParkingSpot(MotorSpot("1"))
	floor.addParkingSpot(CompactSpot("2"))
	floor.addParkingSpot(LargeSpot("3"))
	floor.addParkingSpot(LargeSpot("4"))
	floor.addParkingSpot(ElectricSpot("5"))
	val car = Car("123")
	val parkingLot = ParkingLot("p1")
	parkingLot.addParkingFloor(floor)
	
	println(parkingLot)
	parkingLot.park(car)
	println(parkingLot)
	println(car)
	Thread.sleep(1000)
	val ticket = parkingLot.leave(car)
	println(parkingLot)
	ticket?.let { println(parkingLot.pay(ticket, 2000)) }
}

// base

enum class VehicleType {
	MOTOR, CAR, VAN, TRUCK, ELECTRIC;
}

enum class SpotType {
	MOTOR, COMPACT, LARGE, ELECTRIC, HANDICAPPED;
}

enum class AccountStatus {
	ACTIVE, BLOCKED, BANNED, COMPROMISED, ARCHIVED, UNKNOWN;
}

enum class TicketStatus {
	ACTIVE, PAID, LOST;
}

data class Address(val streetAddress: String,
				   val city: String,
				   val state: String,
				   val zipCode: String,
				   val country: String)

data class Person(
	val name: String,
	val address: Address,
	val email: String,
	val phone: String
)



data class Ticket(val spotId: String, val type: TicketStatus, val start: Long, val end: Long? = null)

// vehicle
abstract class Vehicle(val type: VehicleType,
					   val license: String,
					   var ticket: Ticket?) {
	override fun toString() = type.toString() + " " + license + " " + ticket
}

class Motor(license: String, ticket: Ticket? = null) :
	Vehicle(VehicleType.MOTOR, license, ticket)

class Car(license: String, ticket: Ticket? = null) :
	Vehicle(VehicleType.CAR, license, ticket)

class Van(license: String, ticket: Ticket? = null) :
	Vehicle(VehicleType.VAN, license, ticket)

class Truck(license: String, ticket: Ticket? = null) :
	Vehicle(VehicleType.TRUCK, license, ticket)

class Electric(license: String, ticket: Ticket? = null) :
	Vehicle(VehicleType.ELECTRIC, license, ticket)



// spot
abstract class Spot(val type: SpotType,
				val id: String,
				var vehicle: Vehicle? = null) {
	val isFree: Boolean
		get() = vehicle == null
	
	override fun toString() = type.toString() + " " + id + " " + vehicle + " " + isFree
}

class MotorSpot(id: String, vehicle: Vehicle? = null) :
	Spot(SpotType.MOTOR, id, vehicle)

class CompactSpot(id: String, vehicle: Vehicle? = null) :
	Spot(SpotType.COMPACT, id, vehicle)

class LargeSpot(id: String, vehicle: Vehicle? = null) :
	Spot(SpotType.LARGE, id, vehicle)

class HandicappedSpot(id: String, vehicle: Vehicle? = null) :
	Spot(SpotType.HANDICAPPED, id, vehicle)

class ElectricSpot(id: String, vehicle: Vehicle? = null) :
	Spot(SpotType.ELECTRIC, id, vehicle)

















// floor

interface FloorResponsiblities {
	fun addParkingSpot(spot: Spot)
	fun park(vehicle: Vehicle): String
	fun leave(vehicle: Vehicle): String
}

data class Floor(val id: String) : FloorResponsiblities {
	val motorSpots = hashMapOf<String, MotorSpot>()
	val compactSpots = hashMapOf<String, CompactSpot>()
	val largeSpots = hashMapOf<String, LargeSpot>()
	val electricSpots = hashMapOf<String, ElectricSpot>()
	val handicappedSpots = hashMapOf<String, HandicappedSpot>()
	
	override fun toString() =
		id + " " + motorSpots + " " + compactSpots + " " + largeSpots + " " + electricSpots + " " + handicappedSpots
	
	override fun addParkingSpot(spot: Spot) {
		when(spot) {
			is MotorSpot -> motorSpots[spot.id] = spot
			is CompactSpot -> compactSpots[spot.id] = spot
			is LargeSpot -> largeSpots[spot.id] = spot
			is ElectricSpot -> electricSpots[spot.id] = spot
			is HandicappedSpot -> handicappedSpots[spot.id] = spot
		}
	}
	
	override fun park(vehicle: Vehicle): String {
		when(vehicle) {
			is Motor -> {
				for((id, spot) in motorSpots) {
					if(spot.isFree) {
						vehicle.ticket = Ticket(id, TicketStatus.ACTIVE, System.currentTimeMillis())
						spot.vehicle = vehicle
						return id
					}
				}
			}
			is Car -> {
				for((id, spot) in compactSpots) {
					if(spot.isFree) {
						vehicle.ticket = Ticket(id, TicketStatus.ACTIVE, System.currentTimeMillis())
						spot.vehicle = vehicle
						return id
					}
				}
			}
			is Van -> {
				for((id, spot) in largeSpots) {
					if(spot.isFree) {
						vehicle.ticket = Ticket(id, TicketStatus.ACTIVE, System.currentTimeMillis())
						spot.vehicle = vehicle
						return id
					}
				}
			}
			is Truck -> {
				for((id, spot) in largeSpots) {
					if(spot.isFree) {
						vehicle.ticket = Ticket(id, TicketStatus.ACTIVE, System.currentTimeMillis())
						spot.vehicle = vehicle
						return id
					}
				}
			}
			is Electric -> {
				for((id, spot) in electricSpots) {
					if(spot.isFree) {
						vehicle.ticket = Ticket(id, TicketStatus.ACTIVE, System.currentTimeMillis())
						spot.vehicle = vehicle
						return id
					}
				}
			}
		}
		return (-1).toString()
	}
	
	override fun leave(vehicle: Vehicle): String {
		vehicle.ticket?.spotId?.let {
			when(vehicle) {
				is Motor -> {
					if(motorSpots.contains(it)) {
						motorSpots.remove(it)
						vehicle.ticket =
							vehicle.ticket?.copy(end = System.currentTimeMillis())
						return it
					}
				}
				is Car -> {
					if(compactSpots.contains(it)) {
						motorSpots.remove(it)
						vehicle.ticket =
							vehicle.ticket?.copy(end = System.currentTimeMillis())
						return it
					}
				}
				is Van -> {
					if(largeSpots.contains(it)) {
						motorSpots.remove(it)
						vehicle.ticket =
							vehicle.ticket?.copy(end = System.currentTimeMillis())
						return it
					}
				}
				is Truck -> {
					if(largeSpots.contains(it)) {
						motorSpots.remove(it)
						vehicle.ticket =
							vehicle.ticket?.copy(end = System.currentTimeMillis())
						return it
					}
				}
				is Electric -> {
					if(electricSpots.contains(it)) {
						motorSpots.remove(it)
						vehicle.ticket =
							vehicle.ticket?.copy(end = System.currentTimeMillis())
						return it
					}
				}
			}
		}
		return (-1).toString()
	}
}



interface ParkingLotResponsiblities {
	fun addParkingFloor(floor: Floor)
	fun park(vehicle: Vehicle): Ticket?
	fun leave(vehicle: Vehicle): Ticket?
	fun pay(ticket: Ticket, cash: Long): Ticket
}

data class ParkingLot(val id: String) : ParkingLotResponsiblities {
	val floors = mutableListOf<Floor>()
	
	override fun addParkingFloor(floor: Floor) {
		floors.add(floor)
	}
	
	override fun park(vehicle: Vehicle): Ticket? {
		val INVALID = (-1).toString()
		
		for(floor in floors) {
			val id = floor.park(vehicle)
			if(id == INVALID) continue
			
			return vehicle.ticket
		}
		return null
	}
	
	override fun leave(vehicle: Vehicle): Ticket? {
		val INVALID = (-1).toString()
		
		for(floor in floors) {
			val id = floor.leave(vehicle)
			if(id == INVALID) continue
			
			return vehicle.ticket
		}
		return null
	}
	
	override fun pay(ticket: Ticket, cash: Long): Ticket {
		if(cash >= (ticket.end ?: System.currentTimeMillis()) - ticket.start) {
			// make the payment
			return ticket.copy(type = TicketStatus.PAID)
		}
		return ticket
	}
}






























abstract class Account(val userName: String,
					   val password: String,
					   val status: AccountStatus,
					   val person: Person) {
	
	abstract fun resetPassword(old: String, new: String): Boolean
}

interface AdminResponsibilities {
	fun addParkingFloor(floor: Floor): Boolean
	fun addParkingSpot(floorName: String, spot: Spot): Boolean
}

interface ParkingAttendant {
	fun processTicket(ticketNumber: String)
}

class Admin(userName: String, password: String, status: AccountStatus, person: Person) :
	Account(userName, password, status, person), AdminResponsibilities {
	
	override fun resetPassword(old: String, new: String) = true
	
	override fun addParkingFloor(floor: Floor): Boolean = true
	override fun addParkingSpot(floorName: String, spot: Spot): Boolean = true
	
}




