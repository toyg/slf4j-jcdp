SLF4J-JCDP
========
An SLF4J wrapper for [JCDP](https://github.com/dialex/JCDP), so you can log in colour!

Note it's still pretty new and could do with better tests, but it seems to work.
You can build it with Gradle (until I figure out how to upload it).

You can even log to file  - see the [Log to file](#Log-to-file) section.


# Configuration

Debug level can be set with `jcdp.level`, using one of the acceptable SLF4J levels 
(ERROR, WARN, INFO, DEBUG, or TRACE).

You can configure the foreground and background colors for each level by 
setting any of the following System properties:

```
jcdp.ERROR.foreground=WHITE
jcdp.ERROR.background=RED
jcdp.WARN.foreground=BLACK
jcdp.WARN.background=YELLOW
jcdp.INFO.foreground=WHITE
jcdp.INFO.background=GREEN
jcdp.DEBUG.foreground=WHITE
jcdp.DEBUG.background=BLACK
jcdp.TRACE.foreground=MAGENTA
jcdp.TRACE.background=BLACK
```

Any level you don't configure will be set to foreground white and background black.


Timestamp printing can be set with `jcdp.timestamp.enabled`, using true or false.

# Log to file

If you want to *additionally* log to file, you must use:
- [my modified JCDP "FilePrinter" branch](https://github.com/toyg/JCDP/tree/FilePrinter).
- [the FilePrinter branch of this repo](https://github.com/toyg/slf4j-jcdp/tree/FilePrinter).

Hopefully at some point my pull request will be merged and all this will get easier...

If file printing is supported, you can configure it with the following properties:
```
jcdp.file.enabled=true
jcdp.file.path=/path/to/your/output.log
jcdp.file.level=INFO
```

# Contributions

Pull requests are really, really welcome.

# License

Released under the MIT license.

@ 2019 Giacomo Lacava, TarGLet Limited.

# Contact
giac at autoepm.com 