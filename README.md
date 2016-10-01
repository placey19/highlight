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

## Disclaimers & limitations

I haven't actually tried this on anything else other than Terminal v2.6.1 on
OS X El Capitan. However, I've read the ANSI codes I've used are widely
supported...

[https://en.wikipedia.org/wiki/ANSI_escape_code](https://en.wikipedia.org/wiki/ANSI_escape_code)

The way this works is it adds escaped sequences of text which is then parsed by
the Terminal which then renders the text with the desired attributes. However,
there is a known limitation which is if you use a sequence of text as the text
to highlight which matches an escaped sequence then things go awry.

Also, if you want to pipe stdin more than once like:
`cat whatever.txt | hl -r red | hl -g green` and both texts are matched on the
same line then the two instances of Highlight might clash and not have the
desired results.

## Usage

As this is a Java command line application and running a Java class is long and
ugly and not something you want to write each time
(e.g: `java -classpath "/path/to/repo" Highlight "$@"`), I've added a
simple script called **hl** which will do it for you which is what is referenced
in the help text.

```
Highlight text passed to stdin.

Usage: hl [options] text

Examples:
cat file.txt | hl -r text
adb logcat | hl -c "some logging text"

Color options:
    -a, --black         Black
    -r, --red           Red
    -g, --green         Green
    -y, --yellow        Yellow
    -b, --blue          Blue
    -p, --purple        Purple
    -c, --cyan          Cyan
    -w, --white         White (default)

Other options:
    -i, --ignore-case   Perform case insensitive matching
    -l, --line          Highlight whole line
    -u, --underline     Underline matching text
    -k, --blink         Blink matching text
    -n, --background    Highlight background of matching text instead
```
