package gameVersus

import gameComponents.Coordinate
import gameComponents.GameEvent
import gameComponents.Point
import model.GameVersus

class GameVersusViewModel {

    //Test class until we got a real server side

    class ClientServerCommandTest {

        private var server = 10.0
        private val goal = Point(3.0,5.0)

        fun SetServer(serv : Double){
            server = serv
        }

        fun SendDataToOther(goal: Coordinate, photo: List<Double>, playerId: Int) {
            println("sending data")
            println("goal : x = " + goal.pos.first + " y = " + goal.pos.second)
            println("photo : " + photo[0])
            println("par joueur " + playerId)
            println("vers server : " + server)
        }

        fun GetData(uid : Int) : GameVersus {
            println("got the data")
            return GameVersus(goal,listOf(3.0,2.0,1.0),0,3,0.1)
        }

        fun increment(game: GameVersus) : GameVersus? {
            if(game.nbTry >= game.nbTryMax) {
                return null
            }
            return GameVersus(game.goal,game.Photo,(game.nbTry + 1),game.nbTryMax,game.threshold)
        }

    }

    private val reseau = ClientServerCommandTest()

    // Add the ui bidding

    private var gameState : GameVersus? = null // try MutableLiveData but without api it's quite hard

    //handle the 3 different possibility
    fun handleEvent(event: GameEvent) : Int {
        when (event) {
            is GameEvent.OnStart -> return SetGoal(event.Goal,event.Photo,event.PlayerId)
            is GameEvent.OnPointSelected -> return TryLocation(event.PlayerId, event.test)
            is GameEvent.OnSurrend -> return OnSurrend(event.PlayerId)
        }
    }

    //Set the goal at the start of the game (each player set the goal of the other)
    fun SetGoal(goal : Coordinate, photo: List<Double>, playerId : Int) : Int {
        reseau.SendDataToOther(goal,photo,playerId)
        gameState = reseau.GetData(playerId)
        return 0
    }

    //When someone tap on the screen the location he think the goal is
    fun TryLocation(UserId: Int, test : Coordinate) : Int {
        println("user : " + UserId + " tried to find the goal")
        if(gameState == null){
            println("error no game")
            println("fin du match")

            return 2
        }else {
            val test2 = gameState!!.verify(test)
            if (test2) {
                println("bravo tu as gagné")
                println("fin du match")
                return 0
            } else {
                gameState = reseau.increment(gameState!!)
                if (gameState == null) {
                    println("fin du match, tu as perdu")
                    return 2
                }
            }
        }
        return 1
    }

    //End the game and add a lose the the one who surrended
    fun OnSurrend(UserId: Int) : Int {
        println("user : " + UserId + " Surrended")
        println("fin du match")

        return 0
    }

}