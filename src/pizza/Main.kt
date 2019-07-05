package pizza

fun main() {
	val pizzaFactory = PizzaFactory()
	println(pizzaFactory.newInstance()
		.garlic()
		.tomato()
		.build())
}

abstract class BasePizza {
	abstract val cost: Double
	
	override fun toString(): String = cost.toString()
}

class Pizza: BasePizza() {
	override val cost = 10.0
}

class Garlic(pizza: BasePizza) : BasePizza() {
	override val cost = 20.0 + pizza.cost
}

class Tomato(pizza: BasePizza) : BasePizza() {
	override val cost = 0.5 + pizza.cost
}

class PizzaFactory {
	private var _instance: BasePizza = Pizza()
	
	fun newInstance() = apply {
		_instance = Pizza()
	}
	
	fun garlic() = apply {
		_instance = Garlic(_instance)
	}
	
	fun tomato() = apply {
		_instance = Tomato(_instance)
	}
	
	fun build(): BasePizza = _instance
}

