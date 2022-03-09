package GameComponents

//definition des different event qui peuvent survenir durant une partie de versus

sealed class GameEvent{
    data class OnPointSelected(val PlayerId : Int, val test : Coordinate) : GameEvent() //Essaie d'un des deux joueur
    data class OnStart(val Goal : Coordinate, val Photo: List<Double>, val PlayerId: Int) : GameEvent()
    // Goal represent les coordonée de l'objectif et Photo represente la photo de l'objectif a trouver.
    data class OnSurrend(val PlayerId : Int) : GameEvent() //Si un des deux joueur veut arreter sans qu'aucun des deux n'est gagné
}
