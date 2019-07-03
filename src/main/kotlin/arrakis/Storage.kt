package arrakis

abstract class Storage() {

    abstract fun writeToStorage(obj: kotlin.Any)

    abstract fun readFromStorge(obj: kotlin.Any)

    abstract fun deleteFromStorage(obj: kotlin.Any)

    abstract fun updateFile(obj: kotlin.Any)

}