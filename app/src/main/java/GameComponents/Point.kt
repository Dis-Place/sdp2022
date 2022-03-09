package GameComponents

class Point(val x: Double,val y: Double) : Coordinate {
    override var pos: List<Double>
        get() = listOf(x,y)
        set(value) {}
}