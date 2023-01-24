package command

public sealed interface Command {
    public val names: List<String>
    public val help: String
}

public interface ExecutableCommand : Command {
    public fun exec(arguments: List<String>)
}

public interface NestedHostCommand : Command {
    public val subCommands: List<Command>
}
