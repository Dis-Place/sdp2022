package com.github.displace.sdp2022.database

import com.github.displace.sdp2022.util.listeners.Listener
import com.github.displace.sdp2022.util.listeners.ListenerManager
import com.google.gson.Gson
import java.lang.UnsupportedOperationException

/**
 * mock Database
 *
 * NOTE : use factory to get an instance
 *
 * @author LeoLgdr
 * @see DatabaseFactory
 * @see GoodDB
 */
class MockDB : GoodDB {
    private val contents: ObservableMultiLevelMap = ObservableMultiLevelMap()

    override fun <T> update(reference: String, newValue: T): T {
        contents.update(reference, newValue)
        return newValue
    }

    override fun delete(reference: String) {
        contents.delete(reference)
    }

    override fun <T> getThenCall(reference: String, callback: (T?) -> Unit) {
        callback(contents.get(reference) as T?)
    }

    override fun <T> runTransaction(reference: String, specification: TransactionSpecification<T>) {
        val old = contents.get(reference) as T?
        val committed = specification.preCheck(old)
        if(committed) {
            contents.update(reference, specification.computeUpdatedData(old))
        }
        specification.onCompleteCallback(committed)
    }

    override fun <T> addListener(reference: String, listener: Listener<T?>) {
        contents.addListener(reference, listener)
    }

    override fun <T> removeListener(reference: String, listener: Listener<T?>) {
        contents.removeListener(reference, listener)
    }

    fun clear() {
        contents.clear()
    }

    private class ObservableMultiLevelMap {
        private var root = ObservableNodeFactory.root()

        fun <T> get(reference: String): T? {
            return root.child(reference)?.get() as T?
        }

        fun <T> update(reference: String, newValue: T) {
            root.update(reference,newValue as Any)
        }

        fun delete(reference: String) {
            root.deleteChild(reference)
        }

        fun <T> addListener(reference: String, listener: Listener<T?>) {
            root.child(reference)?.addListener(listener)
        }

        fun <T> removeListener(reference: String, listener: Listener<T?>) {
            root.child(reference)?.removeListener(listener)
        }

        fun clear() {
            root = ObservableNodeFactory.root()
        }

        private object ObservableNodeFactory {
            val gson = Gson()
            fun root(): ObservableNode {
                /*
                this a trick because you'd expected the root to
                act as a MapNode and a mapNode should have a parent
                and I did not want parent() to be nullable
                 */
                val trueRootNode = RootNode()
                return MapNode(trueRootNode, mapOf<String,Any>())
            }

            fun get(parent: ObservableNode, value: Any): ObservableNode {
                when {
                    value::class in PRIMITIVE_TYPES -> {
                        return LeafNode(parent, value)
                    }
                    value is List<*> -> {
                        return ListNode(parent, value)
                    }
                    value is Map<*, *> -> {
                        return MapNode(parent,value.mapKeys { it.key.toString() })
                    }

                    /* please do not an actual Object in the database, there are
                     * specific restrictions to be able to convert it to json
                     * if they are met, this should work though */
                    else -> {
                        return MapNode(parent,
                            gson.fromJson(gson.toJson(value), Map::class.java)
                                .mapKeys { it.key.toString() })
                    }
                }
            }
        }

        private abstract class ObservableNode {
            private val listenerManager = ListenerManager<Any?>()
            private val listenerMap = mutableMapOf<Any,Listener<Any?>>()
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

            abstract fun deleteChild(reference: String)

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

        /**
         * true head node, you can't do anything with it
         * and it should not be accessed
         */
        private class RootNode : ObservableNode() {
            private fun unsupported(): Nothing = throw UnsupportedOperationException("root")

            override fun parent(): Nothing = unsupported()

            override fun child(reference: String): Nothing = unsupported()

            override fun get(): Nothing = unsupported()

            override fun set(reference: String, newValue: Any): Nothing = unsupported()

            override fun update(newValue: Any): Nothing = unsupported()

            override fun deleteChild(reference: String): Nothing = unsupported()

            override fun notifyChange() {}
        }

        private class MapNode(parent: ObservableNode, map: Map<String,*>): ParentedNode(parent) {
            private val children = mutableMapOf<String,ObservableNode>()

            init {
                map.mapValues {
                    update(it.key,it.value!!)
                }
            }

            override fun child(reference: String): ObservableNode? {
                return children[reference]
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
                children.remove(reference)
                notifyChange()
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

            override fun deleteChild(reference: String) {
                val index = reference.toInt()
                if(index < children.size){
                    children.removeAt(index)
                }
            }
        }

        private class LeafNode(parent: ObservableNode, private var value: Any): ParentedNode(parent) {

            private fun unsupported(): Nothing = throw UnsupportedOperationException("leaf")

            override fun get(): Any {
                return value
            }

            override fun update(newValue: Any) {
                value = newValue
                notifyChange()
            }

            override fun set(reference: String, newValue: Any): Nothing = unsupported()

            override fun child(reference: String): Nothing = unsupported()

            override fun deleteChild(reference: String): Nothing = unsupported()
        }

        companion object {
            val PRIMITIVE_TYPES = listOf<Any>(String::class, Boolean::class, Int::class, Long::class, Double::class)
        }
    }
}