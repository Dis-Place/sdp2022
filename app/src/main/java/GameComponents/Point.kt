package GameComponents
//model of a point
class Point(var x: Double,var y: Double) : Coordinate {
    override var pos: List<Double>
        get() = listOf(x,y)
        set(value) {x=value[0]; y=value[1]}
}