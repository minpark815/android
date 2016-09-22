##prior-setup
if: git remote --v shows something other than "origin" such as "main"
do: git remote rm [the name of the remote such as main]

do: git remote add upstream https://github.com/NaoApp/android.git

##if your forked repo is behind the main repo

do: git pull --rebase upstream master

##if you have new updates to the code

do: git add [name of the new file]
do: git commit -m "[commit message (Present tense, first letter capital)]"
do: git push origin master

and then go to github.com and file a new pull request

##Workflow
Git does not have a standard workflow per se.
The way we will develop is as follows.

1. In your forked repo, which at this point in time should be identical to the main repo, make whatever changes you want to make.
2. After changes have been made, commit them to the master of your forked repo
3. File a pull request