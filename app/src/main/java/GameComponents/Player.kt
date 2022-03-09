package GameComponents

//modelisation d'un joueur

class Player : Coordinate{
    private var Id = 0; //Son id liée a un compte

    override var pos: List<Double> //Sa position qui devra etre updaté regulierement
        get() = pos
        set(value) {pos = value}

    fun SetId(newId: Int) { //Uniquement lors de la creation ou connection a son compte
        Id = newId
    }

    fun GetId() : Int {
        return Id
    }
}