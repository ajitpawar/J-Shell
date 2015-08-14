## JavaShell

A simulation of Unix filesystem in Java with a Bash-like shell to interact with the virtual filesystem

Commands supported:
* get
* grep
* cat
* cd
* cp
* ls
* ln
* echo
* mkdir
* mv
* pwd
* rm

#### Usage
> javac src/shell/JShell.java <br/>
> java src/shell/JShell

Now execute bash-like commands as you normally would. For example:
> mkdir test <br/>
> cd test <br/>
> get http://textfiles.com/computers/chaos.txt <br/>
> ls <br/>
> cat chaos.txt <br/>
> exit
