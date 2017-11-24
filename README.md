# A* in Clojure

![An image of an example render.](images/basic.png?raw=true "Render of A* being solved in Clojure.")

[A\* algorithm][a-star] implementation in [Clojure][]. Rendering is performed with [Quil][].

Built during [West London Hack Night][wlhn] on 23rd of November, 2017. Emphasis on _hack_, some parts of the code aren't very elegant.

## Usage

Once you have [leiningen][] installed, you may simply execute `lein run` in your terminal.

If you have an editor set up that allows you to interactively work with Clojure (like Emacs+CIDER, Vim+fireplace or [Cursive][]), you may simply open `src/wlhn_a_star/core.clj` and execute the code in your REPL.

Evaluating the body of `-main` will start the Quil rendering. In Emacs I use `C-c C-k` to evaluate the entire buffer which will then be re-rendered by Quil.

In [Spacemacs][] (I highly recommend this if you're getting into Clojure, the other "batteries included" solution would be [Cursive][]) you can also use bindings such as `,ee` to evaluate the previous form and observe the effects in the render window.

## Unlicenced

Find the full [unlicense][] in the `UNLICENSE` file, but here's a snippet.

>This is free and unencumbered software released into the public domain.
>
>Anyone is free to copy, modify, publish, use, compile, sell, or distribute this software, either in source code form or as a compiled binary, for any purpose, commercial or non-commercial, and by any means.

Do what you want. Learn as much as you can. Unlicense more software.

[a-star]: https://en.wikipedia.org/wiki/A*_search_algorithm
[clojure]: https://clojure.org/
[quil]: http://quil.info/
[unlicense]: http://unlicense.org/
[leiningen]: https://leiningen.org/
[spacemacs]: http://spacemacs.org/
[cursive]: https://cursive-ide.com/
