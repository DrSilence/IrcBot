"#######################################
"# Additional .vimrc for Java-Project
"#

" prevent vim from changing directories:
set noautochdir

" Easy Compile Project:
map <F5> :make<CR>

" Easy Run Project with Main-Class:
map <F6> :make run TESTNAME=de.drsilence.ircbot.Main<CR>

" Easy Clean compile ouputs:
map <F7> :make clean<CR>

