/*
 * git.c
 *
 *  Created on: 09/02/2013
 *      Author: marrony
 */
#include "git.h"

#include <string.h>

void git_version(char version[100]) {
	FILE* fr = NULL;
	const char* args[] = {"git", "--version", NULL};

	if(execute_program(args, &fr, NULL))
		return;

	fread(version, 1, 100, fr);
	fclose(fr);
}

void git_rev_parse(const char* refspec, char sha1[40]) {
	const char* cmd[] = {"git", "rev-parse", refspec, NULL};
	FILE* fr = NULL;

	execute_program(cmd, &fr, NULL);

	fread(sha1, 1, 40, fr);
	fclose(fr);
}

void git_update_ref(const char* refspec, const char sha1[40]) {
	const char* cmd[] = {"git", "update-ref", refspec, sha1, NULL};
	execute_program(cmd, NULL, NULL);
}

void git_commit_tree(const char* message, const char tsha1[40], const char parent[40], char csha1[40]) {
	FILE* fr = NULL;
	FILE* fw = NULL;

	if(parent) {
		const char* cmd[] = {"git", "commit-tree", tsha1, "-p", parent, NULL};

		if(execute_program(cmd, &fr, &fw))
			return;
	} else {
		const char* cmd[] = {"git", "commit-tree", tsha1, NULL};

		if(execute_program(cmd, &fr, &fw))
			return;
	}

	fwrite(message, 1, strlen(message), fw);
	fclose(fw);

	fread(csha1, 1, 40, fr);
	fclose(fr);

	git_update_ref("refs/heads/review", csha1);
}

void git_hash_object(const void* bytes, int nbytes, char sha1[40]) {
	const char* cmd[] = {"git", "hash-object", "-w", "--stdin", NULL};
	FILE* fw = NULL;
	FILE* fr = NULL;

	if(execute_program(cmd, &fr, &fw))
		return;

	fwrite(bytes, 1, nbytes, fw);
	fclose(fw);

	fread(sha1, 1, 40, fr);
	fclose(fr);
}

void git_add(const char* filename) {
	const char* cmd[] = {"git", "add", filename, NULL};

	execute_program(cmd, NULL, NULL);
}

void git_write_tree(char sha1[40]) {
	const char* cmd[] = {"git", "write-tree", NULL};
	FILE* fr = NULL;

	execute_program(cmd, &fr, NULL);

	fread(sha1, 1, 40, fr);
	fclose(fr);
}

void git_mktree_begin(struct tree* t) {
	const char* cmd[] = {"git", "mktree", NULL};

	execute_program(cmd, &t->fr, &t->fw);
}

void git_mktree_add(struct tree* t, int mode, const char* type, const char sha1[40], const char* file) {
	fprintf(t->fw, "%06o %s %40s\t%s\n", mode, type, sha1, file);
}

void git_mktree_end(struct tree* t) {
	fclose(t->fw);

	fread(t->sha1, 1, 40, t->fr);
	fclose(t->fr);
}
