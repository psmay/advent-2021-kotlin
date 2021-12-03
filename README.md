advent-2021-kotlin (Or: How I solved Advent of Code 2021 puzzles in Kotlin)
===========================================================================

These are my solutions to the puzzles of [Advent of
Code](https://adventofcode.com/) 2021 as I get acquainted with Kotlin
and IDEA.

Why Kotlin/IDEA?
----------------

At work, I use Visual Studio and C#, and I'm really fond of LINQ, but I
don't use Windows at home, every time I try to get something C#-based
going at home, using VS Code, I despair; the experience leaves much to
be desired. So I thought I'd use Advent as an excuse to get friendly
with something new. (But don't be surprised if my work shows a bit of
C#-esqueness—or maybe even Perlishness.)

So far, by comparison to C# with VS Code, IDEA is killing it. By
comparison to C# and actual Visual Studio (which, if you haven't tried
it, don't, because it is really, really good and in no way free or
sustainable), there are omissions, but I'm finding it pretty competent
and probably something I could use for an actual project.

No to stdio, yes to unit tests
------------------------------

Instead of using the ordinary template, I'm approaching this as if I'm
pretending to write a library and using unit tests to check everything.
The main code itself won't interact directly with stdio.

This code is intended to be stringlyphobic. To the extent possible, the
solutions will accept input and produce output that is already
domain-meaningful. The functions are not likely to accept a `String` or
a list of `String` directly unless it really makes sense for them to do
so. (The day 2 test is already a really good example of this; its input
is in the form of a data class with an enum parameter.) If possible, the
parsing is done within the test module, though it may call into the main
code for some help parsing. (The day 2 test also does this.)

Where should I put the test input?
----------------------------------

This being test input, it belongs with the tests, not the main code.

All test input should be named and placed within the project at
`src/test/resources/y2021/DayXXInput.txt`, where `XX` is the day number.

Share and enjoy
---------------

> advent-2021-kotlin - Puzzle solutions for Advent of Code 2021
> implemented in Kotlin
>
> Written in 2021 by Peter S. May <https://psmay.com/>
>
> To the extent possible under law, the author(s) have dedicated all
> copyright and related and neighboring rights to this software to the
> public domain worldwide. This software is distributed without any
> warranty.
>
> You should have received a copy of the CC0 Public Domain Dedication
> along with this software. If not, see
> <http://creativecommons.org/publicdomain/zero/1.0/>.
