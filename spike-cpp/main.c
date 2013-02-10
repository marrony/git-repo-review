/*
 * main.cpp
 *
 *  Created on: 07/02/2013
 *      Author: marrony
 */
#include "exec.h"
#include "git.h"

//#include <stdio.h>
//#include <stdlib.h>
//#include <unistd.h>
//#include <fcntl.h>
#include <string.h>
//#include <pthread.h>
//#include <signal.h>
//#include <errno.h>

int get_last_commit(char sha1[40]) {
	git_rev_parse("refs/heads/review", sha1);

	return strncmp(sha1, "refs/heads/review\n", 40) != 0;
}

struct review {
	char sha1[40];
};

void create_review(const char sha1[40], char sha1id[40]) {
	struct review re = {0};
	struct tree tr = {0};
	char objsha1[41] = {0};
	char parent[41] = {0};

	strncpy(re.sha1, sha1, 40);

	git_hash_object(&re, sizeof(re), objsha1);

	git_mktree_begin(&tr);
	git_mktree_add(&tr, GIT_REGULAR_NON_EXECUTABLE, "blob", objsha1, "new-review.txt");
	git_mktree_end(&tr);

	int has_parent = get_last_commit(parent);

	git_commit_tree("new-review", tr.sha1, has_parent ? parent : NULL, sha1id);
}

struct comment {
	char sha1[40];
	char message[100];
};

void add_comment(const char sha1id[40], const char* message) {
	struct comment cm = {0};
	struct tree tr = {0};
	char objsha1[41] = {0};
	char parent[41] = {0};
	char csha1[41] = {0};

	strncpy(cm.sha1, sha1id, 40);
	strncpy(cm.message, message, 100);
	git_hash_object(&cm, sizeof(cm), objsha1);

	git_mktree_begin(&tr);
	git_mktree_add(&tr, GIT_REGULAR_NON_EXECUTABLE, "blob", objsha1, "new-comment.txt");
	git_mktree_end(&tr);

	int has_parent = get_last_commit(parent);

	git_commit_tree(message, tr.sha1, has_parent ? parent : NULL, csha1);
}

int main(int argc, char* argv[]) {
//	putenv("GIT_INDEX_FILE=.git/new-index");

	char sha1id[41];

	create_review(GIT_EMPTY_TREE, sha1id);
	add_comment(sha1id, "novo comentario 1");
	add_comment(sha1id, "novo comentario 2");
	add_comment(sha1id, "novo comentario 3");

	return 0;
}
