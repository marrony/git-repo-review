/*
 * main.cpp
 *
 *  Created on: 07/02/2013
 *      Author: marrony
 */

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <pthread.h>
#include <signal.h>
#include <errno.h>

#ifdef WIN32
#include <io.h>
#include <process.h>
#endif

#define READ 0
#define WRITE 1

#define PARENT_READ  read_pipe[READ]
#define CHILD_WRITE  read_pipe[WRITE]

#define CHILD_READ   write_pipe[READ]
#define PARENT_WRITE write_pipe[WRITE]

#ifndef pipe
int pipe(int fd[2]) {
	return _pipe(fd, 256, O_BINARY | O_NOINHERIT);
}
#endif

int execute_program(const char* const* argv, FILE** fr, FILE** fw) {
	int read_pipe[2];
	int write_pipe[2];

	pipe(read_pipe);
	pipe(write_pipe);

	int fdstdin = dup(STDIN_FILENO);
	int fdstdout = dup(STDOUT_FILENO);

	if(dup2(CHILD_READ, STDIN_FILENO) < 0)
		return 1;

	if(dup2(CHILD_WRITE, STDOUT_FILENO) < 0)
		return 1;

	close(CHILD_READ);
	close(CHILD_WRITE);

#ifdef WIN32
	int id = _spawnvp(P_NOWAIT, argv[0], (const char* const*)argv);
#else
	int id = fork();

	if(id == 0) {
		execvp(argv[0], argv);
		exit(0);
	}
#endif

	if(id < 0)
		return 1;

	if(dup2(fdstdin, STDIN_FILENO) < 0)
		return 1;

	if(dup2(fdstdout, STDOUT_FILENO) < 0)
		return 1;

	close(fdstdin);
	close(fdstdout);

	if(fr)
		*fr = fdopen(PARENT_READ, "r");
	else
		close(PARENT_READ);

	if(fw)
		*fw = fdopen(PARENT_WRITE, "w");
	else
		close(PARENT_WRITE);

	return 0;
}

int git_version() {
	char bytes[256+1];
	int nbytes;
	FILE* fr = NULL;
	const char* args[] = {"git", "--version", NULL};

	if(execute_program(args, &fr, NULL))
		return 1;

	while(!feof(fr)) {
		nbytes = fread(bytes, 1, 256, fr);

		if(nbytes == 0)
			break;

		bytes[nbytes] = 0;

		printf("%s", bytes);
	}

	if(fr) fclose(fr);

	return 0;
}

void rev_parse(const char* refspec, char sha1[40]) {
	const char* cmd[] = {"git", "rev-parse", refspec, NULL};
	FILE* fr = NULL;

	execute_program(cmd, &fr, NULL);

	fread(sha1, 1, 40, fr);
	fclose(fr);
}

int get_last_commit(char sha1[40]) {
	rev_parse("refs/heads/review", sha1);

	return strncmp(sha1, "refs/heads/review\n", 40) != 0;
}

void update_ref(const char* refspec, const char* sha1) {
	const char* cmd[] = {"git", "update-ref", refspec, sha1, NULL};
	execute_program(cmd, NULL, NULL);
}

void commit_tree(const char* message, const char* tsha1, const char* parent, char csha1[40]) {
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

	update_ref("refs/heads/review", csha1);
}

struct review {
	char sha1[40];
};

void create_review(const char* sha1) {

}

#define EMPTY_TREE "4b825dc642cb6eb9a060e54bf8d69288fbee4904"

int main(int argc, char* argv[]) {
	char parent[41] = {0};
	char csha1[41] = {0};

	int has_parent = get_last_commit(parent);

	commit_tree("teste de commit", EMPTY_TREE, has_parent ? parent : NULL, csha1);
	printf("%s", csha1);

	return 0;
}
