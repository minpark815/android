##prior-setup
if: git remote --v shows something other than "origin" such as "main"  
do: git remote rm [the name of the remote such as main]  

do: git remote add upstream https://github.com/NaoApp/android.git

##if your forked repo is behind the main repo

1. You have new commits:  
do: git pull --rebase upstream [name of branch]  
2. You don't have new commits:
do: git stash  
do: git checkout [name of branch]  
do: git pull upstream [name of branch]    
do: git checkout [name of branch]  
do: git stash pop  

##if you have new updates to the code

do: git add [name of the new file]  
do: git commit -m "[commit message (Present tense, first letter capital)]"  
do: git push origin [name of branch (probably master)]  
file a new pull request  

##Workflow
Git does not have a standard workflow per se.
The way we will develop is as follows.

1. In your forked repo, which at this point in time should be identical to the main repo, make whatever changes you want to make.
2. After changes have been made, commit them to the master of your forked repo
3. File a pull request

##Troubleshooting/FAQ
Q. Why are we using rebase and not merge?
A. I don't want the history to have literal thousands of meaningless merges.

Q. What does --rebase do exactly?
A. When you rebase and then commit, your commits go on top of all the other changes that happened on the remote that made your fork outdated.

Q. What does stash do exactly?
A. Stash saves your work that are not committed yet. Stash pop will restore said work.