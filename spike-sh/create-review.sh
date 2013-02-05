#/bin/sh

if [ $# != 1 ]; then
	echo 'Falta um argumento'
	exit 1
fi

export GIT_INDEX_FILE=.git/new-index

parent=`git rev-parse refs/heads/review 2> /dev/null`

#echo teset $parent

#do something
#git add .
#tsha=`git write-tree`
#csha=`echo 'new review' | git commit-tree $tsha`

# empty tree
tsha=`git hash-object -t tree /dev/null`

if [ $parent != 'refs/heads/review' ]; then
	csha=`echo $1 | git commit-tree $tsha -p $parent`
else
	csha=`echo $1 | git commit-tree $tsha`
fi

#echo $csha

git update-ref refs/heads/review $csha

#git mktree para criar uma tree


