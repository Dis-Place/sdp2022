package GameComponents

//model of a player

class Player : Coordinate{
    private var Id = 0; //An id link to an account

    override var pos: List<Double> //His localisation updated regulary
        get() = pos
        set(value) {pos = value}

    fun SetId(newId: Int) { //Uniquely want the account is created or when he log in
        Id = newId
    }

    fun GetId() : Int {
        return Id
    }
}