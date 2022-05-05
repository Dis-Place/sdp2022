package com.github.displace.sdp2022.util.collections

import com.github.displace.sdp2022.database.ReferenceParser
import com.github.displace.sdp2022.util.listeners.Listener
import com.github.displace.sdp2022.util.listeners.ListenerManager
import java.lang.UnsupportedOperationException
import java.util.concurrent.locks.ReentrantLock

/**
 * observable dynamic multilevel indexing map
 *
 * @author LeoLgdr
 * @see com.github.displace.sdp2022.database.MockDB
 */
class ObservableMultiLevelMap {
    private var root = ObservableNodeFactory.root()


    fun <T> get(reference: String): T? {
        return root.child(reference)?.get() as T?
    }

    fun <T> update(reference: String, newValue: T) {
        root.update(reference,newValue as Any)
    }

    fun delete(reference: String) {
        root.updateDeleteChild(reference)
    }

    fun <T> addListener(reference: String, listener: Listener<T?>) {
        root.child(reference)?.addListener(listener)
    }

    fun <T> removeListener(reference: String, listener: Listener<T?>) {
        root.child(reference)?.removeListener(listener)
    }

    companion object {
        val PRIMITIVE_TYPES = listOf<Any>(String::class, Boolean::class, Int::class, Long::class, Double::class)
    }
}

private object ObservableNodeFactory {
    fun root(): ObservableNode {
        /*
        this a trick because you'd expected the root to
        act as a MapNode and a mapNode should have a parent
        and I did not want parent() to be nullable
         */
        val trueRootNode = object : ObservableNode() {

            override fun parent(): Nothing = throw UnsupportedOperationException("root")

            override fun child(reference: String): Nothing = throw UnsupportedOperationException("root")

            override fun get(): Nothing = throw UnsupportedOperationException("root")

            override fun set(reference: String, newValue: Any): Nothing = throw UnsupportedOperationException("root")

            override fun update(newValue: Any): Nothing = throw UnsupportedOperationException("root")

            override fun deleteChild(reference: String): Nothing = throw UnsupportedOperationException("root")

            override fun notifyChange() {}
        }
        return MapNode(trueRootNode, mapOf<String,Any>())
    }

    fun get(parent: ObservableNode, value: Any): ObservableNode {
        return when {
            value::class in ObservableMultiLevelMap.PRIMITIVE_TYPES -> {
                LeafNode(parent, value)
            }
            value is List<*> -> {
                ListNode(parent, value)
            }
            value is Map<*, *> -> {
                MapNode(parent,value.mapKeys { it.key.toString() })
            }

            else -> {
                throw UnsupportedOperationException()
            }
        }
    }
}

private abstract class ObservableNode {
    private val listenerManager = ListenerManager<Any?>()
    private val listenerMap = mutableMapOf<Any, Listener<Any?>>()
    private var isObservable = true

    abstract fun parent(): ObservableNode

    abstract fun child(reference: String): ObservableNode?

    abstract fun get(): Any

    abstract fun update(newValue: Any)

    fun update(reference: String, newValue: Any) {
        /*
        to avoid having listeners called
        while constructing the child
        we disable the observability
         */
        isObservable = false
        set(reference, newValue)
        isObservable = true
        notifyChange()
    }

    /**
     * NOTE: for the sake of my sanity please
     * don't call directly, use update
     *
     * @param reference
     * @param newValue
     * @see update
     */
    abstract fun set(reference: String, newValue: Any)

    /**
     * NOTE: for the sake of my sanity please
     * don't call directly, use updateDeleteChild
     *
     * @param reference
     * @see updateDeleteChild
     */
    abstract fun deleteChild(reference: String)

    fun updateDeleteChild(reference: String) {
        isObservable = false
        deleteChild(reference)
        isObservable = true
        notifyChange()
    }

