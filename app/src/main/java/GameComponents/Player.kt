package GameComponents

//model of a player

class Player(var x:Double, var y:Double) : Coordinate{
    private var Id = 0 //An id link to an account

    override var pos: List<Double> //His localisation updated regulary
        get() = listOf(x,y)
        set(value) {x=value[0]; y=value[1]}

    fun SetId(newId: Int) { //Uniquely want the account is created or when he log in
        Id = newId
    }

    fun GetId() : Int {
        return Id
    }
}