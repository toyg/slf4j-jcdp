SLF4J-JCDP
========
[ ![Download](https://api.bintray.com/packages/autoepm/slf4j-jcdp/slf4j-jcdp/images/download.svg?version=1.0.0) ](https://bintray.com/autoepm/slf4j-jcdp/slf4j-jcdp/1.0.0/link)

An SLF4J wrapper for [JCDP](https://github.com/dialex/JCDP), so you can log in colour!

Note it's still pretty new and could do with better tests, but it seems to work.
You can even log to file  - see the [Log to file](#Log-to-file) section.

# Compatibility

* Java 8+.
* Tested with SLF4J 1.8.0. It may or may not work with earlier versions.

# Setup

This version is compatible with mainline JCDP 2.0.x, which means it can *only* log to terminal.

You will have to build it yourself. Look at build.gradle for information.

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

# Contributions

Pull requests are really, really welcome.

# License

Released under the MIT license.

@ 2019 Giacomo Lacava, TarGLet Limited.

# Contact
giac at autoepm.com 