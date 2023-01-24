package command

sealed interface Command {
    val names: List<String>
    val help: String
}

interface ExecutableCommand : Command {
    fun exec(arguments: List<String>)
}

interface NestedHostCommand : Command {
    val subCommands: List<Command>
}
