#/bin/sh

reviews=`git rev-list refs/heads/review`

for review in $reviews; do
	git log --format="%s" $review
done
