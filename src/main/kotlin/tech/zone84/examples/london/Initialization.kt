package tech.zone84.examples.london

object Initialization {
    fun initialize(initializers: List<Initializer>) {
        val successful = ArrayList<Initializer>(initializers.size)
        for (initializer in initializers) {
            try {
                initializer.onStart()
                successful.add(initializer)
            } catch (exception: Exception) {
                val suppressed = ArrayList<Exception>(successful.size)
                for (rolledBack in successful.reversed()) {
                    try {
                        rolledBack.onRollback()
                    } catch (shutdownException: Exception) {
                        suppressed.add(shutdownException)
                    }
                }
                suppressed.forEach(exception::addSuppressed)
                throw exception
            }
        }
    }
}
