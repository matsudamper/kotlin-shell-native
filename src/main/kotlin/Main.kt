import command.Command
import command.CommandExecutor
import command.ExecutableCommand
import command.NestedHostCommand
import java.io.File

fun main(args: Array<String>) {
    CommandExecutor(
        listOf(
            object : ExecutableCommand {
                override val names: List<String> = listOf("ls")
                override val help: String = "list file and directories"
                override fun exec(arguments: List<String>) {
                    arguments.joinToString {
                        val files = File(it).listFiles()

                        buildString {
                            appendLine(it)
                            appendLine(files.joinToString { file -> file.name })
                        }
                    }.let { println(it) }
                }
            },
            object : NestedHostCommand {
                override val names: List<String> = listOf("main")
                override val help: String = "main command"
                override val subCommands: List<Command> = listOf(
                    object : ExecutableCommand {
                        override val names: List<String> = listOf("sub1")
                        override val help: String = "sub command1"
                        override fun exec(arguments: List<String>) {
                            println("exec sub command1")
                        }
                    },
                    object : ExecutableCommand {
                        override val names: List<String> = listOf("sub2")
                        override val help: String = "sub command2"
                        override fun exec(arguments: List<String>) {
                            println("exec sub command2")
                        }
                    }
                )
            }
        )
    ).exec(args.toList())
}
