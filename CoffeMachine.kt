package machine


fun main() {
    val coffeeMachine = CoffeeMachine(400, 540, 120, 9, 550)
    do {
        val result = coffeeMachine.processInput(readln())
    } while (result)
}

class CoffeeMachine(
    private var water: Int,
    private var milk: Int,
    private var beans: Int,
    private var cups: Int,
    private var cash: Int
) {
    private var state = State.MainMenu

    init {
        printMenu(state)
    }

    fun processInput(input: String): Boolean {
        state = when (state) {
            State.MainMenu -> processMainMenuInput(input)
            State.ChooseBeverage -> buyBeverage(input)
            State.RefillWater -> fillWater(input)
            State.RefillMilk -> fillMilk(input)
            State.RefillCoffee -> fillCoffee(input)
            State.RefillCups -> fillCups(input)
            else -> State.MainMenu
        }
        printMenu(state)
        return state != State.Exit
    }

    private fun printMenu(state: State) {
        when (state) {
            State.MainMenu -> print("Write action (buy, fill, take, remaining, exit): ")
            State.ChooseBeverage -> print("\nWhat do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ")
            State.Remaining -> println(
                """
                                        |
                                        |The coffee machine has:
                                        |$water ml of water
                                        |$milk ml of milk
                                        |$beans g of coffee beans
                                        |$cups disposable cups
                                        |$$cash of money
                                        |
                                        """.trimMargin()
            )
            State.RefillWater -> print("\nWrite how many ml of water do you want to add: ")
            State.RefillMilk -> print("Write how many ml of milk do you want to add: ")
            State.RefillCoffee -> print("Write how many grams of coffee beans do you want to add: ")
            State.RefillCups -> print("Write how many disposable cups of coffee do you want to add: ")
            State.Take -> println("\nI gave you \$${cash}\n")
            State.InvalidInput -> println("Invalid input")
            State.Exit -> print("")
        }
    }

    private fun processMainMenuInput(input: String): State {
        return when (input) {
            "buy" -> State.ChooseBeverage
            "fill" -> State.RefillWater
            "take" -> {
                printMenu(State.Take)
                cash = 0
                State.MainMenu
            }
            "remaining" -> {
                printMenu(State.Remaining)
                State.MainMenu
            }
            "exit" -> State.Exit
            else -> {
                printMenu(State.InvalidInput)
                State.MainMenu
            }
        }
    }

    private fun buyBeverage(input: String): State {
        return when (input) {
            "1" -> prepareCoffee(Drinks.Espresso)
            "2" -> prepareCoffee(Drinks.Latte)
            "3" -> prepareCoffee(Drinks.Cappuccino)
            "back" -> State.MainMenu
            else -> State.ChooseBeverage
        }
    }

    private fun prepareCoffee(beverage: Drinks): State {
        if (checkIngredients(beverage)) {
            println("I have enough resources, making you a coffee!\n")
            makeCoffee(beverage)
        }
        return State.MainMenu
    }

    private fun checkIngredients(beverage: Drinks): Boolean {
        val missingIngredients = mutableListOf<String>()
        when {
            water < beverage.water -> missingIngredients.add("water")
            milk < beverage.milk -> missingIngredients.add("milk")
            beans < beverage.beans -> missingIngredients.add("coffee beans")
            cups <= 0 -> missingIngredients.add("cups")
        }
        if (missingIngredients.size > 0) {
            println("Sorry, not enough ${missingIngredients.joinToString(",")}!")
        }
        return missingIngredients.size == 0
    }

    private fun makeCoffee(beverage: Drinks) {
        water -= beverage.water
        milk -= beverage.milk
        beans -= beverage.beans
        cups--
        cash += beverage.price
    }

    private fun fillCups(input: String): State {
        val cupsAdded = input.toInt()
        cups += cupsAdded
        println()
        return State.MainMenu
    }

    private fun fillCoffee(input: String): State {
        val coffeeAdded = input.toInt()
        beans += coffeeAdded
        return State.RefillCups
    }

    private fun fillMilk(input: String): State {
        val milkAdded = input.toInt()
        milk += milkAdded
        return State.RefillCoffee
    }

    private fun fillWater(input: String): State {
        val waterAdded = input.toInt()
        water += waterAdded
        return State.RefillMilk
    }

    companion object {
        enum class State {
            MainMenu,
            ChooseBeverage,
            RefillWater,
            RefillMilk,
            RefillCoffee,
            RefillCups,
            Remaining,
            Take,
            InvalidInput,
            Exit
        }

        enum class Drinks(
            val water: Int,
            val milk: Int,
            val beans: Int,
            val price: Int
        ) {
            Espresso(250, 0, 16, 4),
            Latte(350, 75, 20, 7),
            Cappuccino(200, 100, 12, 6)
        }
    }
}
