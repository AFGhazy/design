package parkinglot

import java.lang.System

const val INVALID_TIME = -1L

fun main(args : Array<String>) { 
	val bus: Bus = Bus("1")
	val bus2: Bus = Bus("2")
	val floor1 = Floor(List<Spot>(4) { Spot(Type.Normal) })
	val floor2 = Floor(List<Spot>(4) { Spot(Type.Electric) })
	val parkingLot = ParkingLot(listOf(floor1, floor2))
	
	println(parkingLot)
	println(parkingLot.park(bus))
	println(parkingLot.park(bus2))
	println(parkingLot)
	println("++++++++")
	Thread.sleep(20000)
	println(parkingLot.pay(parkingLot.leave(bus), PaymentMethod.CREDIT, null, 20020))
	println(parkingLot.pay(parkingLot.leave(bus2), PaymentMethod.CASH, null, 18000))
}

enum class Size(val numOfSpots: Int) {
	Motor(1), Compact(1), Large(4);
}

enum class Type(val alsoCanUse: List<Type>) {
	Electric(emptyList()),
	Access(emptyList()),
	Normal(listOf(Type.Electric, Type.Access));
}

data class Spot(val type: Type, var vehicle: Vehicle? = null) {}

abstract class Vehicle(val licensePlate: String,
					   val size: Size,
					   val vehicleType: Type) {
	var spots: List<Spot>? = null
	
	override fun toString(): String {
		return "[" + licensePlate + " " + size + " " + vehicleType + "]"
	}
}

class Motor(licensePlate: String) :
	Vehicle(licensePlate, Size.Motor, Type.Normal)

class Car(licensePlate: String) :
	Vehicle(licensePlate, Size.Compact, Type.Normal)

class ElectricCar(licensePlate: String) :
	Vehicle(licensePlate, Size.Compact, Type.Electric)

class AccessCar(licensePlate: String) :
	Vehicle(licensePlate, Size.Compact, Type.Access)

class Bus(licensePlate: String) :
	Vehicle(licensePlate, Size.Large, Type.Normal)


data class Floor(val spots: List<Spot>) {
	fun park(vehicle: Vehicle): Long {
		for(i in 0 .. spots.size - vehicle.size.numOfSpots) {
			var numOfSpots = 0
			for(j in i until i + vehicle.size.numOfSpots) {
				numOfSpots += if (spots.get(j).vehicle == null &&
					(vehicle.vehicleType == spots.get(j).type ||
						 spots.get(j).type in vehicle.vehicleType.alsoCanUse)) 1 else 0
			}
			
			if(numOfSpots == vehicle.size.numOfSpots) {
				val list = mutableListOf<Spot>()
				for(j in i until i + vehicle.size.numOfSpots) {
					spots.get(j).vehicle = vehicle
					list.add(spots.get(j))
				}
				vehicle.spots = list
				return System.currentTimeMillis()
			}
		}
		return INVALID_TIME
	}
	
	fun leave(vehicle: Vehicle): Long {
		var exist = false
		for(spot in spots) {
			if(vehicle.spots?.contains(spot) ?: false) {
				exist = true
				spot.vehicle = null
			}
		}
		if(exist) {
			vehicle.spots = null
			return System.currentTimeMillis()
		} else {
			return INVALID_TIME
		}
	}
}

data class Ticket(val start: Long, val end: Long)

enum class PaymentMethod {
	CREDIT,
	CASH;
}

data class ParkingLot(val floors: List<Floor>) {
	val vehicleToTS = hashMapOf<Vehicle, Long>()
	
	fun park(vehicle: Vehicle): Ticket {
		for(floor in floors) {
			val start = floor.park(vehicle)
			if(start == INVALID_TIME) continue
			
			vehicleToTS[vehicle] = start
			return Ticket(start, start)
		}
		return Ticket(INVALID_TIME, INVALID_TIME)
	}
	
	fun leave(vehicle: Vehicle): Ticket {
		
		for(floor in floors) {
			val t = floor.leave(vehicle)
			if(t != INVALID_TIME) {
				return Ticket(vehicleToTS[vehicle] ?: INVALID_TIME, t)
			}
		}
		return Ticket(INVALID_TIME, INVALID_TIME)
	}
	
	fun pay(ticket: Ticket, paymentMethod: PaymentMethod,
			credtiCardDetails: Long? = null,
			cashAmount: Long? = null): Boolean {
		return when(paymentMethod) {
			PaymentMethod.CASH -> ticket.end - ticket.start <= cashAmount ?: 0
			PaymentMethod.CREDIT -> ticket.end - ticket.start <= credtiCardDetails ?: 0
		}
	}
}