# Highlight

Many times I've found myself searching through many lines of text or logging
searching for a specific line or keyword. I thought there must be a way to
simply make some text stand out but after a lot of searching I couldn't find
what I wanted.

This simple command line app allows highlighting some text passed to stdin
using the ANSI escape codes that are compatible with many shells. The desired
text can be highlighted by providing a color option.

## Installation

A JDK must be installed and setup on the command line so both `java` and `javac`
can be executed.

- Clone repository to a local folder.
- Add folder of the repository to your PATH environment variable.
- Run **hlCompile**.
- Run **hl** to make sure all is well.

## Support & limitations

I've tried this with Terminal v2.6.1 on OS X El Capitan, Windows 10 Command
Prompt and Windows 10 Powershell. I've read the ANSI codes are widely supported
but sadly look like crap on Command Prompt and Powershell.

[https://en.wikipedia.org/wiki/ANSI_escape_code](https://en.wikipedia.org/wiki/ANSI_escape_code)

The way this works is it adds escaped sequences of text which is then parsed by
the Terminal which then renders the text with the desired attributes. However,
there is a known limitation which is if you use a sequence of text as the text
to highlight which matches an escaped sequence then things go awry.

## Optional environment variables

These are not mentioned in the help text but are supported:

- **HL_DEFAULT_COLOR**  
Use the named color (e.g: `red`, `blue`, etc) as the default instead of white
- **HL_ALWAYS_CASE_INSENSITIVE**  
Set to `true` or `1` to always perform case insensitive matching like the **-i**
option is always being provided. When set, the **-i** option won't even show up
in the help text!

## Usage

As this is a Java command line application and running a Java class is long and
ugly and not something you want to write each time
(e.g: `java -classpath "/path/to/repo" Highlight "$@"`), I've added a
simple script called **hl** which will do it for you which is what is referenced
in the help text.

![Usage](https://cloud.githubusercontent.com/assets/16898116/19061924/bebd88c4-89ec-11e6-8f65-a98d3e8711d4.png)
