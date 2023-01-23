import command.Command
import command.CommandExecutor
import command.ExecutableCommand
import command.NestedHostCommand
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class CommandParseTest : DescribeSpec({
    val defaultOut = System.out
    val defaultErr = System.err
    var outStream = ByteArrayOutputStream()
    var errStream = ByteArrayOutputStream()

    beforeEach {
        outStream = ByteArrayOutputStream()
        System.setOut(PrintStream(outStream))

        errStream = ByteArrayOutputStream()
        System.setErr(PrintStream(errStream))
    }
    afterEach {
        System.setOut(defaultOut)
        System.setErr(defaultErr)
    }

    describe("CommandParseTest") {
        context("no command specified") {
            it("show all command usage") {
                val firstCommand = object : ExecutableCommand {
                    override val names: List<String> = listOf("first")
                    override val help: String = "this is first command\nlinebreak"
                    override fun exec(arguments: List<String>) {
                        TODO("Not yet implemented")
                    }
                }
                val secondCommand = object : ExecutableCommand {
                    override val names: List<String> = listOf("second")
                    override val help: String = "this is second command"
                    override fun exec(arguments: List<String>) {
                        TODO("Not yet implemented")
                    }
                }
                val commands = listOf(
                    firstCommand,
                    secondCommand
                )
                CommandExecutor(commands).exec(listOf(""))

                outStream.toString().trimEnd()
                    .shouldBe(
                        """
                            first
                            ${TAB}this is first command
                            ${TAB}linebreak
                            
                            second
                            ${TAB}this is second command
                        """.trimIndent()
                    )
            }
        }

        context("command specified") {
            it("execute command") {
                val firstCommandPrintText = "execute first command"
                val firstCommand = object : ExecutableCommand {
                    override val names: List<String> = listOf("first")
                    override val help: String = "this is first command"

                    override fun exec(arguments: List<String>) {
                        println(firstCommandPrintText)
                    }
                }
                val secondCommand = object : ExecutableCommand {
                    override val names: List<String> = listOf("second")
                    override val help: String = "this is second command"

                    override fun exec(arguments: List<String>) {
                        TODO("Not yet implemented")
                    }
                }
                val commands = listOf(
                    firstCommand,
                    secondCommand
                )
                CommandExecutor(commands).exec(listOf("first"))

                outStream.toString().trimEnd()
                    .shouldBe(firstCommandPrintText)
            }
        }

        context("nested command") {
            context("call nested host") {
                it("show all command usage") {
                    val commandsDefine = NestedCommands()

                    CommandExecutor(commandsDefine.commands).exec(listOf("first"))

                    outStream.toString().trimEnd()
                        .shouldBe(
                            """
                                first_first
                                ${TAB}first_first help
                                
                                first_second
                                ${TAB}first_second help
                            """.trimIndent()
                        )
                }
            }
            context("call nested command") {
                it("execute nested enable command") {
                    val commandsDefine = NestedCommands()

                    CommandExecutor(commandsDefine.commands).exec(listOf("first", "first_first"))

                    outStream.toString().trimEnd()
                        .shouldBe(commandsDefine.firstCommandFirstCommandOutText)
                }

                it("execute nested not defined command") {
                    val commandsDefine = NestedCommands()

                    CommandExecutor(commandsDefine.commands).exec(listOf("first", "not_defined"))

                    errStream.toString().trimEnd()
                        .shouldBe(
                            """
                                "first" not have "not_defined" sub command.
                            """.trimIndent()
                        )
                }
            }
        }
    }
})

private const val TAB = "\t"

private class NestedCommands {
    val firstCommandFirstCommandOutText = "first\nfirst_first\nexecuted"
    val firstCommand by lazy {
        object : NestedHostCommand {
            override val names: List<String> = listOf("first")
            override val help: String = "this is first command"

            override val subCommands: List<Command> = listOf(
                firstCommandFirstCommand,
                firstCommandSecondCommand
            )
        }
    }
    val firstCommandFirstCommand by lazy {
        object : ExecutableCommand {
            override val help: String = "first_first help"
            override val names: List<String> = listOf("first_first")
            override fun exec(arguments: List<String>) {
                println(firstCommandFirstCommandOutText)
            }
        }
    }
    val firstCommandSecondCommand by lazy {
        object : ExecutableCommand {
            override val help: String = "first_second help"
            override val names: List<String> = listOf("first_second")
            override fun exec(arguments: List<String>) {
                TODO("Not yet implemented")
            }
        }
    }
    val notCalledCommand = object : ExecutableCommand {
        override val names: List<String> = listOf("not_called")
        override val help: String = "this is not_called command"

        override fun exec(arguments: List<String>) {
            TODO("Not yet implemented")
        }
    }
    val commands = listOf(
        firstCommand,
        notCalledCommand,
    )
}
