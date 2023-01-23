package command

class CommandExecutor private constructor(
    private val commands: List<Command>,
    private val parentCommand: Command?,
    private val parentExecutor: CommandExecutor?,
) {
    constructor(commands: List<Command>) : this(
        commands = commands,
        parentCommand = null,
        parentExecutor = null
    )

    fun exec(arguments: List<String>) {
        val firstArgument = arguments.firstOrNull { it.isNotBlank() }
        val remainingArguments = arguments.drop(1)
        if (firstArgument == null) {
            println(
                commands.joinToString("\n") { command ->
                    val commandNames = command.names.joinToString(", ")

                    buildString {
                        appendLine(commandNames)
                        command.help.split("\n").forEach {
                            appendLine("\t$it")
                        }
                    }
                }
            )
            return
        }

        val targetCommand = commands.filter { command ->
            firstArgument in command.names
        }.let { matchCommand ->
            if (matchCommand.size > 1) {
                throw IllegalStateException("found multiple commands define. $matchCommand")
            } else if (matchCommand.isEmpty()) {
                val parentCommands = getParentCommandsDesc().joinToString(" ") { it.names.first() }
                System.err.println(
                    """
                        "$parentCommands" not have "$firstArgument" sub command.
                    """.trimIndent()
                )
                return
            }
            matchCommand.firstOrNull()
        } ?: TODO()

        when (targetCommand) {
            is ExecutableCommand -> {
                runCatching {
                    targetCommand.exec(remainingArguments)
                }.onFailure {
                    throw IllegalStateException(this.toString(), it)
                }
            }

            is NestedHostCommand -> {
                runCatching {
                    CommandExecutor(
                        commands = targetCommand.subCommands,
                        parentCommand = targetCommand,
                        parentExecutor = this,
                    ).exec(remainingArguments)
                }.onFailure {
                    throw IllegalStateException(this.toString(), it)
                }
            }
        }
    }

    private fun getParentCommandsDesc(): List<Command> {
        val parents = mutableListOf(parentExecutor)
        while (parents.lastOrNull() != null) {
            parents.add(parents.last()!!.parentExecutor)
        }

        return parents
            .map { it?.parentCommand }
            .plus(parentCommand)
            .reversed()
            .filterNotNull()
    }

    override fun toString(): String {
        return mapOf(
            "parentCommand" to parentCommand,
            "commands" to commands.map { it.toCustomString() },
        ).toString().let {
            "${this::class.java.simpleName}($it)"
        }
    }
}

private fun Command.toCustomString(): String {
    return listOf(
        "names" to names,
        "help" to help,
    ).plus(
        when (this) {
            is ExecutableCommand -> null
            is NestedHostCommand -> "subCommands" to subCommands.map { it.toCustomString() }
        }
    ).filterNotNull().toMap().toString()
        .let { text ->
            val className = this::class.java.simpleName.takeUnless { it.isBlank() }
                ?: when (this) {
                    is ExecutableCommand -> ExecutableCommand::class.java.simpleName
                    is NestedHostCommand -> NestedHostCommand::class.java.simpleName
                }
            "$className($text)"
        }
}