    fun <T> addListener(listener: Listener<T?>) {
        val anyListener = Listener<Any?>{ listener.invoke(it as T?) }
        listenerManager.addCall(anyListener)
        listenerMap[listener] = anyListener
    }

    fun <T> removeListener(listener: Listener<T?>) {
        val anyListener = listenerMap[listener]
        if(anyListener!= null) {
            listenerManager.removeCall(anyListener)
            listenerMap.remove(listener)
        }
    }

    open fun notifyChange() {
        if(isObservable){
            //Thread.sleep(5000)
            listenerManager.invokeAll(get())
        }
    }
}

private abstract class ParentedNode(private val parent: ObservableNode): ObservableNode() {
    override fun parent(): ObservableNode {
        return parent
    }

    override fun notifyChange() {
        super.notifyChange()
        parent().notifyChange()
    }
}



private class MapNode(parent: ObservableNode, map: Map<String,*>): ParentedNode(parent) {
    private val children = mutableMapOf<String,ObservableNode>()

    init {
        map.mapValues {
            update(it.key,it.value!!)
        }
    }

    override fun child(reference: String): ObservableNode? {
        val parsedRef = ReferenceParser.parse(reference)
        return when {
            parsedRef.isEmpty() -> this
            parsedRef.size > 1 -> children[parsedRef[0]]?.child(parsedRef[1])
            else -> children[parsedRef[0]]
        }
    }

    override fun get(): Any {
        return children.mapValues { entry ->
            entry.value.get()
        }.toMap()
    }

    override fun update(newValue: Any) {
        children.clear()
        (newValue as Map<*,*>).map {
            update(it.key.toString(),it.value!!)
        }
        notifyChange()
    }

    override fun set(reference: String, newValue: Any) {
        val parsedRef = ReferenceParser.parse(reference)

        if(parsedRef.isEmpty()) {
            update(newValue)
            return
        }

        var child = child(parsedRef[0])

        if(child==null) {
            child = ObservableNodeFactory.get(
                this,
                if (parsedRef.size > 1) mapOf<String, Any>() else newValue
            )
            children[parsedRef[0]] = child
        }

        if(parsedRef.size == 1) {
            child.update(newValue)
        } else {
            child.update(parsedRef[1],newValue)
        }
    }

    override fun deleteChild(reference: String) {
        val parsedRef = ReferenceParser.parse(reference)
        if(parsedRef.size > 1) {
            children[parsedRef[0]]?.updateDeleteChild(parsedRef[1])
        } else {
            children.remove(reference)
        }
    }
}

private class ListNode(parent: ObservableNode, list: List<*>): ParentedNode(parent) {
    private val children = mutableListOf<ObservableNode>()

    init {
        children.addAll(list.map { ObservableNodeFactory.get(this, it!!) })
    }

    override fun child(reference: String): ObservableNode? {
        val index = reference.toInt()
        if(index < children.size) {
            return children[index]
        }
        return null
    }

    override fun get(): Any {
        return children.map { node ->
            node.get()
        }.toList()
    }

    override fun update(newValue: Any) {
        children.clear()
        children.addAll( (newValue as List<*>).map {
            ObservableNodeFactory.get(this, it!!)
        })
        notifyChange()
    }

    override fun set(reference: String, newValue: Any): Nothing = throw UnsupportedOperationException("list node")

    override fun deleteChild(reference: String): Nothing = throw UnsupportedOperationException("list node")

}

private class LeafNode(parent: ObservableNode, private var value: Any): ParentedNode(parent) {

    override fun get(): Any {
        return value
    }

    override fun update(newValue: Any) {
        value = newValue
        notifyChange()
    }

    override fun set(reference: String, newValue: Any): Nothing = throw UnsupportedOperationException("leaf")

    override fun child(reference: String): Nothing = throw UnsupportedOperationException("leaf")

    override fun deleteChild(reference: String): Nothing = throw UnsupportedOperationException("leaf")
}