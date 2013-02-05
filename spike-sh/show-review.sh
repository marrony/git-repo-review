#/bin/sh

reviews=`git rev-list refs/heads/review`

for review in $reviews; do
	git log -1 --format="%s" $review
done
